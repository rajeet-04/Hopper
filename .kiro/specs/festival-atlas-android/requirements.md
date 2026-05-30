# Requirements Document

## Introduction

Hopper(Festival Atlas) is an offline-first Android application that serves as a cultural navigation and archival tool for Bengal's two major festivals: Durga Puja (Kolkata, UNESCO Intangible Cultural Heritage) and Jagaddhatri Puja (Chandannagar/Krishnanagar, famous for lighting displays and Bishorjon processions). The MVP (v0.1) focuses on safe offline navigation — helping users find pandals, avoid crowds, locate emergency exits, and report crowd conditions, all without requiring network connectivity.

## Glossary

- **Hopper**: The Android application providing offline navigation and cultural archival for Bengal's festivals
- **Pandal**: A temporary decorated structure housing a deity idol during the festival, serving as the primary venue visitors navigate to
- **Map_Engine**: The MapLibre GL Native rendering component responsible for displaying geographic data and pandal locations
- **Crowd_Reporter**: The subsystem that collects, aggregates, and displays anonymous crowd density reports from users
- **Exit_Router**: The subsystem that calculates and displays routes from the user's current location to the nearest emergency service points
- **Offline_Cache**: The Room database and bundled GeoJSON asset layer that stores all pandal, exit node, and calendar data for zero-network operation
- **Exit_Node**: A pre-seeded point of interest representing a Metro station, Railway station, Police booth, or Medical camp
- **Festival_Toggle**: The UI control that switches the entire dataset and map view between Durga Puja and Jagaddhatri Puja contexts
- **Tithi**: A lunar day in the Hindu calendar used to determine festival dates and ritual timings
- **Crowd_Bucket**: One of three crowd density levels (green, yellow, red) representing estimated wait times at a pandal
- **Night_Safety_Mode**: A high-contrast display mode that prioritizes well-lit routes and emergency exits for nighttime navigation
- **Graceful_Degradation**: The fallback UI mode that displays a compass/radar view pointing to nearest pandals and exits when map tiles are unavailable
- **Device_Hash**: A SHA-256 hash of the device identifier used to attribute crowd reports without storing personally identifiable information
- **Edition**: A year-by-year snapshot of a pandal's theme, artists, awards, and photos for archival purposes
- **Itinerary_Builder**: The subsystem that generates optimized pandal-visiting routes based on proximity and crowd conditions
- **Light_Trail**: A curated walking route overlay for Chandannagar's famous lighting installations during Jagaddhatri Puja
- **Bishorjon_Tracker**: The subsystem that tracks and displays live immersion procession routes during Jagaddhatri Puja
- **Bhog_Finder**: The subsystem that displays community-reported food distribution and street food locations near pandal zones
- **Predictive_Crowd**: The subsystem that uses historical crowd data to forecast wait times at pandals
- **Contributor_Portal**: The Next.js web dashboard where puja committees manage their pandal data
- **Source_Type**: A classification field indicating the origin of pandal data (committee, volunteer, news, or unknown), used to assess data trustworthiness
- **Confidence_Level**: A three-tier rating (low, medium, high) indicating the reliability of a data entry based on its source and verification status
- **Open_Data_Export**: The automated system that generates downloadable GeoJSON and CSV datasets of festival data under CC BY 4.0 license at the end of each festival season
- **Lost_Person_Board**: The community bulletin board subsystem that allows users to broadcast their approximate location when separated from their group, with posts auto-expiring after 2 hours
- **Oral_History_Vault**: The subsystem that stores and serves text snippets and audio recordings of cultural narratives from elder committee members, tagged to specific pandals
- **Heritage_Layer**: A map overlay displaying French colonial landmarks and historical buildings in Chandannagar, available during Jagaddhatri Puja
- **Reporter_Reputation**: The scoring system that tracks crowd report accuracy per device hash, awarding badges and increased report weight to consistently accurate reporters
- **Volunteer_Module**: The subsystem that allows puja committees to post volunteer requirements and users to sign up for crowd management shifts
- **Ritual_Guide**: The offline content module providing step-by-step ritual procedures, timing guides, and downloadable devotional audio for both festivals
- **Live_API**: The public REST API serving real-time festival data to third-party developers and researchers during and after festival season

