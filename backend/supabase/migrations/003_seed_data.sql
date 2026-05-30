-- ============================================================
-- Seed Data: Durga Puja 2026 Pandals (Kolkata)
-- ============================================================

INSERT INTO pandals (id, name, name_bengali, latitude, longitude, city, neighborhood, festival, year, theme, committee_name, established_year, artisan_credits_json, awards, significance_rank, source_type, confidence_level) VALUES
('dp2026_bagbazar', 'Bagbazar Sarbojanin', 'বাগবাজার সার্বজনীন', 22.5958, 88.3629, 'Kolkata', 'Bagbazar', 'DURGA_PUJA', 2026, 'Terracotta Temple Architecture', 'Bagbazar Sarbojanin Durgotsab Committee', 1919, '[{"name":"Sanatan Dinda","role":"idol_maker"}]', ARRAY['Best Pandal 2024'], 1, 'COMMITTEE', 'HIGH'),
('dp2026_kumartuli', 'Kumartuli Park Sarbojanin', 'কুমারটুলি পার্ক সার্বজনীন', 22.5982, 88.3587, 'Kolkata', 'Kumartuli', 'DURGA_PUJA', 2026, 'Underwater Coral Kingdom', 'Kumartuli Park Sarbojanin Durgotsab', 1995, '[{"name":"Rintu Das","role":"pandal_artist"}]', ARRAY['Best Idol 2023'], 2, 'COMMITTEE', 'HIGH'),
('dp2026_college_square', 'College Square', 'কলেজ স্কোয়ার', 22.5762, 88.3639, 'Kolkata', 'College Street', 'DURGA_PUJA', 2026, 'Digital Renaissance', 'College Square Durgotsab Committee', 1948, '[]', ARRAY[]::TEXT[], 3, 'COMMITTEE', 'HIGH'),
('dp2026_suruchi', 'Suruchi Sangha', 'সুরুচি সংঘ', 22.5125, 88.3530, 'Kolkata', 'New Alipore', 'DURGA_PUJA', 2026, 'Tribal Art of Jharkhand', 'Suruchi Sangha', 1969, '[{"name":"Bhabatosh Sutar","role":"pandal_artist"}]', ARRAY['Best Theme 2024', 'UNESCO Recognition'], 4, 'COMMITTEE', 'HIGH'),
('dp2026_mohammad_ali_park', 'Mohammad Ali Park', 'মহম্মদ আলি পার্ক', 22.5715, 88.3595, 'Kolkata', 'Central Kolkata', 'DURGA_PUJA', 2026, 'Mughal Architecture Revival', 'Mohammad Ali Park Durgotsab Committee', 1969, '[]', ARRAY['Best Lighting 2023'], 5, 'COMMITTEE', 'HIGH'),
('dp2026_deshapriya', 'Deshapriya Park', 'দেশপ্রিয় পার্ক', 22.5175, 88.3505, 'Kolkata', 'Southern Avenue', 'DURGA_PUJA', 2026, 'Climate Change Awareness', 'Deshapriya Park Sarbojanin', 1939, '[]', ARRAY['Best Innovation 2024'], 6, 'COMMITTEE', 'HIGH'),
('dp2026_ekdalia', 'Ekdalia Evergreen', 'একডালিয়া এভারগ্রিন', 22.5195, 88.3565, 'Kolkata', 'Gariahat', 'DURGA_PUJA', 2026, 'Origami Universe', 'Ekdalia Evergreen Club', 1943, '[]', ARRAY['Best Artistic 2023'], 7, 'COMMITTEE', 'HIGH'),
('dp2026_santosh_mitra', 'Santosh Mitra Square', 'সন্তোষ মিত্র স্কোয়ার', 22.5680, 88.3620, 'Kolkata', 'Sealdah', 'DURGA_PUJA', 2026, 'Chandannagar Light Art', 'Santosh Mitra Square Durgotsab', 1935, '[]', ARRAY['Best Lighting 2024'], 8, 'COMMITTEE', 'HIGH'),
('dp2026_sreebhumi', 'Sreebhumi Sporting Club', 'শ্রীভূমি স্পোর্টিং ক্লাব', 22.6125, 88.3890, 'Kolkata', 'Lake Town', 'DURGA_PUJA', 2026, 'Burj Khalifa Replica', 'Sreebhumi Sporting Club', 1972, '[]', ARRAY['Most Popular 2024'], 9, 'COMMITTEE', 'HIGH'),
('dp2026_chetla', 'Chetla Agrani', 'চেতলা অগ্রণী', 22.5095, 88.3420, 'Kolkata', 'Chetla', 'DURGA_PUJA', 2026, 'Bankura Terracotta', 'Chetla Agrani Club', 1965, '[]', ARRAY['Best Cultural 2023'], 10, 'COMMITTEE', 'HIGH');

