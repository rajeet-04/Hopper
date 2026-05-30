# Deployment Guide

## Prerequisites
- Node.js 18+ installed
- A [Supabase](https://supabase.com) account (free tier)
- A [Cloudflare](https://cloudflare.com) account (free tier)

---

## Step 1: Supabase Setup

1. **Create a new project** at https://supabase.com/dashboard
   - Choose a region close to your users (e.g., Mumbai for India)
   - Note your project URL and keys

2. **Run the migrations** in order via the SQL Editor (Dashboard → SQL Editor → New Query):
   - Paste and run `supabase/migrations/001_initial_schema.sql`
   - Paste and run `supabase/migrations/002_row_level_security.sql`
   - Paste and run `supabase/migrations/003_seed_data.sql`
   - Paste and run `supabase/migrations/004_cleanup_cron.sql`

3. **Verify** by going to Table Editor — you should see tables with seed data.

4. **Copy your credentials**:
   - Project URL: `https://<ref>.supabase.co`
   - Service Role Key: Settings → API → `service_role` key (keep secret!)

---

## Step 2: Cloudflare Worker Setup

1. **Install Wrangler CLI**:
   ```bash
   npm install -g wrangler
   ```

2. **Login to Cloudflare**:
   ```bash
   wrangler login
   ```

3. **Install dependencies**:
   ```bash
   cd backend/worker
   npm install
   ```

4. **Set secrets** (these are encrypted, never in code):
   ```bash
   wrangler secret put SUPABASE_URL
   # Paste: https://<your-ref>.supabase.co

   wrangler secret put SUPABASE_SERVICE_KEY
   # Paste: your service_role key
   ```

5. **Deploy**:
   ```bash
   wrangler deploy
   ```

6. **Note your Worker URL**: `https://hopper-api.<your-subdomain>.workers.dev`

---

## Step 3: Android App Configuration

Update `app/src/main/java/com/example/hopper/di/NetworkModule.kt`:

```kotlin
private const val BASE_URL = "https://hopper-api.<your-subdomain>.workers.dev/"
```

---

## Step 4: Verify

Test the API:
```bash
# Health check
curl https://hopper-api.<your-subdomain>.workers.dev/health

# Get pandals
curl "https://hopper-api.<your-subdomain>.workers.dev/v1/pandals?festival=DURGA_PUJA&year=2026"

# Get calendar
curl "https://hopper-api.<your-subdomain>.workers.dev/v1/calendar?festival=DURGA_PUJA&year=2026"

# Submit crowd report
curl -X POST https://hopper-api.<your-subdomain>.workers.dev/v1/crowd \
  -H "Content-Type: application/json" \
  -d '{"pandalId":"dp2026_bagbazar","bucket":"YELLOW","deviceHash":"a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2","reportedAt":1729000000000}'

# Get crowd level
curl https://hopper-api.<your-subdomain>.workers.dev/v1/crowd/dp2026_bagbazar
```

---

## Cost Breakdown (Free Tier)

| Service | Free Tier Limit | Expected Usage |
|---------|----------------|----------------|
| Supabase | 500MB DB, 2GB bandwidth, 50K MAU | ~50MB DB, <1GB bandwidth |
| Cloudflare Workers | 100K requests/day | ~5K-20K requests/day during festival |
| **Total** | **$0/month** | Well within limits |

---

## Scaling Notes

If the app grows beyond free tier limits during peak festival days:
- **Supabase Pro** ($25/mo) gives 8GB DB, 250GB bandwidth
- **Cloudflare Workers Paid** ($5/mo) gives 10M requests/month
- **Azure** ($100 student credit) can host a backup PostgreSQL if needed

The architecture is designed so that the app works fully offline — the backend is for sync and crowd aggregation only. Even if the backend goes down, users can still navigate using bundled data.
