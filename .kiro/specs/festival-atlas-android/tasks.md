# Implementation Plan: Festival Atlas Android (Hopper)

## Overview

This plan implements the offline-first Android festival navigation app using Clean Architecture with Hilt DI, Room database, MapLibre GL Native, WorkManager, and Jetpack Compose. Tasks are ordered to build foundational layers first (domain models, database, DI), then core features (map, offline data, festival toggle), followed by feature modules (crowd reporting, exit routing, itinerary, etc.), and finally integration and polish.

## Tasks

- [ ] 1. Project setup, dependencies, and core infrastructure
  - [x] 1.1 Configure Gradle dependencies and version catalog
    - Add Hilt, Room, MapLibre GL Native, WorkManager, Navigation Compose, DataStore, Kotlin Coroutines, and testing libraries to `libs.versions.toml` and `app/build.gradle.kts`
    - Add KSP plugin for Room and Hilt annotation processing
    - Configure minSdk=26, targetSdk=36
    - _Requirements: 9.4_

  - [x] 1.2 Create package structure and Hilt application class
    - Create `com.example.hopper` package hierarchy: `di/`, `data/local/db/dao/`, `data/local/db/entity/`, `data/local/assets/`, `data/remote/api/`, `data/remote/sync/`, `data/repository/`, `domain/model/`, `domain/repository/`, `domain/usecase/`, `ui/`, `util/`
    - Create `HopperApplication.kt` annotated with `@HiltAndroidApp`
    - Update `AndroidManifest.xml` with application class, permissions (INTERNET, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, VIBRATE)
    - _Requirements: 9.1, 9.5_

  - [-] 1.3 Implement core domain models
    - Create all domain model data classes: `Pandal`, `ExitNode`, `CrowdReport`, `CrowdBucket`, `Festival`, `FestivalContext`, `Tithi`, `LatLng`, `SourceType`, `ConfidenceLevel`, `ArtisanCredits`, `ExitNodeCategory`, `ExitRoute`
    - Create enums: `Festival`, `CrowdBucket`, `ExitNodeCategory`, `SourceType`, `ConfidenceLevel`
    - _Requirements: 1.1, 2.1, 3.2, 4.2, 5.1_

  - [-] 1.4 Implement utility classes
    - Create `HaversineCalculator.kt` with distance calculation between two `LatLng` points
    - Create `DeviceHashUtil.kt` with SHA-256 hashing of `Settings.Secure.ANDROID_ID`
    - Create `DateTimeUtils.kt` for epoch/Instant conversions
    - Create `LocationUtils.kt` for coordinate helpers
    - _Requirements: 3.3, 5.3, 11.1_

  - [ ]* 1.5 Write unit tests for HaversineCalculator and DeviceHashUtil
    - Test known coordinate pairs produce expected distances (±1m)
    - Test SHA-256 produces deterministic output for known input
    - Test edge cases: same point returns 0, antipodal points
    - _Requirements: 3.3, 11.1_

- [ ] 2. Room database and entity layer
  - [~] 2.1 Create Room entity classes
    - Implement `PandalEntity`, `ExitNodeEntity`, `ConnectorEntity`, `CrowdReportEntity`, `TithiEntity`, `EditionEntity` with all fields per design
    - Implement `LightTrailEntity`, `BhogPinEntity`, `ProcessionEntity`, `ProcessionReportEntity`, `HistoricalCrowdPatternEntity`
    - Implement `LostPersonPostEntity`, `OralHistoryEntity`, `HeritagePointEntity`, `ReputationEntity`
    - Implement `VolunteerPostEntity`, `VolunteerSignupEntity`, `RitualGuideEntity`, `AudioAssetEntity`
    - Implement `ItineraryEntity` and `ItineraryStopEntity`
    - Create `Converters.kt` type converter class for JSON arrays, enums, and timestamps
    - _Requirements: 8.1, 8.2, 8.3, 8.6_

  - [~] 2.2 Create Room DAO interfaces
    - Implement `PandalDao` with queries: getByFestivalAndYear, getById, getNearestPandals (ordered by lat/lng proximity), search
    - Implement `ExitNodeDao` with queries: getByCategory, getNearestByCategory
    - Implement `CrowdReportDao` with queries: getActiveReportsForPandal (non-expired), insert, getLatestByDeviceAndPandal (rate limit check), deleteExpired
    - Implement `CalendarDao` with queries: getTithisByFestivalAndYear, getCurrentTithi
    - Implement `ItineraryDao`, `LightTrailDao`, `BhogDao`, `ProcessionDao`
    - Implement `LostPersonDao`, `OralHistoryDao`, `HeritageDao`, `ReputationDao`, `VolunteerDao`, `RitualGuideDao`
    - _Requirements: 8.1, 8.2, 5.5, 5.7_

  - [~] 2.3 Create HopperDatabase and DatabaseModule
    - Implement `HopperDatabase.kt` abstract class with all DAO accessors
    - Create `di/DatabaseModule.kt` Hilt module providing Room database singleton and all DAO instances
    - Configure Room with fallback to destructive migration for development
    - _Requirements: 8.1_

  - [ ]* 2.4 Write unit tests for Room DAOs
    - Test PandalDao festival/year filtering returns only matching entries
    - Test CrowdReportDao expiry query excludes reports older than 20 minutes
    - Test CrowdReportDao rate limit query detects reports within 10-minute window
    - Use in-memory Room database for tests
    - _Requirements: 2.4, 2.5, 2.6, 5.5, 5.7_

