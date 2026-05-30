import { corsHeaders } from './cors';

/**
 * Wraps data in the ApiResponseDto format expected by the Android app.
 */
export function apiResponse<T>(data: T, meta?: { count?: number; version?: string }): Response {
  const body = {
    data,
    meta: {
      timestamp: Date.now(),
      ...meta,
    },
  };

  return new Response(JSON.stringify(body), {
    status: 200,
    headers: {
      ...corsHeaders,
      'Content-Type': 'application/json',
      'Cache-Control': 'public, max-age=60',
    },
  });
}

export function errorResponse(status: number, message: string): Response {
  return new Response(
    JSON.stringify({
      error: message,
      meta: { timestamp: Date.now() },
    }),
    {
      status,
      headers: {
        ...corsHeaders,
        'Content-Type': 'application/json',
      },
    }
  );
}
