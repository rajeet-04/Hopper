# Hopper — Festival Atlas

Offline-first Android app for navigating Durga Puja (Kolkata) and Jagaddhatri Puja (Chandannagar) pandals with real-time crowd intelligence, safety routing, and cultural context.

## Features

- **Interactive Map** — MapLibre GL Native with pandal pins color-coded by live crowd levels (Green/Yellow/Red)
- **Crowd Reporting** — Privacy-preserving crowd submissions using device hash (no PII), weighted median aggregation, 10-min rate limiting
- **Exit Routing** — Nearest Metro, Railway, Police, Medical exit points with well-lit path preference
- **Festival Toggle** — Switch between Durga Puja and Jagaddhatri Puja with automatic date-based detection
- **Night Safety Mode** — High-contrast dark map style with enhanced emergency pin visibility
- **Offline-First** — Bundled GeoJSON data, Room database, works without internet
- **Background Sync** — WorkManager-based periodic sync and crowd report upload
- **Itinerary Planner** — Build custom pandal-hopping routes
- **Cultural Context** — Tithi calendar, ritual guides, oral histories, artisan credits
- **Community** — Lost person alerts, volunteer coordination, bhog (food) pins

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Android App                           │
│  Compose UI → ViewModel → UseCase → Repository → Room   │
│                                          ↕              │
│                                    WorkManager          │
└──────────────────────────┬──────────────────────────────┘
                           │ HTTPS
                           ▼
┌──────────────────────────────────────────────────────────┐
│           Cloudflare Worker (API Gateway)                 │
│  Rate limiting · Validation · Response formatting        │
│  https://hopper-api.mr-harrycarter1999.workers.dev       │
└──────────────────────────┬───────────────────────────────┘
                           │ PostgREST
                           ▼
┌──────────────────────────────────────────────────────────┐
│              Supabase (PostgreSQL)                        │
│  Tables · RLS · RPC Functions · Realtime · pg_cron       │
│  Region: ap-south-1 (Mumbai)                             │
└──────────────────────────────────────────────────────────┘
```

## Tech Stack

| Layer | Technology |
|-------|-----------|
| UI | Jetpack Compose, Material 3 |
| Navigation | Navigation Compose |
| DI | Hilt |
| Local DB | Room (SQLite) |
| Map | MapLibre GL Native |
| Networking | Retrofit + kotlinx.serialization |
| Background | WorkManager |
| Preferences | DataStore |
| API Gateway | Cloudflare Workers (TypeScript) |
| Database | Supabase PostgreSQL + PostGIS |
| Realtime | Supabase Realtime |

## Project Structure

```
hopper/
├── app/                          # Android application
│   ├── src/main/
│   │   ├── assets/               # Bundled GeoJSON & calendar data
│   │   └── java/com/example/hopper/
│   │       ├── data/             # Repository implementations, Room, API
│   │       ├── di/               # Hilt modules
│   │       ├── domain/           # Models, use cases, repository interfaces
│   │       ├── ui/               # Compose screens & ViewModels
│   │       └── util/             # Haversine, DeviceHash, etc.
│   └── build.gradle.kts
├── backend/
│   ├── worker/                   # Cloudflare Worker (API gateway)
│   │   ├── src/
│   │   │   ├── index.ts          # Router
│   │   │   ├── routes/           # pandals, crowd, calendar, artists
│   │   │   └── utils/            # supabase client, cors, rate limiting
│   │   ├── wrangler.toml
│   │   └── package.json
│   └── supabase/
│       └── migrations/           # SQL schema, RLS, seed data, cron
├── local.properties              # Secrets (gitignored)
├── local.properties.example      # Template for new developers
└── gradle/                       # Version catalog
```

## Getting Started

### Prerequisites

- Android Studio Ladybug+ (or IntelliJ with Android plugin)
- JDK 11+
- Node.js 18+ (for backend)

### 1. Clone & Configure

```bash
git clone <repo-url>
cd hopper
cp local.properties.example local.properties
```

Edit `local.properties` with your credentials:

```properties
api.base.url=https://hopper-api.mr-harrycarter1999.workers.dev/
supabase.url=https://<your-ref>.supabase.co
supabase.anon.key=<your-anon-key>
map.style.url=https://demotiles.maplibre.org/style.json
map.night.style.url=https://demotiles.maplibre.org/style.json
map.tiles.url=https://demotiles.maplibre.org/tiles/tiles.json
```

### 2. Build the App

```bash
./gradlew assembleRelease
```

Output APKs (R8 minified, ABI-split):
- `app-arm64-v8a-release.apk` — ~13 MB (modern phones)
- `app-armeabi-v7a-release.apk` — ~10 MB (older phones)

### 3. Backend (already deployed)

The backend is live. To redeploy after changes:

```bash
cd backend/worker
npm install
wrangler deploy
```

Secrets are managed via:
```bash
wrangler secret put SUPABASE_URL
wrangler secret put SUPABASE_SERVICE_KEY
```

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/health` | Health check |
| GET | `/v1/pandals?festival=DURGA_PUJA&year=2026` | List pandals |
| GET | `/v1/crowd/{pandalId}` | Aggregated crowd level |
| POST | `/v1/crowd` | Submit crowd report |
| GET | `/v1/calendar?festival=DURGA_PUJA&year=2026` | Tithi calendar |
| GET | `/v1/artists` | Artist directory |

## Privacy & Security

- **No PII collected** — Crowd reports use SHA-256 device hash only
- **RLS enforced** — Row Level Security on all Supabase tables
- **Secrets in local.properties** — Gitignored, never committed
- **Service key server-side only** — Only the Cloudflare Worker holds the service_role key
- **Rate limiting** — IP-level (30/min) + device-level (1 per 10 min per pandal)
- **R8 obfuscation** — Release builds are fully obfuscated

## Cost

| Service | Tier | Monthly Cost |
|---------|------|-------------|
| Supabase | Free | $0 |
| Cloudflare Workers | Free | $0 |
| **Total** | | **$0** |

## License

Private — All rights reserved.
