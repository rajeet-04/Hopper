-- ============================================================
-- Row Level Security Policies
-- ============================================================

-- Enable RLS on all tables
ALTER TABLE pandals ENABLE ROW LEVEL SECURITY;
ALTER TABLE crowd_reports ENABLE ROW LEVEL SECURITY;
ALTER TABLE calendar_tithis ENABLE ROW LEVEL SECURITY;
ALTER TABLE artists ENABLE ROW LEVEL SECURITY;
ALTER TABLE exit_nodes ENABLE ROW LEVEL SECURITY;
ALTER TABLE connectors ENABLE ROW LEVEL SECURITY;
ALTER TABLE reputation ENABLE ROW LEVEL SECURITY;
ALTER TABLE historical_crowd_patterns ENABLE ROW LEVEL SECURITY;
ALTER TABLE lost_persons ENABLE ROW LEVEL SECURITY;

-- ============================================================
-- PANDALS: Public read, service-role write
-- ============================================================
CREATE POLICY "pandals_select_all" ON pandals
    FOR SELECT USING (true);

CREATE POLICY "pandals_insert_service" ON pandals
    FOR INSERT WITH CHECK (auth.role() = 'service_role');

CREATE POLICY "pandals_update_service" ON pandals
    FOR UPDATE USING (auth.role() = 'service_role');

-- ============================================================
-- CROWD REPORTS: Public insert (via worker), public read active
-- ============================================================
CREATE POLICY "crowd_reports_select_active" ON crowd_reports
    FOR SELECT USING (expires_at > NOW());

CREATE POLICY "crowd_reports_insert_all" ON crowd_reports
    FOR INSERT WITH CHECK (true);

CREATE POLICY "crowd_reports_delete_service" ON crowd_reports
    FOR DELETE USING (auth.role() = 'service_role');

-- ============================================================
-- CALENDAR: Public read, service-role write
-- ============================================================
CREATE POLICY "calendar_select_all" ON calendar_tithis
    FOR SELECT USING (true);

CREATE POLICY "calendar_insert_service" ON calendar_tithis
    FOR INSERT WITH CHECK (auth.role() = 'service_role');

-- ============================================================
-- ARTISTS: Public read, service-role write
-- ============================================================
CREATE POLICY "artists_select_all" ON artists
    FOR SELECT USING (true);

CREATE POLICY "artists_insert_service" ON artists
    FOR INSERT WITH CHECK (auth.role() = 'service_role');

-- ============================================================
-- EXIT NODES: Public read, service-role write
-- ============================================================
CREATE POLICY "exit_nodes_select_all" ON exit_nodes
    FOR SELECT USING (true);

CREATE POLICY "exit_nodes_insert_service" ON exit_nodes
    FOR INSERT WITH CHECK (auth.role() = 'service_role');

-- ============================================================
-- CONNECTORS: Public read, service-role write
-- ============================================================
CREATE POLICY "connectors_select_all" ON connectors
    FOR SELECT USING (true);

CREATE POLICY "connectors_insert_service" ON connectors
    FOR INSERT WITH CHECK (auth.role() = 'service_role');

-- ============================================================
-- REPUTATION: Service-role only
-- ============================================================
CREATE POLICY "reputation_service_only" ON reputation
    FOR ALL USING (auth.role() = 'service_role');

-- ============================================================
-- HISTORICAL PATTERNS: Public read, service-role write
-- ============================================================
CREATE POLICY "historical_select_all" ON historical_crowd_patterns
    FOR SELECT USING (true);

CREATE POLICY "historical_insert_service" ON historical_crowd_patterns
    FOR INSERT WITH CHECK (auth.role() = 'service_role');

-- ============================================================
-- LOST PERSONS: Public read active, public insert, owner resolve
-- ============================================================
CREATE POLICY "lost_persons_select_active" ON lost_persons
    FOR SELECT USING (is_resolved = FALSE AND expires_at > NOW());

CREATE POLICY "lost_persons_insert_all" ON lost_persons
    FOR INSERT WITH CHECK (true);

CREATE POLICY "lost_persons_update_service" ON lost_persons
    FOR UPDATE USING (auth.role() = 'service_role');
