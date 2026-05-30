-- ============================================================
-- Hopper Backend: Initial Schema
-- Supabase PostgreSQL migration
-- ============================================================

-- Enable required extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "postgis";

-- ============================================================
-- PANDALS
-- ============================================================
CREATE TABLE pandals (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    name_bengali TEXT,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    city TEXT NOT NULL DEFAULT 'Kolkata',
    neighborhood TEXT,
    festival TEXT NOT NULL CHECK (festival IN ('DURGA_PUJA', 'JAGADDHATRI_PUJA')),
    year INTEGER NOT NULL,
    theme TEXT,
    committee_name TEXT,
    established_year INTEGER,
    artisan_credits_json JSONB DEFAULT '[]'::jsonb,
    awards TEXT[] DEFAULT '{}',
    photos TEXT[] DEFAULT '{}',
    significance_rank INTEGER DEFAULT 0,
    source_type TEXT NOT NULL DEFAULT 'UNKNOWN' CHECK (source_type IN ('COMMITTEE', 'VOLUNTEER', 'SCRAPED', 'UNKNOWN')),
    confidence_level TEXT NOT NULL DEFAULT 'LOW' CHECK (confidence_level IN ('HIGH', 'MEDIUM', 'LOW')),
    location GEOGRAPHY(Point, 4326) GENERATED ALWAYS AS (
        ST_SetSRID(ST_MakePoint(longitude, latitude), 4326)::geography
    ) STORED,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_pandals_festival_year ON pandals(festival, year);
CREATE INDEX idx_pandals_location ON pandals USING GIST(location);
CREATE INDEX idx_pandals_significance ON pandals(significance_rank);

-- ============================================================
-- CROWD REPORTS
-- ============================================================
CREATE TABLE crowd_reports (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    pandal_id TEXT NOT NULL REFERENCES pandals(id) ON DELETE CASCADE,
    bucket TEXT NOT NULL CHECK (bucket IN ('GREEN', 'YELLOW', 'RED')),
    device_hash TEXT NOT NULL,
    reported_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMPTZ NOT NULL,
    weight_multiplier DOUBLE PRECISION DEFAULT 1.0
);

CREATE INDEX idx_crowd_reports_pandal_active ON crowd_reports(pandal_id, expires_at DESC);
CREATE INDEX idx_crowd_reports_device ON crowd_reports(device_hash, pandal_id, reported_at DESC);

-- ============================================================
-- CALENDAR (TITHI)
-- ============================================================
CREATE TABLE calendar_tithis (
    id TEXT PRIMARY KEY,
    festival TEXT NOT NULL CHECK (festival IN ('DURGA_PUJA', 'JAGADDHATRI_PUJA')),
    year INTEGER NOT NULL,
    name TEXT NOT NULL,
    name_bengali TEXT,
    date DATE NOT NULL,
    cultural_significance TEXT,
    is_peak_crowd BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_tithis_festival_year ON calendar_tithis(festival, year);

-- ============================================================
-- ARTISTS
-- ============================================================
CREATE TABLE artists (
    id TEXT PRIMARY KEY DEFAULT uuid_generate_v4()::text,
    name TEXT NOT NULL,
    name_bengali TEXT,
    specialty TEXT,
    pandal_ids TEXT[] DEFAULT '{}',
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- ============================================================
-- EXIT NODES
-- ============================================================
CREATE TABLE exit_nodes (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    name_bengali TEXT,
    category TEXT NOT NULL CHECK (category IN ('METRO', 'RAILWAY', 'BUS', 'FERRY', 'POLICE', 'MEDICAL', 'FIRE')),
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    contact_number TEXT,
    is_24hr BOOLEAN DEFAULT FALSE,
    is_well_lit BOOLEAN DEFAULT FALSE,
    location GEOGRAPHY(Point, 4326) GENERATED ALWAYS AS (
        ST_SetSRID(ST_MakePoint(longitude, latitude), 4326)::geography
    ) STORED,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_exit_nodes_category ON exit_nodes(category);
CREATE INDEX idx_exit_nodes_location ON exit_nodes USING GIST(location);

-- ============================================================
-- CONNECTORS (walking paths between pandals/exit nodes)
-- ============================================================
CREATE TABLE connectors (
    id TEXT PRIMARY KEY,
    from_node_id TEXT NOT NULL,
    to_node_id TEXT NOT NULL,
    distance_meters DOUBLE PRECISION NOT NULL,
    is_well_lit BOOLEAN DEFAULT FALSE,
    is_accessible BOOLEAN DEFAULT FALSE,
    geometry JSONB, -- GeoJSON LineString
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_connectors_from ON connectors(from_node_id);
CREATE INDEX idx_connectors_to ON connectors(to_node_id);

-- ============================================================
-- REPUTATION (device-level trust scores)
-- ============================================================
CREATE TABLE reputation (
    device_hash TEXT PRIMARY KEY,
    total_reports INTEGER DEFAULT 0,
    accurate_reports INTEGER DEFAULT 0,
    accuracy DOUBLE PRECISION DEFAULT 0.0,
    weight_multiplier DOUBLE PRECISION DEFAULT 1.0,
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- ============================================================
-- HISTORICAL CROWD PATTERNS
-- ============================================================
CREATE TABLE historical_crowd_patterns (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    pandal_id TEXT NOT NULL REFERENCES pandals(id) ON DELETE CASCADE,
    festival TEXT NOT NULL,
    year INTEGER NOT NULL,
    day_name TEXT NOT NULL,
    hour INTEGER NOT NULL CHECK (hour >= 0 AND hour <= 23),
    avg_bucket TEXT NOT NULL CHECK (avg_bucket IN ('GREEN', 'YELLOW', 'RED')),
    sample_count INTEGER DEFAULT 0,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_historical_pandal ON historical_crowd_patterns(pandal_id, festival, year);

-- ============================================================
-- LOST PERSONS
-- ============================================================
CREATE TABLE lost_persons (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    description TEXT NOT NULL,
    last_seen_pandal_id TEXT REFERENCES pandals(id),
    last_seen_at TIMESTAMPTZ,
    contact_number TEXT NOT NULL,
    device_hash TEXT NOT NULL,
    is_resolved BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    expires_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_lost_persons_active ON lost_persons(is_resolved, expires_at DESC);

-- ============================================================
-- FUNCTIONS
-- ============================================================

-- Function to get aggregated crowd for a pandal (weighted median)
CREATE OR REPLACE FUNCTION get_aggregated_crowd(p_pandal_id TEXT)
RETURNS TABLE(pandal_id TEXT, bucket TEXT, report_count BIGINT) AS $$
DECLARE
    total_weight DOUBLE PRECISION := 0;
    median_pos DOUBLE PRECISION;
    cumulative DOUBLE PRECISION := 0;
    result_bucket TEXT := 'GREEN';
    r RECORD;
BEGIN
    -- Get active (non-expired) reports with weights
    FOR r IN
        SELECT cr.bucket AS b, COALESCE(cr.weight_multiplier, 1.0) AS w
        FROM crowd_reports cr
        WHERE cr.pandal_id = p_pandal_id
          AND cr.expires_at > NOW()
        ORDER BY 
            CASE cr.bucket 
                WHEN 'GREEN' THEN 0 
                WHEN 'YELLOW' THEN 1 
                WHEN 'RED' THEN 2 
            END
    LOOP
        total_weight := total_weight + r.w;
    END LOOP;

    IF total_weight = 0 THEN
        RETURN QUERY SELECT p_pandal_id, 'GREEN'::TEXT, 0::BIGINT;
        RETURN;
    END IF;

    median_pos := total_weight / 2.0;

    FOR r IN
        SELECT cr.bucket AS b, COALESCE(cr.weight_multiplier, 1.0) AS w
        FROM crowd_reports cr
        WHERE cr.pandal_id = p_pandal_id
          AND cr.expires_at > NOW()
        ORDER BY 
            CASE cr.bucket 
                WHEN 'GREEN' THEN 0 
                WHEN 'YELLOW' THEN 1 
                WHEN 'RED' THEN 2 
            END
    LOOP
        cumulative := cumulative + r.w;
        IF cumulative >= median_pos THEN
            result_bucket := r.b;
            EXIT;
        END IF;
    END LOOP;

    RETURN QUERY 
        SELECT p_pandal_id, result_bucket, 
               (SELECT COUNT(*) FROM crowd_reports cr2 
                WHERE cr2.pandal_id = p_pandal_id AND cr2.expires_at > NOW());
END;
$$ LANGUAGE plpgsql;

-- Function to check rate limiting (10 min per device per pandal)
CREATE OR REPLACE FUNCTION is_rate_limited(p_device_hash TEXT, p_pandal_id TEXT)
RETURNS BOOLEAN AS $$
BEGIN
    RETURN EXISTS (
        SELECT 1 FROM crowd_reports
        WHERE device_hash = p_device_hash
          AND pandal_id = p_pandal_id
          AND reported_at > NOW() - INTERVAL '10 minutes'
    );
END;
$$ LANGUAGE plpgsql;

-- Trigger to auto-update updated_at
CREATE OR REPLACE FUNCTION update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER pandals_updated_at
    BEFORE UPDATE ON pandals
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();

CREATE TRIGGER reputation_updated_at
    BEFORE UPDATE ON reputation
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();