-- ============================================================
-- Seed Data: Jagaddhatri Puja 2026 (Chandannagar)
-- ============================================================

INSERT INTO pandals (id, name, name_bengali, latitude, longitude, city, neighborhood, festival, year, theme, committee_name, established_year, significance_rank, source_type, confidence_level) VALUES
('jp2026_barabazar', 'Barabazar Sarbojanin', 'বড়বাজার সার্বজনীন', 22.8672, 88.3678, 'Chandannagar', 'Barabazar', 'JAGADDHATRI_PUJA', 2026, 'French Colonial Heritage', 'Barabazar Sarbojanin Committee', 1910, 1, 'COMMITTEE', 'HIGH'),
('jp2026_gondalpara', 'Gondalpara Sarbojanin', 'গোন্দলপাড়া সার্বজনীন', 22.8645, 88.3710, 'Chandannagar', 'Gondalpara', 'JAGADDHATRI_PUJA', 2026, 'Chandannagar Lighting Legacy', 'Gondalpara Sarbojanin Committee', 1925, 2, 'COMMITTEE', 'HIGH'),
('jp2026_laxmiganj', 'Laxmiganj Sarbojanin', 'লক্ষ্মীগঞ্জ সার্বজনীন', 22.8690, 88.3650, 'Chandannagar', 'Laxmiganj', 'JAGADDHATRI_PUJA', 2026, 'Terracotta Temples of Bengal', 'Laxmiganj Sarbojanin Committee', 1935, 3, 'COMMITTEE', 'HIGH'),
('jp2026_boro_rashbari', 'Boro Rashbari', 'বড় রাসবাড়ি', 22.8660, 88.3695, 'Chandannagar', 'Boro Rashbari', 'JAGADDHATRI_PUJA', 2026, 'Mythological Epics', 'Boro Rashbari Committee', 1920, 4, 'COMMITTEE', 'HIGH'),
('jp2026_khalisani', 'Khalisani Sarbojanin', 'খলিসানি সার্বজনীন', 22.8710, 88.3720, 'Chandannagar', 'Khalisani', 'JAGADDHATRI_PUJA', 2026, 'River Ganges Heritage', 'Khalisani Sarbojanin Committee', 1940, 5, 'COMMITTEE', 'HIGH');

-- ============================================================
-- Seed Data: Calendar Tithis - Durga Puja 2026
-- ============================================================

INSERT INTO calendar_tithis (id, festival, year, name, name_bengali, date, cultural_significance, is_peak_crowd) VALUES
('dp2026_mahalaya', 'DURGA_PUJA', 2026, 'Mahalaya', 'মহালয়া', '2026-10-03', 'Dawn invocation of the Goddess; Birendra Krishna Bhadra recital on radio', FALSE),
('dp2026_panchami', 'DURGA_PUJA', 2026, 'Panchami', 'পঞ্চমী', '2026-10-17', 'Pandal inauguration day; idol eyes painted (Chokkhu Daan)', FALSE),
('dp2026_shashthi', 'DURGA_PUJA', 2026, 'Shashthi', 'ষষ্ঠী', '2026-10-18', 'Bodhon (awakening) of the Goddess; face unveiling ceremony', TRUE),
('dp2026_saptami', 'DURGA_PUJA', 2026, 'Saptami', 'সপ্তমী', '2026-10-19', 'Nabapatrika Snan (bathing of nine plants); main puja begins', TRUE),
('dp2026_ashtami', 'DURGA_PUJA', 2026, 'Ashtami', 'অষ্টমী', '2026-10-20', 'Kumari Puja; Sandhi Puja at junction of Ashtami-Navami', TRUE),
('dp2026_navami', 'DURGA_PUJA', 2026, 'Navami', 'নবমী', '2026-10-21', 'Final day of worship; Maha Aarti in the evening', TRUE),
('dp2026_dashami', 'DURGA_PUJA', 2026, 'Dashami', 'দশমী', '2026-10-22', 'Sindoor Khela; Bishorjon (immersion) processions', TRUE);

