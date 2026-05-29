Now I have a thorough understanding of both festivals. Let me build the complete, deeply detailed PRD extension and feature plan for the \*\*Festival Atlas\*\* app, tailored specifically for \*\*Jagat Dharti (Jagaddhatri Puja)\*\* and \*\*Durga Puja\*\*.

\---

\# 🎨 Festival Atlas — Complete Master Plan  
\#\#\# Tailored for Jagaddhatri Puja (Jagat Dharti) \+ Durga Puja | Android App

\---

\#\# 📖 CULTURAL CONTEXT (Understanding the Domain First)

Before writing a single line of code, understand \*\*what you're building for\*\*:

Jagaddhatri Puja is a Hindu festival celebrated primarily in West Bengal, particularly in Chandannagar and Krishnanagar, and is dedicated to Jagaddhatri Devi, believed to be an incarnation of Durga Mata.

Jagadhatri Puja is one of the important festivals celebrated \*\*after\*\* Durga Puja, Laxmi Puja, and Kali Puja in the month of Kartik.

Chandannagar is world-famous for its fabulous lighting decoration — during this period, the entire city is decorated with colourful modern lights, especially at the time of Bishorjon.

In Chandannagar, people visit from one pandal to another to see the colossal idols of Goddess Jagadhatri and ravishing decorative lights.

Kolkata's Durga Puja has been acknowledged as an \*\*'Intangible Cultural Heritage' by UNESCO\*\*, making digital preservation not just a nice-to-have but a global responsibility.

With the end of Jagadhatri Puja, it is considered to be the \*\*end of Hindu festivals for the year\*\*, and devotees start counting for the next festive season — making this app a year-round cultural anchor.

\---

\#\# 🗂️ REVISED & EXPANDED PRD

\---

\#\# SECTION 1 — DUAL-FESTIVAL IDENTITY

\#\#\# 1.1 The Two Festival Pillars

Your app must treat these as \*\*two distinct but architecturally identical festival modules\*\* that share the same engine:

