import type { Env } from '../index';
import { supabaseQuery } from '../utils/supabase';
import { apiResponse, errorResponse } from '../utils/response';
import { isRateLimited } from '../utils/ratelimit';

/**
 * GET /v1/pandals?festival=DURGA_PUJA&year=2026
 *
 * Returns pandals filtered by festival and year.
 * Supports If-Modified-Since for stale-while-revalidate caching.
 */
export async function handlePandals(
  request: Request,
  env: Env,
  ctx: ExecutionContext
): Promise<Response> {
  const clientIp = request.headers.get('CF-Connecting-IP') ?? 'unknown';
  if (isRateLimited(clientIp)) {
    return errorResponse(429, 'Too many requests');
  }

  const url = new URL(request.url);
  const festival = url.searchParams.get('festival');
  const year = url.searchParams.get('year');

  if (!festival || !year) {
    return errorResponse(400, 'Missing required parameters: festival, year');
  }

  // Validate festival enum
  if (!['DURGA_PUJA', 'JAGADDHATRI_PUJA'].includes(festival)) {
    return errorResponse(400, 'Invalid festival. Must be DURGA_PUJA or JAGADDHATRI_PUJA');
  }

  const params = new URLSearchParams();
  params.set('festival', `eq.${festival}`);
  params.set('year', `eq.${year}`);
  params.set('order', 'significance_rank.asc');
  // Select only fields the app needs (exclude PostGIS geography column)
  params.set('select', 'id,name,name_bengali,latitude,longitude,city,neighborhood,festival,year,theme,committee_name,established_year,artisan_credits_json,awards,photos,significance_rank,source_type,confidence_level,updated_at');

  const response = await supabaseQuery(env, 'pandals', params);

  if (!response.ok) {
    const text = await response.text();
    console.error('Supabase error:', text);
    return errorResponse(502, 'Upstream error');
  }

  const pandals = await response.json() as any[];

  // Transform to match PandalDto expected by the app
  const dtos = pandals.map((p: any) => ({
    id: p.id,
    name: p.name,
    nameBengali: p.name_bengali,
    latitude: p.latitude,
    longitude: p.longitude,
    city: p.city,
    neighborhood: p.neighborhood,
    festival: p.festival,
    year: p.year,
    theme: p.theme,
    committeeName: p.committee_name,
    establishedYear: p.established_year,
    significanceRank: p.significance_rank,
    sourceType: p.source_type,
    confidenceLevel: p.confidence_level,
  }));

  const resp = apiResponse(dtos, { count: dtos.length, version: '1.0' });

  // Add Last-Modified header for conditional requests
  const latestUpdate = pandals.reduce((max: string, p: any) => {
    return p.updated_at > max ? p.updated_at : max;
  }, '');
  if (latestUpdate) {
    resp.headers.set('Last-Modified', new Date(latestUpdate).toUTCString());
  }

  return resp;
}