## Requirements

### Requirement 1: Offline Map Rendering

**User Story:** As a pandal hopper, I want to view a map of pandal locations without internet connectivity, so that I can navigate during network congestion typical of festival crowds.

#### Acceptance Criteria

1. THE Map_Engine SHALL render pandal locations from locally cached GeoJSON data without requiring network connectivity
2. WHEN the application launches with no network available, THE Map_Engine SHALL display all cached pandal pins and the user's GPS location within 3 seconds of obtaining location permission
3. THE Offline_Cache SHALL store GeoJSON boundary data for Kolkata, Chandannagar, and Krishnanagar districts in under 50MB of device storage
4. WHEN vector tile loading fails, THE Map_Engine SHALL switch to Graceful_Degradation mode displaying a compass/radar view with nearest pandals and exits as directional indicators
5. WHEN the device regains network connectivity, THE Map_Engine SHALL silently refresh cached tile data in the background using WorkManager without interrupting the user's current view

### Requirement 2: Dual-Festival Toggle

**User Story:** As a pandal hopper, I want to switch between Durga Puja and Jagaddhatri Puja datasets, so that I can use the same app for both festivals in their respective seasons.

#### Acceptance Criteria

1. THE Festival_Toggle SHALL present two selectable festival contexts: Durga Puja and Jagaddhatri Puja
2. WHEN the user activates the Festival_Toggle, THE Hopper SHALL reload all map pins, pandal listings, and calendar data to reflect the selected festival within 500 milliseconds
3. THE Hopper SHALL default the Festival_Toggle to the festival whose dates are nearest to the current calendar date
4. WHILE Durga Puja is selected, THE Map_Engine SHALL display pandal pins for Kolkata-region pandals only
5. WHILE Jagaddhatri Puja is selected, THE Map_Engine SHALL display pandal pins for Chandannagar and Krishnanagar-region pandals only
6. THE Hopper SHALL enforce a global Year context alongside the Festival context; all queries to the Offline_Cache SHALL explicitly filter by both festival and year, defaulting to the current calendar year when no year is explicitly selected by the user

### Requirement 3: Puja Near Me

**User Story:** As a pandal hopper, I want to see the nearest pandals ranked by distance, crowd level, and significance, so that I can decide what to visit next.

#### Acceptance Criteria

1. WHEN the user opens the "Puja Near Me" view, THE Hopper SHALL display the nearest pandals sorted by a composite score combining distance, current crowd level, and significance ranking
2. THE Hopper SHALL display each pandal entry with: name, distance in meters, current Crowd_Bucket indicator, and a brief theme description
3. THE Hopper SHALL compute nearest-pandal results using locally cached GPS coordinates without requiring network connectivity
4. WHEN the user's location changes by more than 100 meters, THE Hopper SHALL recalculate and update the nearest pandal list automatically
5. THE Hopper SHALL display a minimum of 5 nearest pandals in the list view

### Requirement 4: Emergency Exit Routing

**User Story:** As a safety-conscious family group member, I want to find the fastest route to the nearest Metro, Railway, Police, or Medical facility with one tap, so that I can exit a dangerous crowd situation quickly.

#### Acceptance Criteria

1. THE Hopper SHALL display a persistent "Get Me Out" button on the main map screen that is accessible with a single tap
2. WHEN the user taps "Get Me Out", THE Exit_Router SHALL display the nearest Exit_Node for each category: Metro, Railway, Police, and Medical
3. THE Exit_Router SHALL calculate routes to Exit_Nodes using precomputed offline walking connectors stored in the Offline_Cache
4. THE Exit_Router SHALL display walking distance and estimated walking time for each Exit_Node option
5. WHEN no network is available, THE Exit_Router SHALL provide exit routing using only locally cached connector polylines and Exit_Node coordinates
6. THE Exit_Router SHALL store at least 2 alternate route connectors per pandal-to-exit-node pair to handle blocked routes

### Requirement 5: Crowd Reporting

**User Story:** As a pandal hopper, I want to report crowd density at my current pandal in under 3 taps, so that other users can make informed navigation decisions.

#### Acceptance Criteria