| Dimension | Durga Puja | Jagaddhatri Puja (Jagat Dharti) |  
|---|---|---|  
| \*\*Primary Zone\*\* | Kolkata, all of Bengal | Chandannagar, Krishnanagar, Rishra, Hooghly |  
| \*\*Timing\*\* | Ashwin (Sept–Oct) | Kartik (Oct–Nov) |  
| \*\*Famous For\*\* | Artistic pandals, themes, sculptures | Lighting displays, processions (Bishorjon) |  
| \*\*Crowd Type\*\* | City-wide pandal hopping | Localized but extremely dense corridors |  
| \*\*Cultural Artifacts\*\* | Idol, theme design, decor | Lights, idol, procession routes |  
| \*\*Unique Ritual\*\* | Sindoor Khela, Dhunuchi Naach | Bishorjon procession with light floats |  
| \*\*Database Tables\*\* | \`festivals.durga\_puja\` | \`festivals.jagat\_dharti\` |

\> \*\*Architectural Decision:\*\* Use a single \`Festival\` entity with a \`festival\_type ENUM('DURGA\_PUJA', 'JAGAT\_DHARTI', 'KALI\_PUJA')\` field, so the codebase scales to all Bengali festivals without rewriting.

\---

\#\# SECTION 2 — COMPLETE FEATURE REGISTRY

Every feature below is tagged with: \*\*\[MVP\]\*\* (v0.1), \*\*\[v0.2\]\*\*, \*\*\[v0.3\]\*\*, \*\*\[v0.4\]\*\*, or \*\*\[Future\]\*\*

\---

\#\#\# 🗺️ FEATURE CLUSTER A — THE LIVING MAP

\#\#\#\# A1. Dual-Festival Map View \*\*\[MVP\]\*\*  
\- Toggle switch at top: \`🔱 Durga Puja | 🌟 Jagat Dharti\`  
\- When toggled, map re-renders pandal pins for that festival's season  
\- Color-coded pins:  
  \- 🔴 Durga Puja pandals  
  \- 🟡 Jagaddhatri pandals  
  \- ⚪ Historical/archived (past years, not active)  
\- Cluster pins intelligently when zoomed out (MarkerCluster library)

\#\#\#\# A2. Pandal Detail Card \*\*\[MVP\]\*\*  
Each pandal pin opens a bottom sheet showing:  
\`\`\`  
┌─────────────────────────────────────┐  
│  🏮 Santosh Mitra Square            │  
│  📍 Lebutala, Bowbazar, Kolkata     │  
│  🎨 Theme 2025: "Deep Sea Kingdom"  │  
│  👁 Est. Wait: 🔴 75 mins           │  
│  ⭐ Community Rating: 4.7/5         │  
│  📸 \[Photo\] \[Photo\] \[Photo\] →       │  
│  🏛 Est. Year: 1936                 │  
│  \[Navigate\] \[Archive\] \[Report Wait\] │  
└─────────────────────────────────────┘  
\`\`\`

\#\#\#\# A3. Jagaddhatri-Specific — Lighting Trail Map \*\*\[v0.2\]\*\*  
\- The streets of Chandannagar glow with colorful and creative light decorations that tell stories from mythology, nature, and everyday life — crafted by skilled local artists using modern techniques and bright LEDs.  
\- Feature: A curated \*\*"Chandannagar Light Trail"\*\* overlay on the map  
\- Shows best vantage points for each lighting installation  
\- Ordered as a walking route from start to end  
\- Each light structure has: Artist name, Dimensions, Theme/Story

\#\#\#\# A4. Bishorjon (Procession) Tracker \*\*\[v0.2\]\*\*  
\- Jagaddhatri Puja's Bishorjon is a massive procession unlike Durga Puja  
\- Live map showing active procession routes (crowd-reported)  
\- ETA of procession reaching your current pin  
\- Audio alert: "Bishorjon of \[Pandal Name\] is 500m away from your location"

\#\#\#\# A5. Offline Map Caching \*\*\[MVP\]\*\*  
\- Pre-download lightweight GeoJSON tiles for:  
  \- Chandannagar city grid  
  \- Krishnanagar pandal zone  
  \- North/South/Central Kolkata pandal clusters  
\- Cache size target: \< 15MB per city zone  
\- Shelf life: Refreshed on app launch if network available

\---

\#\#\# 🔴 FEATURE CLUSTER B — CROWD INTELLIGENCE ENGINE

\#\#\#\# B1. Wait Time Reporting \*\*\[MVP\]\*\*  
Three-tap crowd report:  
\`\`\`  
\[🔴 Very Long \- 60min+\] \[🟡 Moderate \- 20min\] \[🟢 Short \- \<10min\]  
\`\`\`  
\- Expires after 20 minutes (report becomes stale)  
\- Weighted average of last 5 reports shown  
\- "Last reported: 4 mins ago by 3 people"

\#\#\#\# B2. Predictive Wait Times \*\*\[v0.3\]\*\*  
\- Historical data from previous year(s) fed into a simple heuristic  
\- Example: "Santosh Mitra Square is historically VERY CROWDED on Ashtami night between 9PM–12AM"  
\- Shown as a \*\*heat timeline bar\*\* on the pandal detail page

\#\#\#\# B3. Smart Visit Planner \*\*\[v0.3\]\*\*  
User inputs:  
\- Current location  
\- Number of pandals they want to visit  
\- Available hours  
\- Preferred crowd level (light / moderate / any)

App outputs: An \*\*optimized pandal itinerary\*\* using a nearest-neighbor routing algorithm, respecting live crowd data.

\`\`\`  
YOUR PLAN FOR TODAY — ASHTAMI NIGHT  
───────────────────────────────────  
📍 Start: Gariahat (You are here)  
⏱ 6 hours available

1\. 7:00 PM → Ballygunge Cultural (🟢 Short wait, 0.4km)  
2\. 8:10 PM → Ekdalia Evergreen (🟡 Moderate, 1.1km)  
3\. 9:40 PM → Triangular Park (🟢 Short wait, 0.8km)  
4\. 11:00 PM → Lake Kalibari (🟢 Short wait, 0.5km)  
───────────────────────────────────  
Est. Total: 5.8km walk | Home by 12:30AM  
\`\`\`

\#\#\#\# B4. Jagaddhatri Lighting Show Schedule \*\*\[v0.2\]\*\*  
\- Many Chandannagar clubs have \*\*timed lighting sequences\*\*  
\- Feature: Show schedule of when specific light displays are active  
\- Push notification: "The Mankundu Club lighting display starts in 30 minutes"

\---

\#\#\# 🚨 FEATURE CLUSTER C — EMERGENCY & SAFETY SYSTEMS

\#\#\#\# C1. "Get Me Out" Emergency Exit \*\*\[MVP\]\*\*  
Single large button on main map. On tap:  
\- Instantly calculates 3 nearest exits:  
  \- 🚇 Metro Station  
  \- 🚉 Railway Station  
  \- 🚓 Police Booth (pre-seeded from West Bengal Police data)  
  \- 🏥 Medical Camp (updated during festival week)  
  \- 🅿️ Parking Zone  
\- Shows walking route, estimated time, and crowd density along the route

\#\#\#\# C2. Jagaddhatri Night Safety Mode \*\*\[v0.2\]\*\*  
\- The Bishorjon happens at \*\*night\*\* with extremely dense crowds on narrow Chandannagar streets  
\- Activate "Night Safety Mode":  
  \- Screen dims to red-tinted low-brightness  
  \- Map switches to high-contrast dark theme  
  \- Shows only emergency exits and police booths  
  \- One-tap "Share My Location" sends your live GPS to a pre-saved contact

\#\#\#\# C3. Medical Emergency POI Layer \*\*\[MVP\]\*\*  
\- Pre-seeded: Major hospitals near pandal zones  
\- Festival-week: Temporary first-aid booths added by community admins  
\- Shows: Distance, contact number, 24hr status

\#\#\#\# C4. Lost Person Bulletin Board \*\*\[v0.3\]\*\*  
\- Simple community board: "I'm lost at \[auto-detected location\]" post  
\- Others nearby can see and assist  
\- Requires no account — just a display name  
\- Auto-expires after 2 hours

\---

\#\#\# 🏛️ FEATURE CLUSTER D — THE CULTURAL ARCHIVE

\#\#\#\# D1. Year-by-Year Pandal Timeline \*\*\[v0.3\]\*\*  
\`\`\`  
SANTOSH MITRA SQUARE — ARCHIVE  
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━  
2025  → Theme: "Deep Sea Kingdom"     \[4 Photos\] \[Award: Best Theme\]  
2024  → Theme: "Egyptian Civilization" \[7 Photos\]  
2023  → Theme: "Bamboo Forest"        \[2 Photos\] \[Award: Best Eco Puja\]  
2022  → Theme: "War & Peace"          \[3 Photos\]  
...  
1936  → Founded (Est. year seeded from historical records)  
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━  
\`\`\`

\#\#\#\# D2. Artisan & Artist Registry \*\*\[v0.3\]\*\*  
\- Skilled local artists design amazing light structures using modern techniques and bright LEDs — these people deserve recognition and documentation  
\- Each artist has a profile card:  
\`\`\`  
┌──────────────────────────────────┐  
│ 🧑‍🎨 Kartick Ghosh               │  
│ Specialty: Idol Making (Kumartuli)│  
│ Active Since: 1994               │  
│ Pandals Worked: 47               │  
│ Notable: Bagbazar 2022, 2023     │  
│ \[See All Work\] \[Contact\]         │  
└──────────────────────────────────┘  
\`\`\`

\#\#\#\# D3. Oral History Vault \*\*\[v0.4\]\*\*  
\- Text snippets from elder committee members  
\- Audio recordings (mp3 stored on object storage, linked by reference)  
\- Community-submitted "neighborhood lore" tagged to specific pandal coordinates  
\- Moderated before publish (admin web dashboard)

\#\#\#\# D4. Chandannagar Heritage Layer \*\*\[v0.3\]\*\*  
\- Chandannagar, known for its French colonial heritage, hosts elaborate light displays and processions, blending tradition with modernity.  
\- Overlay on map: Historical buildings, French colonial landmarks, founding pandal locations  
\- Brief historical description on tap

\#\#\#\# D5. Deity Visual Library \*\*\[v0.3\]\*\*  
\- Photo gallery of \*\*idol styles\*\* across years and pandals  
\- Jagaddhatri-specific: In Tantra and Purana, Jagadhatri is depicted as having the colour of the morning sun, three-eyed and four arms, holding Chakra, conch, bow, and arrow — document regional variations of this iconography  
\- Filter by: Year, City, Style (Traditional / Modern / Eco-Friendly)

\---

\#\#\# 🙏 FEATURE CLUSTER E — PUJA COMPANION (Devotee Utility)

\#\#\#\# E1. Festival Calendar & Tithi Tracker \*\*\[MVP\]\*\*  
\`\`\`  
DURGA PUJA 2026  
━━━━━━━━━━━━━━━━━━━━━━━━  
Oct 16 → Mahasashthi   \[Today\]  
Oct 17 → Maha Saptami  
Oct 18 → Maha Ashtami  ← Peak Crowd  
Oct 19 → Maha Navami   ← Peak Crowd    
Oct 20 → Vijayadashami (Dashami)  
Oct 21 → Sindoor Khela

JAGADDHATRI PUJA 2026  
━━━━━━━━━━━━━━━━━━━━━━━━  
Nov (TBD) → Saptami  
Nov (TBD) → Ashtami  
Nov (TBD) → Navami     ← Main Puja Day  
Nov (TBD) → Bishorjon  ← Procession Night  
\`\`\`  
\- Pull tithi timing from a pre-computed Hindu calendar JSON (no API needed)  
\- Push notifications for each major tithi 1 hour in advance

\#\#\#\# E2. Puja Ritual Guide \*\*\[v0.2\]\*\*  
\- Step-by-step guide for:  
  \- Anjali (offering) timings and procedure  
  \- Sandhi Puja (exact 48-minute window between Ashtami and Navami)  
  \- Dhunuchi Naach guide with tutorial video links  
  \- Sindoor Khela guide  
  \- Bishorjon viewing etiquette  
\- Offline content (no network needed)

\#\#\#\# E3. Bhog & Food Finder \*\*\[v0.2\]\*\*  
\- During Jagaddhatri Puja, devotees prepare bhog like khichuri, labra, luchi, payesh, and a variety of Bengali sweets for offering.  
\- Map overlay: "Bhog Distribution" pins (crowd-reported, time-limited)  
\- Street food stall finder near pandal zones  
\- Community-rated food spots

\#\#\#\# E4. Mantra & Prayer Audio Library \*\*\[v0.3\]\*\*  
\- Offline downloadable audio:  
  \- Durga Saptashati excerpts (recitation guides, not full reproduction)  
  \- Jagaddhatri Dhyan Mantra  
  \- Mahalaya audio notification (plays at dawn on Mahalaya)  
  \- Chandipatha reference links

\#\#\#\# E5. Dress & Prep Guide \*\*\[v0.2\]\*\*  
\- What to wear for pandal hopping (comfort \+ tradition)  
\- What to carry (water, portable charger, medicines)  
\- Crowd safety tips for families with children/elderly  
\- Localized in Bengali \+ English

\---

\#\#\# 👥 FEATURE CLUSTER F — COMMUNITY & SOCIAL

\#\#\#\# F1. Club/Committee Portal (Web Dashboard) \*\*\[v0.2\]\*\*  
A React/Next.js web admin panel for puja committees to self-manage:  
\`\`\`  
FIELDS COMMITTEES CAN UPDATE:  
├── Pandal name, address, coordinates (GPS pin drop)  
├── Theme for current year  
├── Inauguration date/time  
├── Opening hours each day  
├── Idol maker / Light artist credit  
├── Awards received  
├── Photos (upload up to 20 per year)  
├── Emergency contact number  
├── Volunteer contact  
└── Historical entries for past years  
\`\`\`  
\- Login via Google/OTP (no password to remember)  
\- Changes go live after lightweight moderation check  
\- Mobile-responsive so committee members can update from phone

\#\#\#\# F2. Community Photo Wall \*\*\[v0.3\]\*\*  
\- Users submit photos tagged to a specific pandal \+ year  
\- No social login required — just a display name  
\- Best photos (community-upvoted) appear in the archive  
\- Watermark overlay: "Festival Atlas Community Archive"  
\- EXIF data stripped for privacy before storage

\#\#\#\# F3. Community Wait Time Gamification \*\*\[v0.2\]\*\*  
\- "Crowd Reporter" badge for users who submit 10+ accurate reports  
\- Accuracy score: If your report matches the next 3 reports, you get accuracy points  
\- No real prizes — just a community leaderboard during festival week

\#\#\#\# F4. Volunteer Coordination Module \*\*\[v0.3\]\*\*  
\- Puja committees can post volunteer requirements  
\- Example: "Need 3 volunteers for crowd management at Gate 2 on Ashtami night"  
\- Users can sign up via the app  
\- Committee gets a list with contact info

\---

\#\#\# 📡 FEATURE CLUSTER G — THE OPEN DATA LAYER

\#\#\#\# G1. Public REST API \*\*\[v0.4\]\*\*  
\`data.festivalatlas.org\`

\`\`\`  
GET /api/v1/pandals?festival=jagat\_dharti\&city=chandannagar  
GET /api/v1/pandals/{id}/history  
GET /api/v1/artists?specialty=lighting  
GET /api/v1/crowd?pandal\_id={id}  
GET /api/v1/calendar?year=2026\&festival=durga\_puja  
\`\`\`  
\- Rate-limited (100 req/day for anonymous, 10,000 for registered researchers)  
\- Returns clean JSON  
\- Documented on a simple Swagger page

\#\#\#\# G2. Annual GeoJSON Dump \*\*\[v0.4\]\*\*  
\- Every year after the festival, auto-generate:  
  \- \`durga\_puja\_2026\_pandals.geojson\`  
  \- \`jagat\_dharti\_2026\_pandals.geojson\`  
  \- \`artisans\_2026.json\`  
\- Hosted on GitHub Releases AND on the data portal  
\- Licensed under CC BY 4.0 (free use with attribution)

\#\#\#\# G3. Researcher Dashboard \*\*\[v0.4\]\*\*  
\- Simple web UI to query and download subsets of data  
\- Filter by: Year, City, Festival, Award status  
\- Export as: JSON, CSV, GeoJSON, KML (for Google Maps import)

\---

\#\# SECTION 3 — REVISED TECHNOLOGY STACK (Detailed)

\`\`\`  
┌─────────────────────────────────────────────────────────┐  
│                    FESTIVAL ATLAS STACK                 │  
├──────────────────┬──────────────────────────────────────┤  
│ ANDROID APP      │ Kotlin \+ Jetpack Compose             │  
│                  │ MVVM \+ Clean Architecture            │  
│                  │ Room DB (offline pandal data)        │  
│                  │ MapLibre GL (open-source map engine) │  
│                  │ WorkManager (background sync)        │  
│                  │ DataStore (user prefs)               │  
│                  │ Hilt (dependency injection)          │  
│                  │ Retrofit \+ OkHttp (networking)       │  
├──────────────────┼──────────────────────────────────────┤  
│ WEB DASHBOARD    │ Next.js 14 (App Router)              │  
│ (Contributor)    │ Tailwind CSS                         │  
│                  │ React Hook Form                      │  
│                  │ NextAuth.js (Google \+ OTP login)     │  
├──────────────────┼──────────────────────────────────────┤  
│ BACKEND          │ Node.js (Express) OR Go (Gin)        │  
│                  │ PostgreSQL \+ PostGIS extension        │  
│                  │ Redis (crowd data caching, 20min TTL)│  
│                  │ S3-compatible storage (photos/audio) │  
│                  │ BullMQ (background job queue)        │  
├──────────────────┼──────────────────────────────────────┤  
│ MAP TILES        │ OpenStreetMap (free tiles via        │  
│                  │ self-hosted or maptiler.com free)    │  
│                  │ GeoJSON overlays (custom layers)     │  
├──────────────────┼──────────────────────────────────────┤  
│ HOSTING          │ Railway.app / Render.com (backend)   │  
│                  │ Vercel (Next.js dashboard)           │  
│                  │ Cloudflare R2 (photos — cheap)       │  
│                  │ Supabase (PostgreSQL hosting)        │  
├──────────────────┼──────────────────────────────────────┤  
│ CI/CD            │ GitHub Actions                       │  
│                  │ Fastlane (Android build \+ deploy)    │  
└──────────────────┴──────────────────────────────────────┘  
\`\`\`

\> \*\*Why MapLibre over Google Maps?\*\* Zero cost at any scale. Full open-source. Allows offline vector tile caching natively. Critical for budget survival.

\---

\#\# SECTION 4 — REVISED DATABASE SCHEMA

\`\`\`sql  
\-- CORE ENTITIES

CREATE TABLE festivals (  
  id UUID PRIMARY KEY,  
  type VARCHAR(20), \-- 'DURGA\_PUJA', 'JAGAT\_DHARTI', 'KALI\_PUJA'  
  year INTEGER,  
  start\_date DATE,  
  end\_date DATE,  
  bishorjon\_date DATE,  \-- immersion day  
  created\_at TIMESTAMP  
);

CREATE TABLE pandals (  
  id UUID PRIMARY KEY,  
  name VARCHAR(255),  
  name\_bengali VARCHAR(255),       \-- বাংলা নাম  
  city VARCHAR(100),  
  neighborhood VARCHAR(100),  
  location GEOGRAPHY(POINT, 4326), \-- PostGIS  
  established\_year INTEGER,  
  committee\_contact TEXT,  
  is\_active BOOLEAN DEFAULT TRUE,  
  created\_at TIMESTAMP,  
  updated\_at TIMESTAMP  
);

CREATE TABLE pandal\_years (  
  id UUID PRIMARY KEY,  
  pandal\_id UUID REFERENCES pandals(id),  
  festival\_id UUID REFERENCES festivals(id),  
  theme VARCHAR(500),  
  idol\_artist\_id UUID,             \-- FK to artisans  
  light\_artist\_id UUID,            \-- FK to artisans (Jagat Dharti)  
  opening\_time TIME,  
  closing\_time TIME,  
  awards TEXT\[\],  
  notes TEXT,  
  UNIQUE(pandal\_id, festival\_id)  
);

CREATE TABLE artisans (  
  id UUID PRIMARY KEY,  
  name VARCHAR(255),  
  name\_bengali VARCHAR(255),  
  specialty VARCHAR(50),           \-- 'IDOL\_MAKER', 'LIGHTING', 'THEME\_DESIGN'  
  base\_city VARCHAR(100),  
  active\_since INTEGER,  
  bio TEXT,  
  contact\_info TEXT  
);

CREATE TABLE crowd\_reports (  
  id UUID PRIMARY KEY,  
  pandal\_id UUID REFERENCES pandals(id),  
  wait\_level VARCHAR(10),          \-- 'LOW', 'MEDIUM', 'HIGH'  
  wait\_minutes INTEGER,  
  reported\_at TIMESTAMP,  
  reporter\_device\_hash VARCHAR(64),-- anonymized device ID  
  expires\_at TIMESTAMP             \-- auto-expire in 20 mins  
);

CREATE TABLE emergency\_pois (  
  id UUID PRIMARY KEY,  
  poi\_type VARCHAR(30),            \-- 'METRO', 'RAILWAY', 'POLICE', 'HOSPITAL', 'MEDICAL\_CAMP'  
  name VARCHAR(255),  
  location GEOGRAPHY(POINT, 4326),  
  contact\_number VARCHAR(20),  
  is\_24hr BOOLEAN,  
  festival\_only BOOLEAN,           \-- true \= temporary medical camp  
  active\_from DATE,  
  active\_until DATE  
);

CREATE TABLE oral\_histories (  
  id UUID PRIMARY KEY,  
  pandal\_id UUID REFERENCES pandals(id),  
  title VARCHAR(500),  
  content TEXT,  
  audio\_url TEXT,  
  contributor\_name VARCHAR(200),  
  year\_referenced INTEGER,  
  is\_approved BOOLEAN DEFAULT FALSE,  
  submitted\_at TIMESTAMP  
);

CREATE TABLE photos (  
  id UUID PRIMARY KEY,  
  pandal\_id UUID REFERENCES pandals(id),  
  festival\_id UUID REFERENCES festivals(id),  
  storage\_url TEXT,  
  thumbnail\_url TEXT,  
  contributor\_name VARCHAR(200),  
  caption TEXT,  
  upvotes INTEGER DEFAULT 0,  
  is\_approved BOOLEAN DEFAULT FALSE,  
  taken\_at TIMESTAMP  
);

\-- PostGIS spatial index for fast proximity queries  
CREATE INDEX pandals\_location\_idx ON pandals USING GIST(location);  
CREATE INDEX emergency\_pois\_location\_idx ON emergency\_pois USING GIST(location);  
\`\`\`

\---

\#\# SECTION 5 — REVISED PHASED EXECUTION PLAN

\#\#\# 📦 Version 0.1 — The Utility MVP (Target: 3 weeks)  
\*\*Goal: Prove the core works in dense crowds.\*\*

| Task | Detail |  
|---|---|  
| Android map shell | MapLibre GL \+ Compose |  
| Seed data | 150 pandals (50 Jagat Dharti, 100 Durga Puja) |  
| Offline caching | Room DB \+ JSON assets bundled |  
| "Puja Near Me" | Location permission → nearest 5 pandals |  
| Wait time widget | 3-tap crowd report UI |  
| Emergency Exit | Nearest Metro/Police/Hospital routing |  
| Festival Calendar | Hard-coded 2026 tithis |  
| Map toggle | Durga Puja / Jagat Dharti switch |

\*\*Definition of Done:\*\* A volunteer can open the app in Chandannagar with no internet, see the nearest pandal, and navigate to a police booth.

\---

\#\#\# 📦 Version 0.2 — Community & Chandannagar Special (Target: 6 weeks post-MVP)  
\*\*Goal: Activate community data collection.\*\*

| Task | Detail |  
|---|---|  
| Contributor web dashboard | Next.js form for committees |  
| Authentication | Google OAuth \+ mobile OTP |  
| Lighting Trail Map | Chandannagar-specific overlay |  
| Bishorjon Tracker | Crowd-reported procession map |  
| Night Safety Mode | Red-tinted emergency-only view |  
| Bhog Finder | Community-reported bhog pins |  
| Ritual Guide | Offline content module |

\---

\#\#\# 📦 Version 0.3 — The Cultural Archive (Target: 3 months post-MVP)  
\*\*Goal: Shift from pure navigation to living archive.\*\*

| Task | Detail |  
|---|---|  
| Year-by-Year Timeline | Full pandal history UI |  
| Artisan Registry | Artist profiles \+ pandal linkage |  
| Predictive Wait Times | Historical heuristics engine |  
| Smart Visit Planner | Optimized itinerary generator |  
| Community Photo Wall | Upload \+ approve pipeline |  
| Lost Person Board | Real-time community board |  
| Heritage Layer (Chandannagar) | French colonial POI overlay |  
| Bengali language support | Full UI translation |

\---

\#\#\# 📦 Version 0.4 — Open Data & Oral Histories (Target: 6 months post-MVP)  
\*\*Goal: Become a permanent public resource.\*\*

| Task | Detail |  
|---|---|  
| Public REST API | \`data.festivalatlas.org\` launch |  
| Annual GeoJSON dump | Auto-generated after each festival |  
| Oral History Vault | Text \+ audio submissions |  
| Researcher Dashboard | Query \+ download UI |  
| Volunteer Module | Committee volunteer coordination |  
| Mantra Audio Library | Offline audio packs |

\---

\#\# SECTION 6 — SCREEN MAP (Android App)

\`\`\`  
APP ENTRY  
├── Splash → Permission Request (Location)  
│  
HOME SCREEN  
├── \[Map View\] ← Default  
│   ├── Festival Toggle (Durga Puja / Jagat Dharti)  
│   ├── Pandal Pins with Crowd Indicators  
│   ├── My Location Button  
│   ├── \[🚨 GET ME OUT\] Button (always visible)  
│   └── Bottom Sheet → Pandal Detail  
│       ├── Current Info (Theme, Wait, Hours)  
│       ├── \[Navigate\] → Route to Pandal  
│       ├── \[Archive\] → Year-by-Year History  
│       ├── \[Photos\] → Community Photo Wall  
│       └── \[Report Wait\] → 3-tap reporter  
│  
├── \[Explore\] Tab  
│   ├── Top Pandals This Year (Editor's list)  
│   ├── Award Winners  
│   ├── Chandannagar Light Trail (Jagat Dharti)  
│   ├── Bishorjon Live Map (Jagat Dharti)  
│   └── Visit Planner (Smart Itinerary)  
│  
├── \[Calendar\] Tab  
│   ├── This Year's Tithis  
│   ├── Countdown Timer  
│   ├── Ritual Guide per Tithi  
│   └── Notification Settings  
│  
├── \[Archive\] Tab  
│   ├── Search Pandals  
│   ├── Browse by Year  
│   ├── Browse by City  
│   ├── Artisan Profiles  
│   └── Oral Histories  
│  
└── \[Community\] Tab  
    ├── Submit Crowd Report  
    ├── Submit Photo  
    ├── Lost Person Board  
    └── Volunteer Opportunities  
\`\`\`

\---

\#\# SECTION 7 — NON-FUNCTIONAL REQUIREMENTS (Hardened)

| NFR | Requirement | Implementation |  
|---|---|---|  
| \*\*Offline First\*\* | Core map \+ pandal data available with zero network | Room DB \+ bundled GeoJSON assets |  
| \*\*Crowd Tolerance\*\* | App must not crash when 10,000+ users are on simultaneously | Redis caching for crowd data; read-heavy architecture |  
| \*\*Cold Start Time\*\* | App opens map in \< 2.5 seconds | Pre-cached tiles, lazy load non-critical data |  
| \*\*Battery Usage\*\* | Must not drain battery during 6-hour pandal hop | Stop location polling when stationary; WorkManager for background |  
| \*\*Low-End Device Support\*\* | Target Android 8.0+, 2GB RAM devices | No heavy render pipelines; Compose optimization |  
| \*\*Night Usability\*\* | App usable in bright festive environments | High contrast mode; large tap targets (min 48dp) |  
| \*\*Bengali Script\*\* | All content available in Bengali | i18n strings, Bengali font (Hind Siliguri) |  
| \*\*Data Exportability\*\* | All data freely exportable | JSON/CSV/GeoJSON endpoints always available |  
| \*\*Privacy\*\* | No PII stored for anonymous crowd reports | Device hash (SHA256 of Device ID), no name/account required |  
| \*\*Cost Ceiling\*\* | Must survive on \< ₹5,000/month in hosting | Render.com \+ Supabase free tier \+ Cloudflare R2 |

\---

\#\# SECTION 8 — RISKS & MITIGATIONS

| Risk | Severity | Mitigation |  
|---|---|---|  
| Low data quality in early days | High | Seed 150 pandals manually before launch |  
| Committee members won't use web dashboard | Medium | Build mobile-first web form; WhatsApp integration for updates |  
| Crowd data becomes gameable / fake | Medium | Rate-limit by device hash; outlier filtering |  
| App unusable during network blackout (Chandannagar night) | High | Aggressive offline caching; all emergency data cached |  
| Photo storage costs spiral | Medium | Cloudflare R2 (near-zero egress cost); compress all uploads to 800px |  
| Festival dates change per Bengali calendar | Medium | Tithi data pulled from pre-computed 5-year calendar JSON |  
| Low adoption before first festival | High | Partner with 3–5 puja committees before launch for seed data |

\---

\#\# SECTION 9 — LAUNCH STRATEGY

\`\`\`  
T-60 days before Durga Puja 2026:  
  ✅ Partner with 5 puja committees (Kolkata \+ Chandannagar)  
  ✅ Seed 150 pandals in database  
  ✅ Beta test with 20 community volunteers

T-30 days:  
  ✅ Launch on Play Store (free, open-source)  
  ✅ Post on r/kolkata, r/westbengal  
  ✅ Share in Bengali Facebook groups and WhatsApp networks

T-7 days:  
  ✅ Festival countdown push notification to all users  
  ✅ Committee web dashboard goes live

T-0 (Festival week):  
  ✅ Live crowd reporting active  
  ✅ Community photo wall open  
  ✅ Emergency POIs updated with medical camps

Post-Festival (Nov — Jagat Dharti):  
  ✅ Archive locked for Durga Puja 2026  
  ✅ Switch festival toggle default to Jagat Dharti  
  ✅ Chandannagar lighting trail goes live  
\`\`\`

\---

\#\# SECTION 10 — OPEN SOURCE STRATEGY

\`\`\`  
GitHub Repository: github.com/festival-atlas/festival-atlas-android

License: GPL v3 (code) \+ CC BY 4.0 (data)

Contribution Guide:  
├── Code: Standard PR process  
├── Pandal Data: Via web dashboard (no code needed)  
├── Photos: Via in-app submission  
└── Oral Histories: Via web form

Community Channels:  
├── Discord server for contributors  
├── GitHub Discussions for feature requests  
└── Bengali-language Telegram group for local volunteers  
\`\`\`

\---

\#\# ✅ PRE-DEVELOPMENT CHECKLIST

Before writing a single line of code, complete:

\- \[ \] Finalize map engine: \*\*MapLibre GL\*\* confirmed  
\- \[ \] Register domain: \`festivalatlas.org\` \+ \`data.festivalatlas.org\`  
\- \[ \] Set up Supabase project with PostGIS enabled  
\- \[ \] Manually collect GPS coordinates for first 50 pandals (Chandannagar \+ Kolkata)  
\- \[ \] Contact 3 puja committees for data partnership  
\- \[ \] Set up GitHub repo with monorepo structure (\`/android\`, \`/web-dashboard\`, \`/backend\`, \`/data\`)  
\- \[ \] Design Bengali typography system (Hind Siliguri font)  
\- \[ \] Create a simple offline JSON file for 2026 tithis for both festivals  
\- \[ \] Define and document the GeoJSON pandal schema (v1)  
\- \[ \] Set up Cloudflare R2 bucket for photo storage

\---

This is your \*\*complete blueprint\*\* — culturally grounded in the realities of Chandannagar, Hooghly, and Krishnanagar's special celebrations and Kolkata's UNESCO-recognized Durga Puja heritage, and architecturally designed to survive on a community budget for decades. The dual-festival module approach means you build \*\*once\*\* and serve \*\*both\*\* Jagat Dharti and Durga Puja without duplication. Start with the MVP checklist above and work phase by phase. 🙏 