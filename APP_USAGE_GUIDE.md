# Hopper — Festival Atlas App Usage Guide (MVP)

## What is Hopper?

Hopper is a pandal-hopping companion app for **Durga Puja** and **Jagaddhatri Puja** festivals in West Bengal, India. It helps festival-goers navigate between pandals (temporary decorated structures housing deity idols), avoid crowds, and find safe exit routes.

---

## Features (MVP)

### 1. Interactive Map (Home Tab)
- **Pandal pins** on a MapLibre-powered map, color-coded by crowd level:
  - 🟢 Green = Low crowd
  - 🟡 Yellow = Moderate
  - 🔴 Red = High crowd
- **Exit node pins** showing nearby escape points (police posts, medical, transport hubs)
- **"Get Me Out" button** — tap to see the nearest exit routes from your current location
- **Night Safety Mode** — toggle a darker map style highlighting well-lit paths
- **Offline map support** — pre-downloads a 50MB tile region for use without connectivity

### 2. Near Me Tab
- Lists pandals sorted by distance from your current location
- Shows pandal name, theme, and neighborhood
- Tap a card to navigate to it on the map

### 3. Calendar Tab
- Festival schedule with day-by-day events (Shashti through Dashami for Durga Puja, etc.)

### 4. Explore Tab
Hub linking to secondary features:
- **Itinerary Planner** — build an optimized pandal-hopping route
- **Light Trail** — Chandannagar illuminated installations (Jagaddhatri Puja)
- **Oral Histories** — community stories tied to pandals
- **Ritual Guides** — step-by-step ritual instructions
- **Volunteer** — sign up for festival volunteer shifts
- **Leaderboard** — community reporter reputation rankings

### 5. Crowd Reporting
- Submit crowd level reports for any pandal (Green/Yellow/Red)
- Rate-limited to prevent spam (one report per pandal per device per time window)
- Privacy-preserving: uses a device hash, no personal data collected
- Builds your reputation badge tier (Newcomer → higher tiers)

---

## Setup Requirements

### On-Device
1. **Location permission** — the app will prompt on first launch. Grant "While using the app" for full functionality.
2. **GPS enabled** — ensure location/GPS is turned on in device settings.
3. **Internet** — needed for crowd report sync and map tile loading (offline mode available after initial download).

### Developer Setup (local.properties)
```properties
api.base.url=https://your-worker.workers.dev/
supabase.url=https://your-project.supabase.co
supabase.anon.key=your-anon-key
map.style.url=https://your-style-url/style.json
map.night.style.url=https://your-night-style-url/style.json
map.tiles.url=https://your-tiles-url/tiles.json
```

---

## Architecture Overview

```
┌─────────────────────────────────────────────────┐
│  UI Layer (Jetpack Compose)                     │
│  MapScreen · NearMeScreen · CalendarScreen      │
│  ExploreScreen · CrowdReportSheet               │
├─────────────────────────────────────────────────┤
│  ViewModels (Hilt-injected)                     │
│  MapViewModel · NearMeViewModel                 │
│  CrowdReportViewModel                           │
├─────────────────────────────────────────────────┤
│  Domain Layer                                   │
│  UseCases · Repository interfaces               │
│  LocationProvider · FestivalToggleController     │
├─────────────────────────────────────────────────┤
│  Data Layer                                     │
│  Room DB · GeoJSON assets · Retrofit API        │
│  FusedLocationProvider · WorkManager sync        │
├─────────────────────────────────────────────────┤
│  Backend (Cloudflare Worker + Supabase)         │
│  /crowd · /pandals · /calendar · /artists       │
└─────────────────────────────────────────────────┘
```

---

## Data Sources

| Data | Source | Update Method |
|------|--------|---------------|
| Pandal locations | GeoJSON assets (bundled) | App update / future API |
| Crowd levels | User reports → Supabase | Real-time sync via WorkManager |
| Exit nodes | GeoJSON asset (bundled) | App update |
| Connectors (paths) | GeoJSON asset (bundled) | App update |
| Calendar events | JSON asset (bundled) | App update |
| Historical crowd patterns | JSON asset (bundled) | Used for predictions |

---

## Known Limitations (MVP)

1. **Map tiles** — currently using MapLibre demo tiles (basic world map). For street-level detail, configure a proper tile provider (MapTiler, Protomaps, or self-hosted).
2. **No pandal detail screen** — tapping a pandal pin doesn't open a detail view yet.
3. **No directions/routing** — "Get Me Out" shows exit points but doesn't provide turn-by-turn navigation.
4. **Single-city focus** — data is bundled for specific cities; no dynamic city selection.
5. **No push notifications** — crowd alerts and procession updates are pull-only.
6. **No image loading** — pandal photos referenced in data but no image loading library (Coil/Glide) integrated.

---

## How to Build & Run

```bash
# From project root
./gradlew :app:assembleDebug

# Install on connected device
./gradlew :app:installDebug

# Run release build (already in app/release/)
adb install app/release/app-release.apk
```

**Min SDK:** 26 (Android 8.0)  
**Target SDK:** 36  
**Language:** Kotlin with Jetpack Compose  
**DI:** Hilt  
**Database:** Room  
**Maps:** MapLibre GL Native  
**Location:** Google Play Services FusedLocationProvider  

---

## Festival Toggle

The app supports switching between festivals:
- **Durga Puja 2026** — `pandals_durga_puja_2026.geojson`
- **Jagaddhatri Puja 2026** — `pandals_jagaddhatri_puja_2026.geojson`

The `FestivalToggleController` manages which dataset is active.