- [ ] 3. Offline data loading and GeoJSON asset layer
  - [~] 3.1 Implement GeoJsonAssetLoader
    - Create `GeoJsonAssetLoader.kt` that reads bundled GeoJSON files from `assets/` folder
    - Parse GeoJSON FeatureCollection into `PandalEntity` list
    - Parse exit node GeoJSON into `ExitNodeEntity` list
    - Parse connector GeoJSON into `ConnectorEntity` list
    - Implement first-launch database seeding from bundled assets
    - _Requirements: 1.1, 8.1, 8.2, 8.3_

  - [~] 3.2 Create bundled asset files
    - Create sample `assets/pandals_durga_puja_2026.geojson` with pandal data structure
    - Create sample `assets/pandals_jagaddhatri_puja_2026.geojson`
    - Create sample `assets/exit_nodes.geojson` with Metro, Railway, Police, Medical nodes
    - Create sample `assets/connectors.geojson` with precomputed walking polylines
    - Create sample `assets/calendar.json` with tithi data for both festivals
    - Create sample `assets/historical_crowd_patterns.json`
    - _Requirements: 1.1, 1.3, 4.3, 6.2, 8.1, 8.2, 8.3, 18.5_

  - [~] 3.3 Implement database prepopulation on first launch
    - Create `DatabasePrepopulateCallback` that loads all bundled assets into Room on first install
    - Wire callback into `DatabaseModule` Room builder
    - Ensure prepopulation runs only once (check via shared preferences flag)
    - _Requirements: 1.1, 8.1_

  - [ ]* 3.4 Write unit tests for GeoJsonAssetLoader
    - Test parsing valid GeoJSON produces correct PandalEntity list
    - Test malformed GeoJSON features are skipped gracefully
    - Test all required fields are mapped correctly
    - _Requirements: 1.1, 8.1_

- [~] 4. Checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 5. Bilingual language support and theming
  - [~] 5.1 Implement LocaleManager and StringProvider
    - Create `LocaleManager.kt` with SharedPreferences-backed locale state, StateFlow emission, and system default detection
    - Create `StringProvider.kt` with `resolve()` and `resolveNullable()` methods for bilingual field resolution
    - Create `di/LocaleModule.kt` Hilt module providing LocaleManager and StringProvider singletons
    - _Requirements: 10.1, 10.2, 10.3, 10.4_

  - [~] 5.2 Implement HopperTheme and typography
    - Create `ui/theme/Typography.kt` with Hind Siliguri (Bengali) and Inter (Latin) font families
    - Create `ui/theme/HopperTheme.kt` composable wrapping MaterialTheme with locale-aware typography
    - Create `ui/theme/NightSafetyTheme.kt` with high-contrast dark theme, increased text size, 48dp minimum tap targets
    - Bundle font files in `res/font/` directory
    - _Requirements: 7.1, 10.3_

  - [ ]* 5.3 Write property test for locale resolution
    - **Property 11: Locale resolution for bilingual fields**
    - For any entity with English and Bengali fields, verify correct field is returned based on active locale with proper fallback behavior
    - **Validates: Requirements 10.2, 10.4**

- [ ] 6. Festival toggle and location services
  - [~] 6.1 Implement FestivalToggleController
    - Create `FestivalToggleController` interface and implementation with `StateFlow<FestivalContext>`
    - Implement `getDefaultFestival()` using proximity to festival dates from bundled calendar
    - Persist selected festival/year in DataStore
    - Create Hilt module providing FestivalToggleController singleton
    - _Requirements: 2.1, 2.2, 2.3, 2.6_

  - [~] 6.2 Implement LocationProvider
    - Create `LocationProvider` interface with `StateFlow<LatLng?>` and `StateFlow<Boolean>` for availability
    - Implement using FusedLocationProviderClient with 10-second polling interval
    - Implement stationary detection: pause GPS after 2 minutes of no movement, resume on motion
    - Create `di/LocationModule.kt` Hilt module
    - _Requirements: 9.2, 9.3_

  - [ ]* 6.3 Write property test for festival context filtering
    - **Property 1: Festival context filtering**
    - For any pandal dataset with entries from both festivals and multiple years, verify all query results contain only pandals matching both active festival AND active year
    - **Validates: Requirements 2.4, 2.5, 2.6**

  - [ ]* 6.4 Write property test for default festival selection
    - **Property 2: Default festival selection by date proximity**
    - For any calendar date, verify the default festival selection is the festival whose scheduled dates are nearest
    - **Validates: Requirements 2.3**

