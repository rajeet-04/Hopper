\#\# Quick summary (what you’ll have at the end of planning)  
You’ll have (1) a \*\*feature-complete, prioritized backlog\*\* for an Android app that supports \*\*Durga Puja \+ Jagaddhatri/Jagadhatri Puja\*\*, (2) a \*\*clean open data model\*\* that can be exported as \*\*JSON/CSV/GeoJSON\*\*, (3) an \*\*offline-first technical architecture\*\* that still works under “congested network \= effectively offline”, and (4) a \*\*contributor portal \+ moderation \+ governance plan\*\* so the project can survive long-term as a community asset.

\> Note on naming: “jagat dharti” is almost certainly \*\*Jagaddhatri / Jagadhatri Puja\*\* (common spellings). It’s strongly associated with \*\*Chandannagar and Krishnanagar\*\* in Bengal. 

\---

\# Festival Atlas — Perfected Plan (Durga Puja \+ Jagadhatri Puja)

\#\# 1\) Product scope: explicitly support \*two\* festivals without duplicating the app  
\#\#\# Why this matters  
Durga Puja and Jagadhatri Puja have the same “navigation \+ crowds \+ archive” shape, but different \*\*timing, city clusters, crowd patterns, and archival value\*\*.

\- \*\*Durga Puja\*\*: massive pandal-hopping culture; recognized as “Durga Puja in Kolkata” on UNESCO’s Intangible Cultural Heritage list (inscribed 2021).     
\- \*\*Jagadhatri Puja\*\*: peaks in places like \*\*Chandannagar\*\* (often called “City of Lights” in popular writing) and \*\*Krishnanagar\*\*; celebrated around \*\*Kartik Shukla Navami (Oct–Nov window)\*\*; big lighting and immersion processions. 

