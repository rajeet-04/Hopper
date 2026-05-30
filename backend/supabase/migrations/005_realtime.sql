-- ============================================================
-- Enable Supabase Realtime for crowd_reports table
-- This allows the Android app to subscribe to live crowd updates
-- ============================================================

-- Enable realtime for crowd_reports
ALTER PUBLICATION supabase_realtime ADD TABLE crowd_reports;

-- Enable realtime for lost_persons (for community alerts)
ALTER PUBLICATION supabase_realtime ADD TABLE lost_persons;