- [ ] 7. Repository layer - core features
  - [~] 7.1 Implement PandalRepository
    - Create `PandalRepository` interface in domain layer
    - Implement `PandalRepositoryImpl` with Room DAO queries filtered by festival/year context
    - Implement composite scoring algorithm: `0.5*distance + 0.3*crowd + 0.2*significance`
    - Implement `getNearestPandals()` using Haversine distance calculation in-memory
    - _Requirements: 1.1, 3.1, 3.2, 3.3, 3.4, 3.5_

  - [~] 7.2 Implement ExitRouterRepository
    - Create `ExitRouterRepository` interface in domain layer
    - Implement `ExitRouterRepositoryImpl` with nearest exit node per category using Haversine
    - Implement walking time estimation: `ceil(distanceMeters / (5000/60))` minutes
    - Implement Night Safety Mode route preference (prefer `isWellLit=true` connectors)
    - Implement alternate route retrieval (at least 2 connectors per pandal-exit pair)
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5, 4.6, 7.2_

  - [~] 7.3 Implement CrowdReportRepository
    - Create `CrowdReportRepository` interface in domain layer
    - Implement `CrowdReportRepositoryImpl` with: submit report (with device hash), rate limit check (10 min per pandal per device), expiry cleanup (20 min), weighted median aggregation
    - Implement `getAggregatedCrowd()` returning Flow of current bucket using weighted median of non-expired reports
    - Queue reports locally when offline (`isSynced=false`)
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5, 5.6, 5.7, 11.1, 11.2_

  - [~] 7.4 Implement CalendarRepository
    - Create `CalendarRepository` interface in domain layer
    - Implement `CalendarRepositoryImpl` with tithi queries by festival/year, current tithi detection
    - _Requirements: 6.1, 6.2, 6.3, 6.4_

  - [~] 7.5 Create RepositoryModule for Hilt DI
    - Create `di/RepositoryModule.kt` binding all repository interfaces to implementations
    - _Requirements: 8.1_

  - [ ]* 7.6 Write property tests for core repositories
    - **Property 3: Composite score sorting invariant** — verify Puja Near Me list sorted ascending by composite score
    - **Property 4: Exit router nearest-per-category** — verify nearest exit node returned per category by Haversine
    - **Property 5: Walking time calculation consistency** — verify time = ceil(distance / speed)
    - **Validates: Requirements 3.1, 4.2, 4.4, 14.3**

  - [ ]* 7.7 Write property tests for crowd reporting
    - **Property 6: Crowd report privacy invariant** — verify reports contain only pandalId, bucket, deviceHash, timestamp
    - **Property 7: Crowd report expiry** — verify reports excluded after 20 minutes
    - **Property 8: Weighted median crowd aggregation** — verify correct weighted median calculation
    - **Property 9: Crowd report rate limiting** — verify rejection within 10-minute window
    - **Validates: Requirements 5.3, 5.5, 5.6, 5.7, 11.1, 11.2, 11.4**