\#\#\# Core design decision  
Build \*\*one platform\*\* with:  
\- \`Festival\` as a first-class entity (Durga vs Jagadhatri)  
\- \`Edition (Year)\` as a first-class entity (2026, 2025, …)  
\- \`Place\` as a reusable node (a committee/pandal location that appears each year)

This prevents “Durga app” and “Jagadhatri app” from becoming separate products.

\---

\# 2\) Personas & “must-win” user journeys (the ones that drive adoption)

\#\# A) The Pandal Hopper (tourist/local)  
\*\*Goal:\*\* “Given where I am right now, what’s the best thing to do next?”  
\- Open app → sees nearest pandals \+ “good next choice” route  
\- Wants \*fast decisions\* (distance \+ crowd \+ significance \+ open hours)

\#\# B) The Safety-first family group  
\*\*Goal:\*\* avoid crush zones, get to metro/medical quickly    
\- One-tap “Get Me Out” to safe nodes (metro/rail/police/medical)  
\- Clear, low-cognitive UI; offline resiliency

\#\# C) The Volunteer / Committee Member (contributor)  
\*\*Goal:\*\* update theme, add photos, update crowd time in 10 seconds    
\- Needs “idiot-proof” forms \+ moderation workflow    
\- Should not require GitHub

\#\# D) The Researcher / Journalist / Student  
\*\*Goal:\*\* download structured datasets \+ cite them    
\- Needs stable dumps, versioning, and permissive access via \`data.festivalatlas.org\`

\---

\# 3\) Feature set (expanded) — with priorities and “why it exists”

Below is a \*\*complete feature catalog\*\*, grouped as Must/Should/Could so you can plan without scope creep.

\#\# 3.1 MUST HAVE (MVP Utility — proves the app works in crowds)

\#\#\# 1\) Instant “Near Me” \+ best-next suggestion  
\- Show \*\*nearest pandal\*\* (distance in meters, quick theme snippet)  
\- “Best next” ranking formula (simple, transparent):  
  \- score \= distance\_weight \+ crowd\_weight \+ “featured/must-see” weight \+ user preference (e.g., “lighting”, “theme”, “heritage”)

\#\#\# 2\) Offline-first data cache (metadata \+ POIs \+ routes)  
\- Room DB holds:  
  \- pandals (POIs)  
  \- exit nodes (metro/rail/police/medical)  
  \- last-known crowd levels  
  \- precomputed walking connectors (see routing section)

\#\#\# 3\) Crowd indicator (Waze-like but simpler)  
\- 3-state indicator \+ estimated wait time:  
  \- 🟢 \~10 min, 🟡 \~25 min, 🔴 \~90 min (your PRD)  
\- Input UX: “Report crowd” → one tap on preset buckets

\#\#\# 4\) Emergency exits (“Get Me Out”)  
\- One button, then:  
  \- “Nearest Metro”  
  \- “Nearest Railway”  
  \- “Nearest Police”  
  \- “Nearest Medical”  
\- Works with weak connectivity (hybrid approach below)

\---

\#\# 3.2 SHOULD HAVE (improves retention \+ contribution flywheel)

\#\#\# 5\) Festival switch \+ City packs  
\- Toggle festival: \*\*Durga Puja\*\* / \*\*Jagadhatri Puja\*\*  
\- City packs (downloadable):  
  \- Kolkata (Durga)  
  \- Chandannagar (Jagadhatri)  
  \- Krishnanagar (Jagadhatri)  
This matches real festival geography (Jagadhatri is strongly tied to Chandannagar/Krishnanagar). 

\#\#\# 6\) Pandal detail pages (structured, not just text)  
\- Theme (this year)  
\- Committee/club name  
\- Artisan credits (idol maker, lighting, theme designer)  
\- Photos (this year)  
\- “Archive timeline” stub (even if empty in v0.1)

\#\#\# 7\) Filters that match real pandal-hopping behavior  
\- “Near me”  
\- “Low crowd”  
\- “Awarded / Featured”  
\- “Heritage / Historic”  
\- “Lighting-heavy” (especially for Jagadhatri city-of-lights vibe)

\#\#\# 8\) Basic itinerary builder (no fancy optimization)  
\- User selects 5–10 pandals → app orders them by:  
  \- proximity chaining \+ crowd penalty  
\- Save itinerary offline

\---

\#\# 3.3 COULD HAVE (delight features—keep them gated to later versions)

\#\#\# 9\) Virtual / remote pandal tour mode  
Kolkata Police’s Sharadotsav-style idea of virtual tours exists in the ecosystem (useful for crowd avoidance), so it’s a proven user desire.     
Your open version could be: “story cards \+ photo galleries \+ short lore”.

\#\#\# 10\) Oral histories (text now, audio later)  
\- Short “lore snippets” tied to:  
  \- committee  
  \- neighborhood history  
  \- artisan lineage

\#\#\# 11\) Trust & anti-spam for crowd reports  
\- Reputation score for reporters  
\- Rate limits per device  
\- Outlier detection (median \+ MAD)

\#\#\# 12\) Accessibility & safety layers  
\- “Wider roads preferred” route mode  
\- “Well-lit route” at night (especially relevant for Jagadhatri lighting circuits)  
\- “Medical camps near me” emphasis

\---

\# 4\) Data model (open-first) — the backbone of everything

This is the part that makes the project “Wikipedia \+ OSM \+ Festival navigation”.

\#\# 4.1 Entities (minimum viable schema)  
\#\#\# \`Festival\`  
\- \`id\`: \`durga\_puja\`, \`jagadhatri\_puja\`  
\- \`name\_en\`, \`name\_bn\`  
\- \`region\`, \`typical\_months\`  
\- \`about\_md\`

\#\#\# \`Place\` (the persistent identity of a pandal/committee)  
\- \`place\_id\` (stable UUID)  
\- \`festival\_id\`  
\- \`committee\_name\`  
\- \`neighborhood\`, \`city\`, \`district\`  
\- \`location\`: lat/lon (WGS84)  
\- \`tags\`: \`\["heritage","lighting","theme-heavy"\]\`

\#\#\# \`Edition\` (year-by-year snapshot)  
\- \`edition\_id\` (UUID)  
\- \`place\_id\`  
\- \`year\`  
\- \`theme\_title\`  
\- \`theme\_description\`  
\- \`artists\[\]\` (references)  
\- \`awards\[\]\`  
\- \`photos\[\]\` (references)  
\- \`sources\[\]\` (URLs or citations, not necessarily public-facing)

\#\#\# \`Person\` (artisan / artist)  
\- \`person\_id\`  
\- \`name\`  
\- \`role\`: idol\_maker / lighting / theme / etc  
\- \`location\`  
\- \`works\[\]\` (edition references)

\#\#\# \`LoreSnippet\`  
\- \`id\`  
\- \`place\_id\` or \`edition\_id\`  
\- \`language\`  
\- \`text\`  
\- \`attribution\`  
\- later: \`audio\_url\`

\#\#\# \`CrowdReport\`  
\- \`id\`  
\- \`place\_id\`  
\- \`timestamp\`  
\- \`bucket\`: green/yellow/red OR \`estimated\_wait\_min\`  
\- \`confidence\` (optional)  
\- \`reporter\_hash\` (privacy-preserving)

\#\#\# \`ExitNode\`  
\- \`id\`  
\- \`type\`: metro/rail/police/medical  
\- \`name\`  
\- \`location\`  
\- \`notes\`

\#\# 4.2 Export formats (hard requirement)  
\- JSON: canonical API format  
\- CSV: researchers  
\- GeoJSON: mapping tooling

\---

\# 5\) Maps \+ Offline: a practical plan that respects “offline \= congested”

\#\# 5.1 Map rendering stack recommendation (open-source)  
Use \*\*MapLibre Native (Android)\*\* for map display. 

\#\#\# Offline regions  
MapLibre provides an \*\*OfflineManager / offline regions\*\* concept (download a region for offline use).     
MapLibre also documents offline support in its Compose ecosystem (\`maplibre-compose\`). 

\#\#\# Important constraint: PMTiles  
MapLibre Android supports PMTiles sources, but \*\*PMTiles sources currently don’t support caching/offline pack downloads\*\* (per MapLibre example notes).     
So: PMTiles can be great later, but don’t bet MVP offline on PMTiles.

\#\# 5.2 “Graceful degradation” strategy (what users see when maps fail)  
Even if tiles fail, you can still show:  
\- A lightweight \*\*vector-less UI\*\*: user dot \+ compass bearing \+ list of nearest pandals  
\- A “radar view”: nearest POIs on a blank plane (works entirely from cached coordinates)

This ensures utility survives the worst network scenarios.

\---

\# 6\) Routing & “Get Me Out” — make it reliable without overengineering

\#\# 6.1 The routing problem  
True offline routing is hard. GraphHopper is open-source and can run in many environments including Android, but offline-on-Android has caveats and historical changes (community discussions note offline support complications beyond earlier versions; the old Android demo was tied to older releases). 

\#\# 6.2 Recommended hybrid approach (fast \+ resilient)  
\#\#\# MVP (works offline)  
\*\*Precompute and ship “escape connectors”\*\*:  
\- For each pandal (or grid cell), store polylines to nearest:  
  \- metro/rail/police/medical nodes  
\- Store 2–3 alternates (in case one route is blocked)

This avoids full offline routing and still delivers “Get Me Out” reliably.

\#\#\# Later upgrade (when budget/time allows)  
Run a server-side open routing engine (OSRM/Valhalla/GraphHopper server) \+ cache:  
\- If network OK → compute best walking route live  
\- If network bad → fall back to precomputed connectors

\---

\# 7\) Crowd intelligence (simple rules that produce stable estimates)

\#\# 7.1 Input UX (optimize for speed)  
\- One-tap report:  
  \- 🟢 / 🟡 / 🔴  
\- Optional: “Add note” (blocked lane / police barrier / entry closed)

\#\# 7.2 Aggregation logic (transparent \+ robust)  
For each \`place\_id\`:  
\- Take reports in last 30–45 minutes  
\- Convert bucket → minutes (10/25/90)  
\- Use \*\*weighted median\*\*:  
  \- weight higher for:  
    \- historically reliable reporters  
    \- multiple reports from different devices  
  \- decay weight with time

\#\# 7.3 Anti-abuse  
\- Rate limit: e.g., max 1 report / place / 10 min / device  
\- Detect floods: same device posting many places quickly  
\- “Soft moderation”: suspicious reports don’t count fully

\---

\# 8\) Contributor portal (v0.2) — idiot-proof, moderation-ready

\#\# 8.1 Portal features  
\- Map picker to drop a pin (lat/lon)  
\- Form sections:  
  \- Place info (committee, neighborhood)  
  \- Edition (year, theme, awards, artisans)  
  \- Crowd updates (optional)  
  \- Photo upload (with license choice)

\#\# 8.2 Moderation workflow (non-negotiable for open community)  
Roles:  
\- Contributor → submits changes  
\- Moderator → approves/rejects  
\- Admin → manages roles, lock pages

Every change creates:  
\- a diff record  
\- who changed it  
\- when  
\- revert button

\---

\# 9\) Open data \+ licensing: do it correctly from day 1

\#\# 9.1 OSM attribution & tile policies (if you show OSM-based maps)  
If you use OpenStreetMap data/tiles, you must provide proper attribution (“© OpenStreetMap contributors” and link to the copyright/license guidance).     
Also, if you consume official OSM tiles (\`tile.openstreetmap.org\`), there is a tile usage policy you must follow (and in practice, large festival traffic usually means you should \*\*host your own tiles\*\* or use a community-friendly provider). 

\#\# 9.2 Your dataset licensing recommendation  
Keep it simple and durable:  
\- \*\*Database (your festival POIs \+ archive)\*\*: ODbL or CC-BY-SA (choose one and stick to it)  
\- \*\*Media (photos/audio)\*\*: require contributors to pick a license (e.g., CC BY, CC BY-SA) \+ store that metadata

\---

\# 10\) Phased execution plan (improved) — with acceptance criteria

\#\# Version 0.1 (2 weeks): Utility MVP that survives congestion  
\*\*Deliverables\*\*  
\- Android app (Kotlin, Compose, MVVM, Room)  
\- Seed dataset: 100 pandals (split across:  
  \- Kolkata Durga cluster  
  \- Chandannagar Jagadhatri cluster  
  \- Krishnanagar Jagadhatri cluster)  
\- “Near me” \+ “Get me out”  
\- Crowd reporting \+ basic aggregation  
\- Data export endpoint (read-only)

\*\*Acceptance criteria\*\*  
\- App cold start to usable screen \< 2 seconds on mid-range phone  
\- With airplane mode:  
  \- can still find nearest pandal  
  \- can still find nearest exit node  
\- Crowd report can be submitted in \< 3 taps

\#\# Version 0.2: Contributor portal \+ moderation  
\- Next.js portal  
\- Auth \+ roles  
\- Audit logs \+ revert  
\- Upload pipeline for photos (even if archive UI is minimal)

\#\# Version 0.3: Cultural archive UI  
\- Year-by-year timeline per place  
\- Artisan linking  
\- “sources” \+ attribution fields visible

\#\# Version 0.4: Oral histories \+ formal Open API  
\- Lore snippets (text first)  
\- Publish OpenAPI schema \+ GeoJSON dumps \+ dataset versioning

\---

\# 11\) Engineering plan (so implementation is smooth)

\#\# 11.1 Backend services (minimal but correct)  
\- API service (Node/Go)  
\- Postgres \+ PostGIS  
\- Object storage for media (S3-compatible)  
\- Background jobs:  
  \- generate daily dumps (JSON/CSV/GeoJSON)  
  \- rebuild search indexes

\#\# 11.2 Android offline sync pattern  
\- Sync strategy: \*\*stale-while-revalidate\*\*  
  \- app uses cached data immediately  
  \- updates silently when network allows  
\- WorkManager jobs:  
  \- periodic dataset refresh  
  \- upload queued crowd reports when online

\#\# 11.3 Search  
\- Start simple: Postgres full-text search \+ trigram  
\- Later: Meilisearch/Typesense if needed

\---

\# 12\) Extra “high-value” features to add \*before coding\* (because they prevent rewrites)

These are small decisions now that save months later:

\#\# A) Make “Festival \+ Year” unavoidable in the UI and API  
Every query should include:  
\- selected festival  
\- selected year (default: current year, but user can switch)

\#\# B) Add “Source \+ Confidence” fields from day 1  
For each key field (theme, award, artisan):  
\- \`source\_type\`: committee / volunteer / news / unknown  
\- \`source\_url\` or \`source\_note\`  
\- \`confidence\`: low/med/high

This protects archival credibility.

\#\# C) Build “data quality” as a visible status  
Each place page shows:  
\- completeness meter (e.g., has coords, has 2026 theme, has at least 1 photo, has artisan credits)  
This nudges community contributions.

\---

\#\# Completeness pass against your PRD  
\- Open archive (timeline, artisans, lore): covered with concrete schema \+ phased UI  
\- Puja near me \+ offline maps: covered with MapLibre \+ graceful degradation  
\- Crowd intelligence: covered with reporting \+ aggregation \+ anti-abuse  
\- Emergency exit navigation: covered with offline-safe hybrid routing  
\- Open data repo: covered with dumps \+ formats \+ versioning \+ licensing basics

\---

If you want to start building with zero uncertainty, treat the sections \*\*(4) Data model\*\*, \*\*(6) Routing hybrid\*\*, and \*\*(10) Acceptance criteria\*\* as your “no-regrets locks”. They’re the pieces that, if vague, cause the most rework later. 