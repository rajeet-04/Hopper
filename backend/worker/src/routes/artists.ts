import type { Env } from '../index';
import { supabaseQuery } from '../utils/supabase';
import { apiResponse, errorResponse } from '../utils/response';

/**
 * GET /v1/artists
 *
 * Returns all artists with their pandal associations.
 */
export async function handleArtists(env: Env): Promise<Response> {
  const params = new URLSearchParams();
  params.set('order', 'name.asc');

  const response = await supabaseQuery(env, 'artists', params);

  if (!response.ok) {
    return errorResponse(502, 'Upstream error');
  }

  const artists = await response.json() as any[];

  // Transform to match ArtistDto
  const dtos = artists.map((a: any) => ({
    id: a.id,
    name: a.name,
    nameBengali: a.name_bengali,
    specialty: a.specialty,
    pandalIds: a.pandal_ids ?? [],
  }));

  return apiResponse(dtos, { count: dtos.length });
}