- [~] 8. Checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 9. Domain use cases
  - [~] 9.1 Implement core navigation use cases
    - Create `GetNearestPandalsUseCase` combining PandalRepository with LocationProvider and FestivalToggleController
    - Create `GetExitRoutesUseCase` combining ExitRouterRepository with LocationProvider
    - Create `SubmitCrowdReportUseCase` with DeviceHashUtil and rate limit enforcement
    - Create `BuildItineraryUseCase` with nearest-neighbor + crowd penalty algorithm (RED = 2x distance multiplier)
    - Create `ToggleFestivalUseCase` for switching festival context
    - _Requirements: 3.1, 4.2, 5.1, 14.1, 14.2, 2.2_

  - [~] 9.2 Implement feature-specific use cases
    - Create `GetLightTrailUseCase` (only returns data when Jagaddhatri Puja active)
    - Create `ProcessionTrackerUseCase` with observe, report, and ETA methods
    - Create `GetBhogPinsUseCase` and `SubmitBhogReportUseCase`
    - Create `GetPredictiveWaitTimesUseCase` with heat timeline generation and peak summary
    - _Requirements: 15.1, 16.1, 16.3, 17.1, 18.1_

  - [~] 9.3 Implement community feature use cases
    - Create `SubmitLostPersonPostUseCase` and `GetNearbyLostPersonPostsUseCase` (2km radius filter)
    - Create `GetOralHistoriesUseCase` and `DownloadOralHistoryAudioUseCase`
    - Create `GetHeritagePointsUseCase` (only returns data when Jagaddhatri Puja active)
    - Create `GetVolunteerPostsUseCase` and `SignUpForVolunteerShiftUseCase`
    - Create `GetRitualGuidesUseCase` and `GetRitualGuideForTithiUseCase`
    - _Requirements: 22.1, 22.3, 23.1, 24.1, 26.1, 27.1_

  - [ ]* 9.4 Write property tests for itinerary builder
    - **Property 12: Itinerary nearest-neighbor with crowd penalty** — verify RED pandals get 2x distance multiplier and appear later in sequence
    - **Property 13: Itinerary distance invariant** — verify totalDistanceKm equals sum of all stop distances
    - **Validates: Requirements 14.1, 14.2, 14.3, 14.5**

  - [ ]* 9.5 Write property tests for community features
    - **Property 18: Lost person post radius filtering** — verify only posts within 2km returned
    - **Property 19: Lost person post expiry** — verify posts excluded after 2 hours or when resolved
    - **Property 20: Lost person post privacy invariant** — verify no PII beyond display name
    - **Property 23: Heritage layer festival-conditional visibility** — verify empty results when Durga Puja active
    - **Validates: Requirements 22.3, 22.4, 22.5, 22.7, 24.1, 24.5**

- [ ] 10. Repository layer - extended features
  - [~] 10.1 Implement LightTrailRepository
    - Create interface and implementation for light trail data queries
    - Filter by festival (Jagaddhatri Puja only) and year
    - Return ordered stops with route polyline
    - _Requirements: 15.1, 15.2, 15.3, 15.5_

  - [~] 10.2 Implement BishorjonRepository
    - Create interface and implementation for procession tracking
    - Implement `getActiveProcessions()` as Flow, `submitProcessionReport()`, `expireStaleProcessionReports()` (15-min expiry)
    - Implement offline fallback with `getLastKnownProcessions()`
    - _Requirements: 16.1, 16.5, 16.6, 16.7_

  - [~] 10.3 Implement BhogRepository
    - Create interface and implementation for bhog/food pin management
    - Implement category filtering, distance calculation from user, expiry logic (`min(endTime, reportedAt + 2h)`)
    - Implement report submission with offline queuing
    - _Requirements: 17.1, 17.2, 17.3, 17.4, 17.5, 17.6, 17.7_

  - [~] 10.4 Implement LostPersonRepository
    - Create interface and implementation for lost person bulletin board
    - Implement 2km radius filtering using Haversine, 2-hour auto-expiry, post resolution
    - Implement offline queuing for submissions
    - _Requirements: 22.1, 22.2, 22.3, 22.4, 22.5, 22.6, 22.7_

  - [~] 10.5 Implement OralHistoryRepository
    - Create interface and implementation for oral history entries
    - Implement audio download and local caching in `filesDir/oral_history/`
    - Track `isAudioCachedLocally` flag
    - _Requirements: 23.1, 23.2, 23.5, 23.6, 23.7_

  - [~] 10.6 Implement HeritageRepository
    - Create interface and implementation for heritage points
    - Filter by festival (Jagaddhatri Puja only)
    - _Requirements: 24.1, 24.2, 24.3, 24.4, 24.5_

  - [~] 10.7 Implement ReputationRepository
    - Create interface and implementation for reporter reputation
    - Implement accuracy scoring algorithm (compare against next 3 reports from other devices)
    - Implement badge tier calculation (BRONZE: 10+, SILVER: 25+ & 60%, GOLD: 50+ & 80%)
    - Implement weighted report calculation (1.5x for >0.7 accuracy, 2.0x for >0.9)
    - Implement leaderboard query (badge tier, accuracy, count only — no identity)
    - _Requirements: 25.1, 25.2, 25.3, 25.4, 25.5, 25.6_

  - [~] 10.8 Implement VolunteerRepository
    - Create interface and implementation for volunteer post management
    - Implement sign-up with encrypted contact storage, filled-status check, expiry logic
    - _Requirements: 26.1, 26.2, 26.3, 26.4, 26.5, 26.6, 26.7_

  - [~] 10.9 Implement RitualGuideRepository
    - Create interface and implementation for ritual guides and audio assets
    - Implement audio download/caching in `filesDir/ritual_audio/`
    - Implement tithi linkage queries
    - Implement cache size tracking and clearing
    - _Requirements: 27.1, 27.2, 27.3, 27.4, 27.5, 27.6, 27.7_

  - [ ]* 10.10 Write property tests for extended features
    - **Property 14: Procession report expiry** — verify reports marked stale after 15 minutes
    - **Property 15: Bhog pin category filtering and expiry** — verify category filter and expiry logic
    - **Property 21: Oral history pandal association** — verify non-null pandalId on all entries
    - **Validates: Requirements 16.6, 17.1, 17.2, 23.1, 23.6**

  - [ ]* 10.11 Write property tests for reputation and volunteer modules
    - **Property 24: Crowd reporter badge threshold** — verify badge tier thresholds (BRONZE≥10, SILVER≥25&60%, GOLD≥50&80%)
    - **Property 25: Accuracy score computation** — verify comparison against next 3 reports from other devices
    - **Property 26: Reputation-weighted crowd aggregation** — verify weight multipliers (1.5x for >0.7, 2.0x for >0.9)
    - **Property 28: Volunteer post expiry** — verify exclusion after timeSlotEnd
    - **Property 30: Volunteer post filled threshold** — verify rejection when volunteersSignedUp >= volunteersNeeded
    - **Validates: Requirements 25.1, 25.2, 25.4, 26.4, 26.7**

