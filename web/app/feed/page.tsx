"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { apiFetch, imageUrl } from "../lib/api";
import BottomTabBar from "../components/BottomTabBar";
import Toast from "../components/Toast";

type Ticket = {
  id: number;
  movieSeq: number;
  rating: number;
  review: string;
  watchedDate: string;
};

type Movie = {
  seq: number;
  korTitle: string;
  director: string;
  productionYear: string;
  genreName: string;
  duration: string;
  imagePath: string;
};

function formatLongDate(iso: string): string {
  const [y, m, d] = iso.split("-").map(Number);
  const dt = new Date(y, m - 1, d);
  const day = ["일", "월", "화", "수", "목", "금", "토"][dt.getDay()];
  return `${y}년 ${String(m).padStart(2, "0")}월 ${String(d).padStart(2, "0")}일 (${day})`;
}

export default function FeedPage() {
  const router = useRouter();
  const [authed, setAuthed] = useState(false);
  const [tickets, setTickets] = useState<Ticket[]>([]);
  const [movies, setMovies] = useState<Record<number, Movie>>({});
  const [loading, setLoading] = useState(true);
  const [nickname, setNickname] = useState("");

  useEffect(() => {
    if (typeof window === "undefined") return;
    if (!localStorage.getItem("token")) router.replace("/onboarding");
    else {
      setAuthed(true);
      setNickname(localStorage.getItem("nickname") ?? "");
    }
  }, [router]);

  useEffect(() => {
    if (!authed) return;
    (async () => {
      try {
        const list = await apiFetch<Ticket[]>(
          "/api/tickets/public?sort=latest",
        );
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

      <header className="sticky top-0 bg-white z-10 px-5 pt-12 pb-3">
        <h1 className="text-xl font-bold text-gray-900">티켓보기</h1>
      </header>

      <main className="px-5 py-3 pb-24 space-y-6">
        {loading ? (
          <div className="text-center text-gray-400 py-20 text-sm">
            불러오는 중...
          </div>
        ) : tickets.length === 0 ? (
          <div className="text-center text-gray-400 py-20 text-sm">
            공개된 티켓이 없습니다.
          </div>
        ) : (
          tickets.map((t) => {
            const m = movies[t.movieSeq];
            return (
              <article
                key={t.id}
                className="rounded-2xl overflow-hidden border border-gray-100 shadow-sm"
              >
                <Link href={`/tickets/${t.id}`}>
                  <div className="aspect-[3/4] bg-gray-200 relative">
                    {m?.imagePath && (
                      <img
                        src={imageUrl(m.imagePath)}
                        alt={m.korTitle}
                        className="w-full h-full object-cover"
                      />
                    )}
                  </div>
                </Link>
                <div className="bg-white px-4 py-3 flex items-start justify-between gap-2">
                  <div className="min-w-0">
                    <p className="text-base font-semibold text-gray-900 truncate">
                      {m?.korTitle ?? "..."}
                    </p>
                    <p className="text-xs text-gray-500 mt-0.5 truncate">
                      {m
                        ? [
                            m.genreName,
                            m.director && `${m.director} 감독`,
                            m.productionYear && `${m.productionYear}년`,
                            m.duration && `${m.duration}분`,
                          ]
                            .filter(Boolean)
                            .join(" · ")
                        : ""}
                    </p>
                  </div>
                </div>
                <div className="bg-white px-4 pb-4 border-t border-dashed border-gray-200 pt-3">
                  <div className="flex gap-6 text-xs">
                    <div>
                      <p className="text-gray-500 mb-1">별점</p>
                      <p className="text-blue-500 font-medium">
                        ★ {t.rating}/5점
                      </p>
                    </div>
                    <div>
                      <p className="text-gray-500 mb-1">관람일</p>
                      <p className="text-gray-900">
                        {formatLongDate(t.watchedDate)}
                      </p>
                    </div>
                  </div>
                  <div className="mt-3">
                    <p className="text-xs text-gray-500 mb-1">관람 후기</p>
                    {nickname && (
                      <p className="text-[11px] text-gray-500 mb-1">
                        {nickname}
                      </p>
                    )}
                    <p className="text-sm text-gray-800 whitespace-pre-wrap">
                      {t.review}
                    </p>
                  </div>
                </div>
              </article>
            );
          })
        )}
      </main>

      <BottomTabBar />
    </div>
  );
}
