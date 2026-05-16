"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { apiFetch, imageUrl } from "./lib/api";
import BottomTabBar from "./components/BottomTabBar";
import Toast from "./components/Toast";

type Ticket = {
  id: number;
  movieSeq: number;
  rating: number;
  watchedDate: string;
};

type Movie = {
  seq: number;
  korTitle: string;
  imagePath: string;
};

function formatDate(iso: string): string {
  // 2026-05-16 → 26/05/16
  const [y, m, d] = iso.split("-");
  return `${y.slice(2)}/${m}/${d}`;
}

export default function CollectionPage() {
  const router = useRouter();
  const [authed, setAuthed] = useState(false);
  const [tickets, setTickets] = useState<Ticket[]>([]);
  const [movies, setMovies] = useState<Record<number, Movie>>({});
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (typeof window === "undefined") return;
    if (!localStorage.getItem("token")) {
      router.replace("/onboarding");
    } else {
      setAuthed(true);
    }
  }, [router]);

  useEffect(() => {
    if (!authed) return;
    (async () => {
      try {
        const list = await apiFetch<Ticket[]>("/api/tickets");
        setTickets(list);
        const uniqueSeqs = Array.from(new Set(list.map((t) => t.movieSeq)));
        const fetched = await Promise.all(
          uniqueSeqs.map((seq) =>
            apiFetch<Movie>(`/api/movies/${seq}`).catch(() => null),
          ),
        );
        const map: Record<number, Movie> = {};
        fetched.forEach((mv) => {
          if (mv) map[mv.seq] = mv;
        });
        setMovies(map);
      } finally {
        setLoading(false);
      }
    })();
  }, [authed]);

  if (!authed) return null;

  return (
    <div className="mx-auto max-w-[480px] min-h-screen bg-white">
      <Toast />

      <header className="sticky top-0 bg-white z-10 px-5 pt-12 pb-4">
        <h1 className="text-xl font-bold text-gray-900">컬렉션</h1>
      </header>

      <main className="px-4 pb-24">
        {loading ? (
          <div className="text-center text-gray-400 py-20 text-sm">
            불러오는 중...
          </div>
        ) : tickets.length === 0 ? (
          <div className="text-center text-gray-400 py-20 text-sm">
            아직 발행한 티켓이 없습니다.
          </div>
        ) : (
          <div className="grid grid-cols-2 gap-3">
            {tickets.map((t) => {
              const m = movies[t.movieSeq];
              return (
                <Link key={t.id} href={`/tickets/${t.id}`} className="block">
                  <div className="relative aspect-[2/3] bg-gray-200 rounded-xl overflow-hidden">
                    {m?.imagePath && (
                      <img
                        src={imageUrl(m.imagePath)}
                        alt={m.korTitle}
                        className="w-full h-full object-cover"
                        onError={(e) => {
                          (e.currentTarget as HTMLImageElement).style.display =
                            "none";
                        }}
                      />
                    )}
                    <div className="absolute inset-x-0 bottom-0 bg-gradient-to-t from-black/80 to-transparent p-3">
                      <p className="text-white text-sm font-semibold truncate">
                        {m?.korTitle ?? "..."}
                      </p>
                      <div className="flex items-center gap-2 text-[11px] text-white/90 mt-0.5">
                        <span className="text-blue-300">★ {t.rating}/5점</span>
                        <span className="text-white/70">·</span>
                        <span>{formatDate(t.watchedDate)}</span>
                      </div>
                    </div>
                  </div>
                </Link>
              );
            })}
          </div>
        )}
      </main>

      <BottomTabBar />
    </div>
  );
}