- [~] 11. Checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 12. Map engine integration
  - [~] 12.1 Implement MapEngineController interface and MapLibre wrapper
    - Create `MapEngineController` interface with all methods per design (pandal pins, exit nodes, route polylines, overlays)
    - Implement MapLibre GL Native wrapper using Compose `AndroidView`
    - Implement `loadOfflineRegion()` for tile caching (Kolkata, Chandannagar, Krishnanagar bounds, zoom 10-16, <50MB)
    - Implement `setGeoJsonSource()` for dynamic layer management
    - _Requirements: 1.1, 1.2, 1.3_

  - [~] 12.2 Implement offline tile management
    - Configure `OfflineManager` with `OfflineTilePyramidRegionDefinition` for each city zone
    - Implement tile download on first launch or when network available
    - Implement silent background refresh via WorkManager when connectivity restores
    - _Requirements: 1.3, 1.5, 8.4_

  - [~] 12.3 Implement Graceful Degradation view
    - Create `GracefulDegradationView.kt` compass/radar composable showing directional indicators to nearest pandals and exits
    - Implement automatic switch when tile loading fails completely
    - Implement automatic transition back to full map when tiles become available
    - Display pandal names, distances, and crowd indicators in list format alongside compass
    - _Requirements: 1.4, 12.1, 12.2, 12.3, 12.4_

  - [~] 12.4 Implement Night Safety Mode map styling
    - Create dark high-contrast map style JSON for MapLibre
    - Implement `setNightSafetyStyle()` toggle
    - Increase visual weight of Police/Medical exit node pins in night mode
    - Implement automatic activation based on local sunset time
    - _Requirements: 7.1, 7.3, 7.4_

  - [ ]* 12.5 Write property test for Night Safety route preference
    - **Property 10: Night safety route preference** — verify well-lit routes preferred over shorter unlit routes when Night Safety Mode active
    - **Validates: Requirements 7.2**

- [ ] 13. UI layer - Map screen and core navigation
  - [~] 13.1 Implement MapScreen and MapViewModel
    - Create `MapScreen.kt` with MapLibre map view, pandal pins, exit node pins, user location marker
    - Create `MapViewModel.kt` managing map state, pin data, festival context, and night mode toggle
    - Implement persistent "Get Me Out" button overlay accessible with single tap
    - Implement festival toggle UI control in map toolbar
    - Wire location updates to map centering
    - _Requirements: 1.1, 1.2, 2.1, 2.2, 4.1_

  - [~] 13.2 Implement NearMeScreen and NearMeViewModel
    - Create `NearMeScreen.kt` displaying nearest pandals sorted by composite score
    - Show each entry with: name, distance (meters), crowd bucket indicator, theme description
    - Implement auto-recalculation when location changes by >100m
    - Display minimum 5 nearest pandals
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5_

  - [~] 13.3 Implement PandalDetailSheet and PandalDetailViewModel
    - Create `PandalDetailSheet.kt` bottom sheet with: theme, committee name, established year, artisan credits, photo gallery (horizontal scroll, up to 10), awards section, archive timeline stub
    - Implement data provenance indicator (Committee Verified vs Community Sourced)
    - Implement confidence level badge (low/medium/high)
    - Implement archive timeline expansion showing Edition history
    - All content rendered from offline cache
    - _Requirements: 13.1, 13.2, 13.3, 13.4, 13.5, 13.6, 13.7, 20.3, 20.6_

  - [~] 13.4 Implement ExitRouterSheet and ExitRouterViewModel
    - Create `ExitRouterSheet.kt` bottom sheet showing nearest exit node per category (Metro, Railway, Police, Medical)
    - Display walking distance and estimated time for each option
    - Show route polyline on map when user selects an exit
    - Implement alternate route display
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5, 4.6_

  - [~] 13.5 Implement CrowdReportSheet and CrowdReportViewModel
    - Create `CrowdReportSheet.kt` with 3 selectable crowd buckets (green/yellow/red)
    - Ensure complete submission in ≤3 taps from map screen
    - Show current reporter badge tier on submission sheet
    - Display rate limit feedback if reporting too frequently
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.7_

