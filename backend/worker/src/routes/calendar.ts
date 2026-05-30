import type { Env } from '../index';
import { supabaseQuery } from '../utils/supabase';
import { apiResponse, errorResponse } from '../utils/response';
import { isRateLimited } from '../utils/ratelimit';

/**
 * GET /v1/calendar?festival=DURGA_PUJA&year=2026
 *
 * Returns the tithi calendar for a festival edition.
 */
export async function handleCalendar(request: Request, env: Env): Promise<Response> {
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

  if (!['DURGA_PUJA', 'JAGADDHATRI_PUJA'].includes(festival)) {
    return errorResponse(400, 'Invalid festival');
  }

  const params = new URLSearchParams();
  params.set('festival', `eq.${festival}`);
  params.set('year', `eq.${year}`);
  params.set('order', 'date.asc');

  const response = await supabaseQuery(env, 'calendar_tithis', params);

  if (!response.ok) {
    return errorResponse(502, 'Upstream error');
  }

  const tithis = await response.json() as any[];

  // Transform to match TithiDto
  const dtos = tithis.map((t: any) => ({
    id: t.id,
    festival: t.festival,
    year: t.year,
    name: t.name,
    nameBengali: t.name_bengali,
    date: new Date(t.date).getTime(),
    culturalSignificance: t.cultural_significance,
    isPeakCrowd: t.is_peak_crowd,
  }));

  return apiResponse(dtos, { count: dtos.length });
}