-- ============================================================
-- Seed Data: Calendar Tithis - Jagaddhatri Puja 2026
-- ============================================================

INSERT INTO calendar_tithis (id, festival, year, name, name_bengali, date, cultural_significance, is_peak_crowd) VALUES
('jp2026_shashthi', 'JAGADDHATRI_PUJA', 2026, 'Shashthi', 'ষষ্ঠী', '2026-11-17', 'Puja begins; pandal inaugurations across Chandannagar', TRUE),
('jp2026_saptami', 'JAGADDHATRI_PUJA', 2026, 'Saptami', 'সপ্তমী', '2026-11-18', 'Main worship day; famous lighting displays begin', TRUE),
('jp2026_ashtami', 'JAGADDHATRI_PUJA', 2026, 'Ashtami', 'অষ্টমী', '2026-11-19', 'Peak crowd day; Chandannagar lighting at its best', TRUE),
('jp2026_navami', 'JAGADDHATRI_PUJA', 2026, 'Navami', 'নবমী', '2026-11-20', 'Final worship; immersion processions begin', TRUE);

-- ============================================================
-- Seed Data: Exit Nodes
-- ============================================================

INSERT INTO exit_nodes (id, name, name_bengali, category, latitude, longitude, contact_number, is_24hr, is_well_lit) VALUES
('exit_sovabazar_metro', 'Sovabazar Metro', 'শোভাবাজার মেট্রো', 'METRO', 22.5935, 88.3625, NULL, TRUE, TRUE),
('exit_girish_park_metro', 'Girish Park Metro', 'গিরিশ পার্ক মেট্রো', 'METRO', 22.5780, 88.3630, NULL, TRUE, TRUE),
('exit_sealdah_station', 'Sealdah Railway Station', 'শিয়ালদহ স্টেশন', 'RAILWAY', 22.5655, 88.3700, '033-23505535', TRUE, TRUE),
('exit_howrah_station', 'Howrah Railway Station', 'হাওড়া স্টেশন', 'RAILWAY', 22.5839, 88.3428, '033-26382217', TRUE, TRUE),
('exit_kalighat_metro', 'Kalighat Metro', 'কালীঘাট মেট্রো', 'METRO', 22.5195, 88.3440, NULL, TRUE, TRUE),
('exit_lake_town_police', 'Lake Town Police Booth', 'লেক টাউন পুলিশ বুথ', 'POLICE', 22.6100, 88.3870, '100', TRUE, TRUE),
('exit_sskm_hospital', 'SSKM Hospital', 'এসএসকেএম হাসপাতাল', 'MEDICAL', 22.5280, 88.3420, '033-22041101', TRUE, TRUE),
('exit_chandannagar_station', 'Chandannagar Railway Station', 'চন্দননগর স্টেশন', 'RAILWAY', 22.8680, 88.3640, NULL, TRUE, TRUE);

-- ============================================================
-- Seed Data: Artists
-- ============================================================

INSERT INTO artists (id, name, name_bengali, specialty, pandal_ids) VALUES
('artist_sanatan_dinda', 'Sanatan Dinda', 'সনাতন দিন্দা', 'Idol Making', ARRAY['dp2026_bagbazar']),
('artist_rintu_das', 'Rintu Das', 'ঋন্টু দাস', 'Pandal Art & Installation', ARRAY['dp2026_kumartuli']),
('artist_bhabatosh_sutar', 'Bhabatosh Sutar', 'ভবতোষ সূত্রধর', 'Theme Design', ARRAY['dp2026_suruchi']),
('artist_pradip_das', 'Pradip Das', 'প্রদীপ দাস', 'Light Art', ARRAY['dp2026_santosh_mitra']),
('artist_mintu_pal', 'Mintu Pal', 'মিন্টু পাল', 'Clay Sculpture', ARRAY['dp2026_college_square', 'dp2026_deshapriya']);