1. WHEN the user initiates a crowd report, THE Crowd_Reporter SHALL present three selectable Crowd_Buckets: green (under 10 minutes wait), yellow (approximately 25 minutes wait), and red (60 minutes or more wait)
2. THE Crowd_Reporter SHALL allow a complete crowd report submission in 3 or fewer taps from the main map screen
3. THE Crowd_Reporter SHALL associate each report with a Device_Hash and not collect any personally identifiable information
4. WHEN a crowd report is submitted without network connectivity, THE Crowd_Reporter SHALL queue the report locally and upload it when connectivity is restored via WorkManager
5. THE Crowd_Reporter SHALL expire crowd reports after 20 minutes, removing stale data from the displayed aggregation
6. WHEN multiple reports exist for a pandal within the expiry window, THE Crowd_Reporter SHALL display a weighted median of reported wait times
7. THE Crowd_Reporter SHALL enforce a rate limit of one report per pandal per 10 minutes per device to prevent spam

### Requirement 6: Festival Calendar with Tithi Tracking

**User Story:** As a devotee, I want to see the complete festival calendar with accurate tithi dates, so that I can plan my pandal visits around key ritual days.

#### Acceptance Criteria

1. THE Hopper SHALL display the complete festival calendar for both Durga Puja and Jagaddhatri Puja with all major tithi dates for the current year
2. THE Hopper SHALL load calendar and tithi data from a bundled offline JSON asset, requiring no network connectivity
3. THE Hopper SHALL highlight the current tithi and indicate peak crowd days (Ashtami, Navami) with visual emphasis
4. WHEN the user views the calendar, THE Hopper SHALL display each tithi with its name, date, and cultural significance description in both Bengali and English

### Requirement 7: Night Safety Mode

**User Story:** As a pandal hopper navigating at night, I want a high-contrast display mode that shows well-lit routes, so that I can travel safely through unfamiliar areas after dark.

#### Acceptance Criteria

1. WHEN the user activates Night_Safety_Mode, THE Map_Engine SHALL switch to a high-contrast dark theme with increased text size and minimum 48dp tap targets
2. WHILE Night_Safety_Mode is active, THE Exit_Router SHALL prefer routes along well-lit main roads over shorter routes through unlit alleys
3. WHILE Night_Safety_Mode is active, THE Map_Engine SHALL prominently display Police booth and Medical camp Exit_Nodes with increased visual weight
4. THE Hopper SHALL offer automatic activation of Night_Safety_Mode based on local sunset time

### Requirement 8: Offline Data Persistence and Sync

**User Story:** As a user in a network-congested festival area, I want all essential data pre-cached on my device, so that the app remains fully functional regardless of connectivity.

#### Acceptance Criteria

1. THE Offline_Cache SHALL store the complete pandal dataset (minimum 150 pandals with GPS coordinates, names, themes, and committee information) in the Room database
2. THE Offline_Cache SHALL store all Exit_Node data (Metro, Railway, Police, Medical locations with GPS coordinates) for offline spatial queries
3. THE Offline_Cache SHALL store precomputed walking connector polylines for emergency exit routing
4. WHEN network connectivity is available, THE Hopper SHALL perform background data synchronization using WorkManager without impacting foreground performance
5. THE Hopper SHALL implement a stale-while-revalidate sync strategy: serve cached data immediately and update silently when network permits
6. THE Offline_Cache SHALL store the last-known Crowd_Bucket for each pandal so that crowd indicators remain visible during offline periods

### Requirement 9: Application Performance

**User Story:** As a user on a mid-range Android device, I want the app to start quickly and run efficiently during long pandal-hopping sessions, so that it remains usable throughout the festival evening.

#### Acceptance Criteria

1. THE Hopper SHALL achieve cold start to a usable map screen in under 2.5 seconds on a device with 2GB RAM running Android 8.0
2. THE Hopper SHALL maintain battery consumption below 15% over a 6-hour active usage session with GPS enabled
3. THE Hopper SHALL cease GPS polling when the device is stationary for more than 2 minutes and resume when motion is detected
4. THE Hopper SHALL support devices running Android 8.0 (API level 26) and above
5. THE Hopper SHALL function correctly on devices with 2GB RAM by lazy-loading non-critical data and optimizing Compose rendering

