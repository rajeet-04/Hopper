import type { Env } from '../index';

/**
 * Makes authenticated requests to Supabase PostgREST API.
 */
export async function supabaseQuery(
  env: Env,
  table: string,
  params?: URLSearchParams,
  options?: {
    method?: string;
    body?: string;
    headers?: Record<string, string>;
    rpc?: boolean;
  }
): Promise<Response> {
  const method = options?.method ?? 'GET';
  const isRpc = options?.rpc ?? false;

  const baseUrl = `${env.SUPABASE_URL}/rest/v1`;
  const endpoint = isRpc ? `${baseUrl}/rpc/${table}` : `${baseUrl}/${table}`;
  const url = params ? `${endpoint}?${params.toString()}` : endpoint;

  const headers: Record<string, string> = {
    'apikey': env.SUPABASE_SERVICE_KEY,
    'Authorization': `Bearer ${env.SUPABASE_SERVICE_KEY}`,
    'Content-Type': 'application/json',
    'Prefer': method === 'POST' ? 'return=representation' : 'count=exact',
    ...options?.headers,
  };

  const response = await fetch(url, {
    method,
    headers,
    body: options?.body,
  });

  return response;
}
