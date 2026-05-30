-- ============================================================
-- Scheduled cleanup of expired crowd reports
-- Uses pg_cron (available on Supabase free tier)
-- ============================================================

-- Enable pg_cron extension
CREATE EXTENSION IF NOT EXISTS pg_cron;

-- Schedule cleanup every 5 minutes: delete expired crowd reports
SELECT cron.schedule(
    'cleanup-expired-crowd-reports',
    '*/5 * * * *',
    $$DELETE FROM crowd_reports WHERE expires_at < NOW()$$
);

-- Schedule cleanup of expired lost person posts every hour
SELECT cron.schedule(
    'cleanup-expired-lost-persons',
    '0 * * * *',
    $$UPDATE lost_persons SET is_resolved = TRUE WHERE expires_at < NOW() AND is_resolved = FALSE$$
);