### Requirement 10: Bilingual Language Support

**User Story:** As a Bengali-speaking user, I want to use the app in Bengali, so that I can navigate pandal information in my native language.

#### Acceptance Criteria

1. THE Hopper SHALL provide all UI labels, navigation elements, and system messages in both Bengali and English
2. THE Hopper SHALL display pandal names, committee names, and theme descriptions in both Bengali and English where data is available
3. WHEN the user selects Bengali as the display language, THE Hopper SHALL render all text using a Bengali-compatible font (Hind Siliguri or equivalent)
4. THE Hopper SHALL default to the device's system language setting if it matches a supported language

### Requirement 11: Privacy-Preserving Crowd Reports

**User Story:** As a privacy-conscious user, I want to contribute crowd reports without creating an account or sharing personal information, so that I can help the community while maintaining anonymity.

#### Acceptance Criteria

1. THE Crowd_Reporter SHALL identify reporters using only a Device_Hash (SHA-256 of device identifier) and not require user registration or login
2. THE Crowd_Reporter SHALL not transmit, store, or log any personally identifiable information including name, email, phone number, or precise device identifiers
3. THE Hopper SHALL not require any user account creation for core navigation and crowd reporting functionality
4. IF a crowd report contains metadata that could identify a user, THEN THE Crowd_Reporter SHALL strip that metadata before transmission to the backend

### Requirement 12: Graceful Degradation

**User Story:** As a user in an area with failed map tile loading, I want a fallback navigation view, so that I can still find nearby pandals and exits even without rendered maps.

#### Acceptance Criteria

1. WHEN map tile rendering fails completely, THE Map_Engine SHALL display a compass/radar view showing the user's heading and directional indicators to the nearest pandals and Exit_Nodes
2. THE Graceful_Degradation view SHALL display pandal names, distances, and Crowd_Bucket indicators in a list format alongside the compass view
3. THE Graceful_Degradation view SHALL operate using only GPS coordinates cached in the Offline_Cache and the device's compass sensor
4. WHEN map tiles become available again, THE Map_Engine SHALL automatically transition back to the full map view without user intervention

### Requirement 13: Pandal Detail Cards

**User Story:** As a pandal hopper, I want to see structured information about a pandal when I tap its pin, so that I can learn about its theme, artisans, history, and community contributions before visiting.

#### Acceptance Criteria

1. WHEN the user taps a pandal pin on the map, THE Hopper SHALL display a bottom-sheet card containing the pandal's current year theme, committee/club name, established year, artisan credits, community photos, awards, and archive timeline stub
2. THE Hopper SHALL display artisan credits with separate fields for idol maker, lighting designer, and theme designer where data is available
3. THE Hopper SHALL display up to 10 community-submitted photos in a horizontally scrollable gallery within the pandal detail card
4. WHEN award data exists for the pandal, THE Hopper SHALL display award names and years in a dedicated awards section of the detail card
5. THE Hopper SHALL display an archive timeline stub showing the pandal's Edition history as a vertically scrollable list of past years with theme names
6. THE Hopper SHALL render all pandal detail card content from locally cached data in the Offline_Cache without requiring network connectivity
7. WHEN the user taps the archive timeline stub, THE Hopper SHALL expand the timeline to show full Edition entries including theme, artisan credits, photos, and awards for each past year

### Requirement 14: Itinerary Builder

**User Story:** As a pandal hopper planning my evening route, I want to select pandals and get an optimized walking order, so that I can visit multiple pandals efficiently while avoiding heavy crowds.

#### Acceptance Criteria

1. WHEN the user selects between 5 and 10 pandals, THE Itinerary_Builder SHALL generate an ordered walking route using nearest-neighbor proximity chaining with crowd penalty weighting
2. THE Itinerary_Builder SHALL apply a crowd penalty that deprioritizes pandals with a red Crowd_Bucket, placing them later in the route when crowds are expected to thin
3. THE Itinerary_Builder SHALL display the total estimated walking distance in kilometers and total estimated walking time in minutes for the generated itinerary
4. THE Itinerary_Builder SHALL save the generated itinerary to the Offline_Cache for access without network connectivity
5. THE Itinerary_Builder SHALL display each stop in the itinerary with: sequence number, pandal name, walking distance from previous stop, estimated arrival time, and current Crowd_Bucket indicator
6. WHEN the user's location changes during itinerary execution, THE Itinerary_Builder SHALL update estimated arrival times for remaining stops based on the current position
7. THE Itinerary_Builder SHALL compute routes using locally cached pandal GPS coordinates and crowd data without requiring network connectivity

