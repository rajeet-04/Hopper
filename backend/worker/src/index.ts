/**
 * Hopper API Gateway - Cloudflare Worker
 *
 * Routes requests to Supabase, handles rate limiting,
 * caching, and response formatting.
 */

import { handlePandals } from './routes/pandals';
import { handleCrowdGet, handleCrowdPost } from './routes/crowd';
import { handleCalendar } from './routes/calendar';
import { handleArtists } from './routes/artists';
import { corsHeaders, handleOptions } from './utils/cors';
import { errorResponse } from './utils/response';

export interface Env {
  SUPABASE_URL: string;
  SUPABASE_SERVICE_KEY: string;
  ENVIRONMENT: string;
}

export default {
  async fetch(request: Request, env: Env, ctx: ExecutionContext): Promise<Response> {
    // Handle CORS preflight
    if (request.method === 'OPTIONS') {
      return handleOptions();
    }

    const url = new URL(request.url);
    const path = url.pathname;

    try {
      // Route matching
      if (path === '/v1/pandals' && request.method === 'GET') {
        return await handlePandals(request, env, ctx);
      }

      if (path.startsWith('/v1/crowd/') && request.method === 'GET') {
        const pandalId = path.replace('/v1/crowd/', '');
        return await handleCrowdGet(pandalId, env);
      }

      if (path === '/v1/crowd' && request.method === 'POST') {
        return await handleCrowdPost(request, env);
      }

      if (path === '/v1/calendar' && request.method === 'GET') {
        return await handleCalendar(request, env);
      }

      if (path === '/v1/artists' && request.method === 'GET') {
        return await handleArtists(env);
      }

      // Health check
      if (path === '/health') {
        return new Response(JSON.stringify({ status: 'ok', timestamp: Date.now() }), {
          headers: { ...corsHeaders, 'Content-Type': 'application/json' },
        });
      }

      return errorResponse(404, 'Not found');
    } catch (err) {
      console.error('Unhandled error:', err);
      return errorResponse(500, 'Internal server error');
    }
  },
};
