"use client";

import { use, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { apiFetch, imageUrl } from "../../lib/api";
import Toast from "../../components/Toast";

type TicketDetail = {
  id: number;
  movieSeq: number;
  watchedDate: string;
  rating: number;
  review: string;
  likeCount: number;
  isLiked: boolean;
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

export default function TicketDetailPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = use(params);
  const router = useRouter();
  const [authed, setAuthed] = useState(false);
  const [ticket, setTicket] = useState<TicketDetail | null>(null);
  const [movie, setMovie] = useState<Movie | null>(null);
  const [liking, setLiking] = useState(false);
  const [nickname, setNickname] = useState("");

  useEffect(() => {
    if (typeof window === "undefined") return;
    const token = localStorage.getItem("token");
    if (!token) {
      router.replace("/onboarding");
      return;
    }
    setAuthed(true);
    setNickname(localStorage.getItem("nickname") ?? "");
  }, [router]);

  useEffect(() => {
    if (!authed) return;
    (async () => {
      try {
        const t = await apiFetch<TicketDetail>(`/api/tickets/${id}`);
        setTicket(t);
        const m = await apiFetch<Movie>(`/api/movies/${t.movieSeq}`);
        setMovie(m);
      } catch {
        // toast emitted by apiFetch
      }
    })();
  }, [authed, id]);

  const toggleLike = async () => {
    if (!ticket || liking) return;
    setLiking(true);
    const next = !ticket.isLiked;
    try {
      await apiFetch(`/api/likes/${ticket.id}`, {
        method: next ? "POST" : "DELETE",
      });
      setTicket({
        ...ticket,
        isLiked: next,
        likeCount: ticket.likeCount + (next ? 1 : -1),
      });
    } catch {
      // toast emitted by apiFetch
    } finally {
      setLiking(false);
    }
  };

  if (!authed || !ticket) return null;

  return (
    <div className="mx-auto max-w-[480px] min-h-screen bg-white pb-12">
      <Toast />

      <header className="sticky top-0 bg-white z-10 px-5 pt-12 pb-3 flex items-center">
        <button
          onClick={() => router.back()}
          className="text-xl text-gray-900 mr-2"
          aria-label="뒤로"
        >
          ‹
        </button>
        <h1 className="text-base font-bold text-gray-900 mx-auto pr-6">
          티켓 상세
        </h1>
      </header>

      <article className="mx-4 rounded-2xl overflow-hidden border border-gray-100 shadow-sm">
        <div className="aspect-[3/4] bg-gray-200 relative">
          {movie?.imagePath && (
            <img
              src={imageUrl(movie.imagePath)}
              alt={movie.korTitle}
              className="w-full h-full object-cover"
            />
          )}
        </div>
        <div className="bg-white px-4 py-3 flex items-start justify-between gap-2">
          <div className="min-w-0">
            <p className="text-base font-semibold text-gray-900 truncate">
              {movie?.korTitle ?? "..."}
            </p>
            <p className="text-xs text-gray-500 mt-0.5 truncate">
              {movie
                ? [
                    movie.genreName,
                    movie.director && `${movie.director} 감독`,
                    movie.productionYear && `${movie.productionYear}년`,
                    movie.duration,
                  ]
                    .filter(Boolean)
                    .join(" · ")
                : ""}
            </p>
          </div>
          <button
            onClick={toggleLike}
            disabled={liking}
            className="flex items-center gap-1 text-sm shrink-0"
            aria-label={ticket.isLiked ? "좋아요 취소" : "좋아요"}
          >
            <svg
              width="20"
              height="20"
              viewBox="0 0 24 24"
              fill={ticket.isLiked ? "#3b82f6" : "none"}
              stroke={ticket.isLiked ? "#3b82f6" : "currentColor"}
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z" />
            </svg>
            <span className="text-gray-700 tabular-nums">
              {ticket.likeCount.toLocaleString()}
            </span>
          </button>
        </div>
        <div className="bg-white px-4 pb-5 border-t border-dashed border-gray-200 pt-4">
          <div className="flex gap-6 text-xs">
            <div>
              <p className="text-gray-500 mb-1">별점</p>
              <p className="text-blue-500 font-medium">★ {ticket.rating}/5점</p>
            </div>
            <div>
              <p className="text-gray-500 mb-1">관람일</p>
              <p className="text-gray-900">
                {formatLongDate(ticket.watchedDate)}
              </p>
            </div>
          </div>
          <div className="mt-4">
            <p className="text-xs text-gray-500 mb-1">관람 후기</p>
            {nickname && (
              <p className="text-[11px] text-gray-500 mb-1">{nickname}</p>
            )}
            <p className="text-sm text-gray-800 whitespace-pre-wrap leading-relaxed">
              {ticket.review}
            </p>
          </div>
        </div>
      </article>
    </div>
  );
}