- [ ] 14. UI layer - Calendar and Itinerary
  - [~] 14.1 Implement CalendarScreen and CalendarViewModel
    - Create `CalendarScreen.kt` displaying complete festival calendar with tithi dates
    - Highlight current tithi and peak crowd days (Ashtami, Navami) with visual emphasis
    - Display each tithi with name, date, and cultural significance in Bengali and English
    - Show "Ritual Guide" chip on tithis with linked guides
    - _Requirements: 6.1, 6.2, 6.3, 6.4, 27.6_

  - [~] 14.2 Implement ItineraryScreen and ItineraryViewModel
    - Create `ItineraryScreen.kt` with pandal selection (5-10 pandals), route display, and stop list
    - Display each stop with: sequence number, pandal name, distance from previous, estimated arrival, crowd indicator
    - Show total walking distance (km) and total time (minutes)
    - Implement live ETA updates as user location changes
    - Save itinerary to offline cache
    - _Requirements: 14.1, 14.2, 14.3, 14.4, 14.5, 14.6, 14.7_

  - [~] 14.3 Implement HeatTimelineBar component
    - Create `HeatTimelineBar.kt` composable showing hourly crowd predictions (6PM-2AM range)
    - Color segments by predicted CrowdBucket (green/yellow/red)
    - Highlight current hour with marker
    - Display textual peak summary below bar
    - Overlay live crowd data with higher visual priority when available
    - _Requirements: 18.1, 18.2, 18.3, 18.4, 18.6_

- [~] 15. Checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 16. UI layer - Jagaddhatri Puja features
  - [~] 16.1 Implement LightTrailScreen and LightTrailViewModel
    - Create `LightTrailScreen.kt` full-screen map with trail overlay, sequential stop markers, route polyline
    - Display each installation with: artist name, dimensions, theme description
    - Mark vantage points with viewing-angle indicator arcs
    - Show total walking distance and estimated time
    - Only available when Jagaddhatri Puja is active festival
    - _Requirements: 15.1, 15.2, 15.3, 15.4, 15.5, 15.6, 15.7_

  - [~] 16.2 Implement BishorjonTrackerSheet and BishorjonTrackerViewModel
    - Create `BishorjonTrackerSheet.kt` bottom sheet with active processions list, ETA display, report button (≤3 taps)
    - Implement animated polylines with directional arrows on map
    - Implement proximity alert logic: audio + vibration when procession within 500m
    - Show staleness indicators on stale segments (>15 min)
    - Display last-known positions when offline
    - _Requirements: 16.1, 16.2, 16.3, 16.4, 16.5, 16.6, 16.7_

  - [~] 16.3 Implement HeritageDetailSheet and HeritageViewModel
    - Create `HeritageDetailSheet.kt` bottom sheet with heritage point details (name, description, period, photo)
    - Implement heritage layer toggle in map toolbar
    - Use distinct icon/color (sepia/brown monument icon) for heritage pins
    - Only render when Jagaddhatri Puja is active
    - _Requirements: 24.1, 24.2, 24.3, 24.4, 24.5, 24.6_