### Requirement 15: Chandannagar Light Trail

**User Story:** As a visitor to Chandannagar during Jagaddhatri Puja, I want a curated walking route of the famous lighting installations, so that I can experience the best light displays in an optimal order.

#### Acceptance Criteria

1. WHILE Jagaddhatri Puja is selected in the Festival_Toggle, THE Hopper SHALL offer a "Chandannagar Light Trail" map overlay showing curated lighting installations as a sequential walking route
2. THE Light_Trail SHALL display each lighting installation with: artist name, physical dimensions, and theme or story description
3. THE Light_Trail SHALL present installations in a recommended walking order from a defined start point to a defined end point
4. THE Light_Trail SHALL mark best vantage points for viewing each lighting installation as distinct map pins with viewing-angle indicators
5. THE Light_Trail SHALL render the complete route and installation data from locally cached data in the Offline_Cache without requiring network connectivity
6. WHEN the user taps a lighting installation pin, THE Hopper SHALL display a detail card with the artist name, dimensions, theme description, and a photo where available
7. THE Light_Trail SHALL display the total walking distance and estimated walking time for the complete trail route

### Requirement 16: Bishorjon Procession Tracker

**User Story:** As a spectator during Jagaddhatri Puja's Bishorjon night, I want to track live procession routes, so that I can position myself to watch the light floats pass by or avoid congested procession corridors.

#### Acceptance Criteria

1. WHILE Jagaddhatri Puja is selected in the Festival_Toggle and the current date falls on Bishorjon night, THE Bishorjon_Tracker SHALL display active procession routes on the map as crowd-reported polylines
2. WHEN crowd reports indicate a procession is active on a route segment, THE Bishorjon_Tracker SHALL highlight that segment with a distinct animated visual indicator showing direction of movement
3. THE Bishorjon_Tracker SHALL calculate and display the estimated time of arrival of the nearest active procession to the user's current location
4. WHEN an active procession is within 500 meters of the user's location, THE Bishorjon_Tracker SHALL trigger an audio alert and device vibration notification
5. THE Bishorjon_Tracker SHALL allow users to report procession sightings with a pandal name and current location in 3 or fewer taps
6. THE Bishorjon_Tracker SHALL expire procession position reports after 15 minutes to prevent display of stale route data
7. WHEN no network is available, THE Bishorjon_Tracker SHALL display the last-known procession positions from locally cached reports and indicate the staleness of the data with a timestamp

### Requirement 17: Bhog and Food Finder

**User Story:** As a pandal hopper, I want to find bhog distribution points and street food stalls near pandal zones, so that I can eat during my pandal-hopping route without searching blindly.

#### Acceptance Criteria

1. THE Bhog_Finder SHALL display a map overlay with two filterable categories: "Bhog Distribution" pins and "Street Food" pins within pandal zones
2. WHEN a user reports a bhog distribution point, THE Bhog_Finder SHALL create a time-limited pin that expires after the reported distribution end time or after 2 hours, whichever is shorter
3. THE Bhog_Finder SHALL display each bhog distribution pin with: committee name, location, reported start time, and expected end time
4. THE Bhog_Finder SHALL display each street food pin with: stall name or description, location, and community rating on a 5-point scale
5. WHEN the user submits a bhog or food report, THE Bhog_Finder SHALL allow submission in 3 or fewer taps from the map screen
6. WHEN no network is available, THE Bhog_Finder SHALL queue food reports locally and upload them when connectivity is restored via WorkManager
7. THE Bhog_Finder SHALL display the distance from the user's current location to each food pin in meters

### Requirement 18: Predictive Wait Times

