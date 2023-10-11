/**
 * Fetches the base URL of the backend server.
 */
export function getBaseUrl(): string {
    return import.meta.env.VITE_BACKEND_URL;
}