- [ ] 17. UI layer - Community features
  - [~] 17.1 Implement BhogFinderSheet and BhogFinderViewModel
    - Create `BhogFinderSheet.kt` bottom sheet with category toggle (Bhog Distribution / Street Food)
    - Display pin list sorted by distance with: name, committee/description, time, rating
    - Implement quick-report button (≤3 taps)
    - Show distance from user on each pin
    - _Requirements: 17.1, 17.2, 17.3, 17.4, 17.5, 17.6, 17.7_

  - [~] 17.2 Implement LostPersonBoardSheet and LostPersonBoardViewModel
    - Create `LostPersonBoardSheet.kt` bottom sheet with "Post My Location" button and active posts list sorted by distance
    - Auto-populate location from GPS
    - Display posts within 2km radius
    - Implement manual resolution by original poster
    - Show expiry countdown on each post
    - No account creation required
    - _Requirements: 22.1, 22.2, 22.3, 22.4, 22.5, 22.6, 22.7_

  - [~] 17.3 Implement OralHistoryScreen and OralHistoryViewModel
    - Create `OralHistoryScreen.kt` list screen with: title, contributor, year, text preview, audio playback button
    - Implement audio playback using MediaPlayer (streaming when online, local when cached)
    - Implement download button with progress indicator for offline caching
    - Show offline availability indicator (cloud icon / checkmark)
    - _Requirements: 23.1, 23.2, 23.5, 23.6, 23.7_

  - [~] 17.4 Implement LeaderboardScreen and LeaderboardViewModel
    - Create `LeaderboardScreen.kt` displaying community leaderboard with badge tiers, accuracy scores, report counts
    - No personal identity exposed — only badge tier, accuracy, and count
    - Show current device's reputation status
    - _Requirements: 25.1, 25.2, 25.3, 25.5_

  - [~] 17.5 Implement VolunteerScreen and VolunteerViewModel
    - Create `VolunteerScreen.kt` list screen with: role, location, date, time, spots remaining
    - Implement sign-up flow (name + phone number, no account required)
    - Show filled status, hide contact info from public view
    - Filter by active festival/year
    - _Requirements: 26.1, 26.2, 26.3, 26.4, 26.5, 26.6, 26.7_

  - [~] 17.6 Implement RitualGuideScreen and RitualGuideViewModel
    - Create `RitualGuideScreen.kt` with expandable step-by-step instructions, timing notes, linked audio
    - Implement audio download and playback with progress indicator
    - Show all content in Bengali and English
    - Implement navigation from calendar tithi chip to corresponding guide
    - _Requirements: 27.1, 27.2, 27.3, 27.4, 27.5, 27.6, 27.7_

  - [ ]* 17.7 Write property tests for data provenance and display
    - **Property 17: Data provenance defaults** — verify unknown source and low confidence assigned when no attribution
    - **Property 22: Oral history display completeness** — verify all required fields rendered
    - **Property 27: Leaderboard anonymity** — verify no personal identity exposed
    - **Property 29: Volunteer contact access control** — verify contact info not exposed in Android UI
    - **Validates: Requirements 20.4, 23.7, 25.3, 26.5**

- [~] 18. Checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 19. Background sync and WorkManager
  - [~] 19.1 Implement DataSyncWorker
    - Create `DataSyncWorker.kt` CoroutineWorker for periodic background sync
    - Implement stale-while-revalidate: check `If-Modified-Since` / `ETag` headers
    - Sync pandal data, exit node data, crowd aggregations
    - Configure constraints: `NetworkType.CONNECTED`, `BatteryNotLow`
    - Configure exponential backoff retry (30s start)
    - Schedule as periodic work (15-minute minimum interval)
    - _Requirements: 1.5, 8.4, 8.5_

  - [~] 19.2 Implement CrowdUploadWorker
    - Create `CrowdUploadWorker.kt` for uploading queued crowd reports when connectivity restores
    - Query Room for `isSynced=false` reports, upload, mark as synced
    - _Requirements: 5.4_

  - [~] 19.3 Implement feature-specific sync workers
    - Create `BhogUploadWorker.kt` for queued bhog/food reports
    - Create `ProcessionUploadWorker.kt` for queued procession sighting reports
    - Create `LostPersonUploadWorker.kt` for queued lost person posts
    - Create `VolunteerSyncWorker.kt` for volunteer data sync
    - _Requirements: 16.7, 17.6, 22.6_

  - [~] 19.4 Configure WorkManager scheduling in Hilt module
    - Create WorkManager Hilt module with worker factories
    - Schedule all periodic and one-time workers with appropriate constraints
    - Ensure unique work names prevent duplicate scheduling
    - _Requirements: 8.4_

- [ ] 20. Navigation and app wiring
  - [~] 20.1 Implement Jetpack Navigation Compose graph
    - Create navigation graph with routes for: Map, NearMe, Calendar, Itinerary, LightTrail, OralHistory, Leaderboard, Volunteer, RitualGuide
    - Implement bottom navigation bar with primary destinations
    - Implement deep link support for pandal detail and calendar views
    - Wire all screens to navigation controller
    - _Requirements: 1.1, 2.1_

  - [~] 20.2 Wire MainActivity with Hilt and theme
    - Update `MainActivity.kt` with `@AndroidEntryPoint` annotation
    - Set content to `HopperTheme` wrapping navigation graph
    - Implement language toggle in settings/toolbar
    - Implement Night Safety Mode toggle with automatic sunset detection
    - _Requirements: 7.1, 7.4, 10.1_

  - [~] 20.3 Implement data expiry and cleanup scheduling
    - Create periodic cleanup job for: expired crowd reports (20 min), expired procession reports (15 min), expired bhog pins, expired lost person posts (2 hours), expired volunteer posts
    - Schedule via WorkManager or coroutine scope on app launch
    - _Requirements: 5.5, 16.6, 17.2, 22.4, 26.4_