**User Story:** As a pandal hopper planning visits on peak nights, I want to see predicted crowd levels based on historical patterns, so that I can time my visits to avoid the worst congestion.

#### Acceptance Criteria

1. THE Predictive_Crowd SHALL display a heat timeline bar on each pandal detail card showing predicted crowd levels across hourly intervals for the current day
2. THE Predictive_Crowd SHALL generate predictions using historical crowd report data from previous years combined with known peak-day patterns (Ashtami and Navami nights between 9PM and 12AM are historically peak)
3. THE Predictive_Crowd SHALL classify predicted crowd levels into three tiers: Low (green), Moderate (yellow), and High (red) matching the existing Crowd_Bucket system
4. THE Predictive_Crowd SHALL display a textual summary on the heat timeline bar (example: "Historically VERY CROWDED on Ashtami night between 9PM–12AM")
5. THE Predictive_Crowd SHALL load all historical data and prediction heuristics from the Offline_Cache without requiring network connectivity
6. WHEN both live crowd reports and predictive data are available for a pandal, THE Hopper SHALL display live data with higher visual priority and show predictive data as a secondary reference

### Requirement 19: Contributor Portal

**User Story:** As a puja committee member, I want a web dashboard to manage my pandal's data, so that visitors see accurate and up-to-date information about our pandal without relying on third-party data entry.

#### Acceptance Criteria

1. THE Contributor_Portal SHALL provide a web-based form for puja committees to enter and update: pandal name, address, GPS coordinates, current year theme, inauguration date and time, daily opening hours, idol maker credit, lighting artist credit, awards, emergency contact number, volunteer contact, and historical Edition entries
2. THE Contributor_Portal SHALL allow committees to upload up to 20 photos per year with captions for their pandal
3. THE Contributor_Portal SHALL authenticate committee members via Google OAuth or one-time password (OTP) sent to a registered mobile number
4. WHEN a committee submits updated pandal data, THE Contributor_Portal SHALL hold the submission for lightweight moderation review before publishing changes to the live dataset
5. THE Contributor_Portal SHALL provide a mobile-responsive layout that allows committee members to manage data from a smartphone browser
6. THE Contributor_Portal SHALL store GPS coordinates via an interactive map pin-drop interface allowing committees to place their pandal location precisely
7. WHEN moderation approves a submission, THE Contributor_Portal SHALL publish the updated data to the backend within 5 minutes, making it available for synchronization to the Android application's Offline_Cache

### Requirement 20: Data Provenance and Credibility

**User Story:** As a heritage researcher or user, I want to know where the pandal information came from, so that I can trust the historical archive.

#### Acceptance Criteria

1. THE Offline_Cache and the Backend SHALL enforce a Source_Type field (committee, volunteer, news, or unknown) on every historical Edition data entry
2. THE Offline_Cache and the Backend SHALL enforce a Confidence_Level field (low, medium, or high) on every historical Edition data entry
3. WHEN a pandal detail card displays Edition data, THE Hopper SHALL show a visual provenance indicator distinguishing "Committee Verified" entries from "Community Sourced" entries
4. WHEN Edition data is ingested without explicit source attribution, THE Hopper SHALL default the Source_Type to unknown and the Confidence_Level to low
5. THE Contributor_Portal SHALL require committee members to select a Source_Type when submitting or editing historical Edition entries
6. THE Hopper SHALL display the Confidence_Level as a visual badge (low, medium, high) on each Edition entry in the archive timeline

### Requirement 21: Open Data Export

**User Story:** As an open-data advocate or student, I want to download the festival dataset, so that I can use it for research without being locked into the Hopper app.

#### Acceptance Criteria

1. THE Open_Data_Export SHALL automatically generate bundled GeoJSON and CSV exports of all public pandal coordinates, themes, artisan credits, awards, committee information, and archival data at the end of each festival season
2. THE Open_Data_Export SHALL publish all exported data under the Creative Commons Attribution 4.0 International (CC BY 4.0) license
3. THE Open_Data_Export SHALL version each export file by festival and year using the naming convention {festival}_{year}_pandals.{format} (example: durga_puja_2026_pandals.geojson)
4. THE Backend SHALL serve Open_Data_Export files via a public download endpoint accessible without authentication
5. THE Open_Data_Export SHALL include pandal locations, themes, artisan credits, awards, and committee information in every generated export
6. WHEN a festival season concludes, THE Open_Data_Export SHALL generate and publish the new dataset within 7 days of the final immersion date

