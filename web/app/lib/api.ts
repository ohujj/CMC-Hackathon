const API_BASE = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

function getToken(): string | null {
  if (typeof window === "undefined") return null;
  return localStorage.getItem("token");
}

function emitToast(message: string) {
  if (typeof window === "undefined") return;
  window.dispatchEvent(new CustomEvent("filmo-toast", { detail: message }));
}

export async function apiFetch<T>(
  path: string,
  options: RequestInit = {},
): Promise<T> {
  const token = getToken();
  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    ...(options.headers as Record<string, string>),
  };
  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  const res = await fetch(`${API_BASE}${path}`, { ...options, headers });

  if (res.status === 401) {
    localStorage.removeItem("token");
    window.location.assign("/onboarding");
    throw new Error("Unauthorized");
  }

  const json = await res.json();

  if (!res.ok) {
    const msg = json?.message ?? "오류가 발생했습니다.";
    emitToast(msg);
    throw new Error(msg);
  }

  return json.data as T;
}

export function imageUrl(imagePath: string): string {
  return `${API_BASE}/api/movies/image/${imagePath}`;
}

export { API_BASE };