- [ ] 21. Remote API service layer (post-MVP prep)
  - [~] 21.1 Implement HopperApiService with Retrofit
    - Create `HopperApiService.kt` Retrofit interface with endpoints matching Live API spec
    - Define DTOs for API responses (pandals, crowd, calendar, artists)
    - Configure base URL, JSON serialization, and timeout (10s)
    - Create Hilt module providing Retrofit instance and API service
    - _Requirements: 28.1, 28.2, 28.4_

  - [ ]* 21.2 Write property test for ritual guide tithi linkage
    - **Property 31: Ritual guide tithi linkage** — verify linked tithiId references existing tithi in calendar data for same festival
    - **Validates: Requirements 27.6**

- [~] 22. Checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 23. Integration testing and final validation
  - [ ]* 23.1 Write integration tests for offline-first flow
    - Test app launches with no network and displays cached pandal pins within 3 seconds
    - Test festival toggle reloads correct dataset within 500ms
    - Test crowd report queued offline and uploaded on connectivity restore
    - Test graceful degradation activates when tiles unavailable
    - _Requirements: 1.1, 1.2, 1.4, 2.2, 5.4_

  - [ ]* 23.2 Write integration tests for community features
    - Test lost person post submission with GPS auto-population
    - Test bhog report 3-tap submission flow
    - Test procession proximity alert triggers at 500m
    - Test oral history audio download and offline playback
    - _Requirements: 16.4, 17.5, 22.1, 22.2, 23.5_

  - [ ]* 23.3 Write property test for predictive timeline and API format
    - **Property 16: Predictive timeline generation** — verify one prediction per hour matching stored patterns, live data has higher priority
    - **Property 32: API rate limiting** — verify 429 response when exceeding limits
    - **Property 33: API JSON response validity** — verify valid JSON with data and meta fields
    - **Validates: Requirements 18.1, 18.5, 18.6, 28.3, 28.4, 28.6**

- [~] 24. Final checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

## Notes

- Tasks marked with `*` are optional and can be skipped for faster MVP
- Each task references specific requirements for traceability
- Checkpoints ensure incremental validation
- Property tests validate universal correctness properties from the design document
- Unit tests validate specific examples and edge cases
- The MVP focuses on the Android client with bundled offline data; backend sync workers are implemented but will connect to the Live API post-MVP
- All UI screens use Jetpack Compose with locale-aware typography
- All data queries filter by active festival and year context
- Privacy is maintained throughout: no PII in crowd reports, no account creation for core features

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1.1", "1.2"] },
    { "id": 1, "tasks": ["1.3", "1.4"] },
    { "id": 2, "tasks": ["1.5", "2.1"] },
    { "id": 3, "tasks": ["2.2", "2.3"] },
    { "id": 4, "tasks": ["2.4", "3.1", "3.2"] },
    { "id": 5, "tasks": ["3.3", "3.4", "5.1"] },
    { "id": 6, "tasks": ["5.2", "5.3", "6.1", "6.2"] },
    { "id": 7, "tasks": ["6.3", "6.4", "7.1", "7.2", "7.3", "7.4"] },
    { "id": 8, "tasks": ["7.5", "7.6", "7.7"] },
    { "id": 9, "tasks": ["9.1", "9.2", "9.3", "10.1", "10.2", "10.3", "10.4", "10.5", "10.6", "10.7", "10.8", "10.9"] },
    { "id": 10, "tasks": ["9.4", "9.5", "10.10", "10.11", "12.1"] },
    { "id": 11, "tasks": ["12.2", "12.3", "12.4"] },
    { "id": 12, "tasks": ["12.5", "13.1", "13.2"] },
    { "id": 13, "tasks": ["13.3", "13.4", "13.5", "14.1", "14.2", "14.3"] },
    { "id": 14, "tasks": ["16.1", "16.2", "16.3", "17.1", "17.2", "17.3", "17.4", "17.5", "17.6"] },
    { "id": 15, "tasks": ["17.7", "19.1", "19.2", "19.3"] },
    { "id": 16, "tasks": ["19.4", "20.1", "20.2", "20.3"] },
    { "id": 17, "tasks": ["21.1", "21.2"] },
    { "id": 18, "tasks": ["23.1", "23.2", "23.3"] }
  ]
}
```
