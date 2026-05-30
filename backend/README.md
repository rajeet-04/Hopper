# Hopper Backend

Festival Atlas backend powered by **Supabase** (PostgreSQL + PostgREST) and **Cloudflare Workers** (API gateway).

## Architecture

```
Android App → Cloudflare Worker (API Gateway) → Supabase (PostgreSQL)
```

### Why this stack?
- **Supabase Free Tier**: 500MB Postgres, 2GB bandwidth, 50K MAU, Realtime subscriptions
- **Cloudflare Workers Free Tier**: 100K requests/day, global edge deployment
- **$0/month** for a festival app with seasonal traffic spikes

### Components

| Component | Purpose | Location |
|-----------|---------|----------|
| Supabase | PostgreSQL DB + Row Level Security | `supabase/` |
| Cloudflare Worker | API gateway, rate limiting, caching | `worker/` |

## Setup

### 1. Supabase
1. Create a project at [supabase.com](https://supabase.com)
2. Run migrations: `supabase db push` (or paste SQL from `supabase/migrations/`)
3. Copy your project URL and anon/service keys

### 2. Cloudflare Worker
1. Install Wrangler: `npm install -g wrangler`
2. Login: `wrangler login`
3. Set secrets:
   ```bash
   wrangler secret put SUPABASE_URL
   wrangler secret put SUPABASE_SERVICE_KEY
   ```
4. Deploy: `wrangler deploy`

### 3. Android App
Update `NetworkModule.kt` BASE_URL to your Worker URL:
```kotlin
private const val BASE_URL = "https://hopper-api.<your-subdomain>.workers.dev/"
```

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/v1/pandals?festival=DURGA_PUJA&year=2026` | List pandals |
| GET | `/v1/crowd/{pandalId}` | Get aggregated crowd level |
| POST | `/v1/crowd` | Submit crowd report |
| GET | `/v1/calendar?festival=DURGA_PUJA&year=2026` | Get tithi calendar |
| GET | `/v1/artists` | List artists |