### Requirement 22: Lost Person Bulletin Board

**User Story:** As a family member separated from my group in a dense festival crowd, I want to broadcast my approximate location on a community board, so that nearby people or my group can find and assist me.

#### Acceptance Criteria

1. THE Lost_Person_Board SHALL allow users to post a lost-person bulletin containing a user-chosen display name and an auto-detected GPS location without requiring account creation or login
2. WHEN a user submits a lost-person post, THE Lost_Person_Board SHALL auto-populate the location field using the device's current GPS coordinates
3. THE Lost_Person_Board SHALL display all active lost-person posts to users within a 2-kilometer radius of each post's GPS coordinates
4. THE Lost_Person_Board SHALL automatically expire and remove lost-person posts after 2 hours from the time of submission
5. THE Lost_Person_Board SHALL not store any personally identifiable information beyond the user-chosen display name associated with each post
6. WHEN a lost-person post is submitted without network connectivity, THE Lost_Person_Board SHALL queue the post locally and upload it when connectivity is restored via WorkManager
7. THE Lost_Person_Board SHALL allow the original poster to manually mark their post as resolved before the 2-hour expiry

### Requirement 23: Oral History Vault

**User Story:** As a cultural preservation enthusiast, I want to access text narratives and audio recordings from elder committee members about pandal history and neighborhood lore, so that intangible cultural heritage is preserved digitally.

#### Acceptance Criteria

1. THE Oral_History_Vault SHALL store and serve text snippets from elder committee members containing narratives about pandal history and neighborhood lore, each tagged to a specific pandal and optionally a year
2. THE Oral_History_Vault SHALL store audio recordings in MP3 format on object storage and link them by reference URL to their corresponding text entries in the application
3. THE Oral_History_Vault SHALL accept community-submitted neighborhood lore entries tagged to specific pandal GPS coordinates
4. WHEN a community member submits an oral history entry, THE Contributor_Portal SHALL hold the submission for moderation review before publishing the content to the live dataset
5. WHEN an oral history entry has been downloaded by the user, THE Oral_History_Vault SHALL make that entry available offline from the Offline_Cache without requiring network connectivity
6. THE Oral_History_Vault SHALL associate each entry with a specific pandal and optionally a historical year reference
7. THE Oral_History_Vault SHALL display each entry with: title, contributor name, associated pandal name, year reference, text content, and an audio playback control where an audio recording exists

### Requirement 24: Chandannagar Heritage Layer

**User Story:** As a visitor to Chandannagar during Jagaddhatri Puja, I want to see historical buildings and French colonial landmarks on the map, so that I can explore the city's heritage alongside the festival.

#### Acceptance Criteria

1. WHILE Jagaddhatri Puja is selected in the Festival_Toggle, THE Heritage_Layer SHALL display a map overlay showing historical buildings and French colonial landmarks in Chandannagar as distinct map pins
2. WHEN the user taps a heritage point pin, THE Hopper SHALL display a brief historical description of the landmark
3. THE Heritage_Layer SHALL store each heritage point with: name, name in Bengali, GPS coordinates, brief description, historical period, and an optional photo
4. THE Heritage_Layer SHALL store all heritage point data in the Offline_Cache for zero-network operation
5. THE Heritage_Layer SHALL be available only when Jagaddhatri Puja is selected in the Festival_Toggle
6. THE Heritage_Layer SHALL visually distinguish heritage point pins from pandal pins and Exit_Node pins using a unique icon and color scheme

### Requirement 25: Crowd Reporter Reputation and Gamification

**User Story:** As a frequent crowd reporter, I want to earn recognition for accurate reports, so that I am motivated to continue contributing reliable crowd data to the community.

#### Acceptance Criteria

