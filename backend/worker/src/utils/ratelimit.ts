/**
 * Simple in-memory rate limiter using Cloudflare Worker's isolate memory.
 * 
 * Note: This is per-isolate, not globally distributed. For a free-tier app
 * with moderate traffic, this provides reasonable protection. For stricter
 * global rate limiting, upgrade to Cloudflare Workers KV or Durable Objects.
 */

interface RateLimitEntry {
  count: number;
  resetAt: number;
}

const rateLimitMap = new Map<string, RateLimitEntry>();

const WINDOW_MS = 60_000; // 1 minute window
const MAX_REQUESTS = 30; // 30 requests per minute per IP

/**
 * Returns true if the request should be rate-limited (blocked).
 */
export function isRateLimited(clientIp: string): boolean {
  const now = Date.now();
  const entry = rateLimitMap.get(clientIp);

  if (!entry || now > entry.resetAt) {
    rateLimitMap.set(clientIp, { count: 1, resetAt: now + WINDOW_MS });
    return false;
  }

  entry.count++;
  if (entry.count > MAX_REQUESTS) {
    return true;
  }

  return false;
}

/**
 * Stricter rate limit for crowd report submissions.
 * 5 submissions per minute per IP.
 */
const crowdLimitMap = new Map<string, RateLimitEntry>();
const CROWD_MAX = 5;

export function isCrowdRateLimited(clientIp: string): boolean {
  const now = Date.now();
  const entry = crowdLimitMap.get(clientIp);

  if (!entry || now > entry.resetAt) {
    crowdLimitMap.set(clientIp, { count: 1, resetAt: now + WINDOW_MS });
    return false;
  }

  entry.count++;
  return entry.count > CROWD_MAX;
}
