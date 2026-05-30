import type { Env } from '../index';
import { supabaseQuery } from '../utils/supabase';
import { apiResponse, errorResponse } from '../utils/response';
import { isCrowdRateLimited, isRateLimited } from '../utils/ratelimit';

/**
 * GET /v1/crowd/{pandalId}
 *
 * Returns the aggregated crowd level for a pandal using weighted median.
 */
export async function handleCrowdGet(pandalId: string, env: Env): Promise<Response> {
  if (!pandalId) {
    return errorResponse(400, 'Missing pandalId');
  }

  // Call the PostgreSQL function for weighted median calculation
  const response = await supabaseQuery(env, 'get_aggregated_crowd', undefined, {
    method: 'POST',
    body: JSON.stringify({ p_pandal_id: pandalId }),
    rpc: true,
  });

  if (!response.ok) {
    const text = await response.text();
    console.error('Supabase RPC error:', text);
    return errorResponse(502, 'Upstream error');
  }

  const results = await response.json() as any[];
  const result = results[0] ?? { pandal_id: pandalId, bucket: 'GREEN', report_count: 0 };

  return apiResponse({
    pandalId: result.pandal_id,
    bucket: result.bucket,
    reportCount: Number(result.report_count),
  });
}

/**
 * POST /v1/crowd
 *
 * Submits a crowd report. Enforces:
 * - IP-based rate limiting (5/min)
 * - Device+pandal rate limiting (1 per 10 min, checked in DB)
 * - Input validation
 */
export async function handleCrowdPost(request: Request, env: Env): Promise<Response> {
  const clientIp = request.headers.get('CF-Connecting-IP') ?? 'unknown';

  // IP-level rate limit
  if (isCrowdRateLimited(clientIp)) {
    return errorResponse(429, 'Too many crowd reports. Please wait.');
  }

  let body: any;
  try {
    body = await request.json();
  } catch {
    return errorResponse(400, 'Invalid JSON body');
  }

  const { pandalId, bucket, deviceHash, reportedAt } = body;

  // Validate required fields
  if (!pandalId || !bucket || !deviceHash) {
    return errorResponse(400, 'Missing required fields: pandalId, bucket, deviceHash');
  }

  // Validate bucket enum
  if (!['GREEN', 'YELLOW', 'RED'].includes(bucket)) {
    return errorResponse(400, 'Invalid bucket. Must be GREEN, YELLOW, or RED');
  }

  // Validate deviceHash format (SHA-256 = 64 hex chars)
  if (!/^[a-f0-9]{64}$/i.test(deviceHash)) {
    return errorResponse(400, 'Invalid deviceHash format');
  }

  // Check DB-level rate limit (10 min per device per pandal)
  const rateLimitResp = await supabaseQuery(env, 'is_rate_limited', undefined, {
    method: 'POST',
    body: JSON.stringify({ p_device_hash: deviceHash, p_pandal_id: pandalId }),
    rpc: true,
  });

  if (rateLimitResp.ok) {
    const isLimited = await rateLimitResp.json();
    if (isLimited === true) {
      return errorResponse(429, 'Rate limited: please wait 10 minutes before reporting again for this pandal');
    }
  }

  // Get reporter's weight multiplier from reputation table
  const repParams = new URLSearchParams();
  repParams.set('device_hash', `eq.${deviceHash}`);
  repParams.set('select', 'weight_multiplier');

  const repResp = await supabaseQuery(env, 'reputation', repParams);
  let weightMultiplier = 1.0;
  if (repResp.ok) {
    const repData = await repResp.json() as any[];
    if (repData.length > 0) {
      weightMultiplier = repData[0].weight_multiplier ?? 1.0;
    }
  }

  // Calculate expiry (20 minutes from now)
  const now = new Date();
  const expiresAt = new Date(now.getTime() + 20 * 60 * 1000);

  // Insert the crowd report
  const insertResp = await supabaseQuery(env, 'crowd_reports', undefined, {
    method: 'POST',
    body: JSON.stringify({
      pandal_id: pandalId,
      bucket,
      device_hash: deviceHash,
      reported_at: now.toISOString(),
      expires_at: expiresAt.toISOString(),
      weight_multiplier: weightMultiplier,
    }),
  });

  if (!insertResp.ok) {
    const text = await insertResp.text();
    console.error('Insert error:', text);
    return errorResponse(502, 'Failed to submit report');
  }

  // Update reputation: increment total_reports
  await supabaseQuery(env, 'reputation', undefined, {
    method: 'POST',
    body: JSON.stringify({
      device_hash: deviceHash,
      total_reports: 1,
      accurate_reports: 0,
      accuracy: 0.0,
      weight_multiplier: 1.0,
    }),
    headers: {
      'Prefer': 'resolution=merge-duplicates',
    },
  });

  return apiResponse(null, { version: '1.0' });
}