1. THE Reporter_Reputation SHALL award a "Crowd Reporter" badge to devices that submit 10 or more crowd reports during a single festival season
2. THE Reporter_Reputation SHALL calculate an accuracy score for each device by comparing each submitted report against the next 3 reports submitted by other devices for the same pandal within the report expiry window
3. THE Reporter_Reputation SHALL display a community leaderboard during festival week showing top reporters by accuracy score, identified only by their badge tier and not by any personal identity
4. WHEN a reporter's accuracy score exceeds a defined threshold, THE Crowd_Reporter SHALL apply increased weight to that reporter's crowd reports in the weighted median calculation
5. THE Reporter_Reputation SHALL not award real prizes and SHALL function purely as community recognition
6. THE Reporter_Reputation SHALL store reputation data locally per Device_Hash and synchronize to the backend when network connectivity is available

### Requirement 26: Volunteer Coordination Module

**User Story:** As a puja committee organizer, I want to post volunteer requirements and receive sign-ups through the app, so that I can coordinate crowd management shifts without relying on informal communication channels.

#### Acceptance Criteria

1. THE Volunteer_Module SHALL allow puja committees to post volunteer requirements via the Contributor_Portal specifying: role description, location, date, time slot, and number of volunteers needed
2. THE Volunteer_Module SHALL allow users to sign up for volunteer shifts via the Android app by providing a name and phone number without requiring account creation
3. WHEN a user signs up for a volunteer shift, THE Volunteer_Module SHALL add the volunteer's name and phone number to a list accessible only to the posting committee
4. THE Volunteer_Module SHALL automatically expire volunteer posts after the specified shift time passes
5. THE Volunteer_Module SHALL not display volunteer contact information publicly and SHALL restrict access to the posting committee only
6. THE Volunteer_Module SHALL display volunteer posts only for the relevant festival and year as determined by the active Festival_Toggle and Year context
7. WHEN a volunteer post reaches its required number of sign-ups, THE Volunteer_Module SHALL mark the post as filled and stop accepting new sign-ups

### Requirement 27: Ritual Guide and Audio Library

**User Story:** As a devotee, I want step-by-step ritual guides and downloadable devotional audio, so that I can participate in rituals correctly and listen to mantras offline during the festival.

#### Acceptance Criteria

1. THE Ritual_Guide SHALL provide step-by-step guides for: Anjali (offering) timings and procedure, Sandhi Puja (the exact 48-minute window between Ashtami and Navami), Dhunuchi Naach, Sindoor Khela, and Bishorjon viewing etiquette
2. THE Ritual_Guide SHALL provide offline downloadable audio content including: Durga Saptashati excerpts (recitation guides), Jagaddhatri Dhyan Mantra, and Mahalaya audio notification
3. THE Ritual_Guide SHALL make all ritual text content and downloaded audio files available offline without requiring network connectivity
4. THE Ritual_Guide SHALL provide all ritual content in both Bengali and English
5. THE Ritual_Guide SHALL store audio files on object storage and cache them locally on the device after first download
6. THE Ritual_Guide SHALL link each ritual guide entry to the relevant tithi in the festival calendar so that users can access the appropriate guide from the calendar view
7. WHEN the user downloads an audio file, THE Ritual_Guide SHALL store the file in the Offline_Cache and indicate download status with a visual indicator

### Requirement 28: Live Public REST API

**User Story:** As a third-party developer or news outlet, I want to query a live public API for real-time pandal data, crowd levels, and calendar information during the festival, so that I can build integrations and provide coverage without scraping the app.

#### Acceptance Criteria

1. THE Live_API SHALL provide a public REST API at data.festivalatlas.org serving live access to pandal data, crowd levels, calendar information, and artisan information
2. THE Live_API SHALL expose the following endpoints: GET /api/v1/pandals, GET /api/v1/pandals/{id}/history, GET /api/v1/artists, GET /api/v1/crowd with pandal_id query parameter, and GET /api/v1/calendar
3. THE Live_API SHALL enforce rate limits of 100 requests per day for anonymous access and 10,000 requests per day for registered researchers
4. THE Live_API SHALL return responses in clean JSON format
5. THE Live_API SHALL provide API documentation via a Swagger/OpenAPI specification
6. THE Live_API SHALL not require authentication for read-only public data access and SHALL enforce rate limits by IP address
7. THE Live_API SHALL serve data during festival season for live information and year-round for historical data access
