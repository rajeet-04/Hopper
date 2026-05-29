# **🗺️ Festival Atlas: The Master Roadmap**

This roadmap outlines the journey from a blank repository to a living, open-source cultural archive for Bengal's festivals (Durga Puja & Jagaddhatri Puja). It prioritizes **offline resilience**, **safety**, and **open data**, ensuring we build the "Wikipedia \+ OpenStreetMap" of Bengal's heritage.

## **🏗️ Phase 0: Foundation & "No-Regrets" Architecture (Weeks 1-2)**

*The goal of this phase is to establish the open data schemas, backend infrastructure, and seed data before writing mobile UI code.*

* **Infrastructure Setup:**  
  * Register domains: festivalatlas.org & data.festivalatlas.org.  
  * Set up Supabase project with PostGIS enabled for spatial queries.  
  * Configure Cloudflare R2 bucket for CC-BY licensed photo storage.  
* **Data Modeling & Open Schema (v1):**  
  * Define the dual-festival JSON/GeoJSON schema enforcing festival (Durga/Jagaddhatri) and year fields.  
  * Implement "Source \+ Confidence" fields (source\_type, confidence\_level) for all archival data to protect credibility.  
  * Create the 2026\_tithis.json offline asset for accurate offline festival timelines.  
* **Seed Data Collection:**  
  * Manually collect GPS coordinates for the first 50 major pandals across Kolkata (Durga Puja) and Chandannagar/Krishnanagar (Jagaddhatri Puja).  
  * Establish contact with at least 3 puja committees for early data partnerships.

## **📱 Phase 1: The Core MVP (v0.1) \- "Safe Offline Navigation" (Weeks 3-6)**

*Building the native Android app focused strictly on solving the immediate pain point: navigating through and escaping massive crowds without the internet.*

* **Offline-First Map Engine:**  
  * Integrate MapLibre GL in the Android (Kotlin/Jetpack Compose) app.  
  * Implement local caching of GeoJSON boundaries for Kolkata and Hooghly districts.  
  * Build "Graceful Degradation": If vector tiles fail entirely, the map switches to a vector-less compass/radar view pointing to the nearest pandal/exit.  
* **The Dual-Festival Toggle:**  
  * Implement the core UI toggle to switch the entire app's dataset and theme between Durga Puja (Oct) and Jagaddhatri Puja (Nov).  
* **"Get Me Out" SOS Feature:**  
  * Build the emergency exit routing to the nearest Metro, Railway Station, Police Booth, or Medical Camp using offline spatial data.  
* **Night Safety Mode:**  
  * Implement high-contrast UI and prioritized routing through well-lit main arteries rather than dark alleys.

## **👥 Phase 2: Community & Crowd Intelligence (v0.5) (Weeks 7-10)**

*Activating the community features and launching the contributor tools.*

* **Web Contributor Dashboard (Next.js):**  
  * Launch the web portal for local historians, volunteers, and committees to add pandals, edit themes, and correct coordinates.  
* **Privacy-Preserving Crowd Intelligence:**  
  * Implement the 3-tap wait-time reporting UI in the Android app.  
  * Use anonymous device hashing to prevent spam (no user accounts required).  
* **Hybrid Routing Engine:**  
  * Combine predefined historical patterns (70%) with live community reports (20%) and location density (10%) to route users away from bottlenecks.

## **🏛️ Phase 3: The Cultural Archive (v1.0) (Weeks 11-14)**

*Transitioning from a navigation utility to a permanent digital heritage museum.*

* **Rich Pandal Profiles:**  
  * Display year-over-year themes, artist credits, and committee histories.  
  * Add a "Data Quality Meter" to pandal pages (e.g., "Missing 2026 Theme") to nudge community contributions.  
* **Oral Histories & Photography:**  
  * Open web forms for locals to submit oral histories and folklore about historic Bonedi Bari traditions.  
  * In-app photo uploads for the community archive.  
* **Chandannagar Light Trail (Jagaddhatri Special):**  
  * Specific mapping layers highlighting the world-famous dynamic lighting routes and installations in Chandannagar.  
* **Bishorjon (Immersion) Tracker:**  
  * Mapping the procession routes and immersion ghats, specifically vital for the massive Jagaddhatri Puja processions.

## **🌍 Phase 4: Legacy & Open Data Export (Post-Festival)**

*Ensuring the data survives forever, regardless of the app's future.*

* **Annual Data Dumps:**  
  * Automate the export of the year's collected coordinates, themes, and histories into downloadable GeoJSON and CSV formats.  
* **Licensing & Distribution:**  
  * Publish the datasets under CC BY 4.0 on data.festivalatlas.org.  
* **Academic & Media Outreach:**  
  * Share the structured datasets with journalists, urban planners, and heritage researchers for analysis.