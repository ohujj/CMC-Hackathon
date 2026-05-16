"use client";

import { useCallback, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { apiFetch, imageUrl } from "../lib/api";
import BottomTabBar from "../components/BottomTabBar";
import Toast from "../components/Toast";

type Movie = {
  seq: number;
  korTitle: string;
  director: string;
  productionYear: string;
  genreName: string;
  duration: string;
  imagePath: string;
};

type PageData = {
  content: Movie[];
};

export default function RecordPage() {
  const router = useRouter();
  const [authed, setAuthed] = useState(false);
  const [movies, setMovies] = useState<Movie[]>([]);
  const [inputValue, setInputValue] = useState("");
  const [keyword, setKeyword] = useState("");
  const [loading, setLoading] = useState(false);
  const [selected, setSelected] = useState<Movie | null>(null);

  const [watchedDate, setWatchedDate] = useState(""); // YYYY-MM-DD
  const [rating, setRating] = useState(0);
  const [review, setReview] = useState("");
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (typeof window === "undefined") return;
    if (!localStorage.getItem("token")) router.replace("/onboarding");
    else setAuthed(true);
  }, [router]);

  const fetchMovies = useCallback(async () => {
    setLoading(true);
    try {
      const params = new URLSearchParams({
        page: "0",
        size: "30",
        sort: "seq,desc",
      });
      if (keyword) params.set("keyword", keyword);
      const data = await apiFetch<PageData>(`/api/movies?${params}`);
      setMovies(data.content);
    } finally {
      setLoading(false);
    }
  }, [keyword]);

  useEffect(() => {
    if (authed) fetchMovies();
  }, [authed, fetchMovies]);

  const onSubmit = async () => {
    if (!selected || !watchedDate || !rating) return;
    setSubmitting(true);
    try {
      await apiFetch("/api/tickets", {
        method: "POST",
        body: JSON.stringify({
          movieSeq: selected.seq,
          watchedDate,
          watchedTime: "12:00:00",
          rating,
          review,
        }),
      });
      router.replace("/");
    } catch {
      // toast emitted by apiFetch
    } finally {
      setSubmitting(false);
    }
  };

  if (!authed) return null;

  // Step 2: 관람 정보 입력
  if (selected) {
    const canSubmit = !!watchedDate && rating > 0 && !submitting;
    return (
      <div className="mx-auto max-w-[480px] min-h-screen bg-white pb-32">
        <Toast />

        <header className="sticky top-0 bg-white z-10 px-5 pt-12 pb-3 flex items-center">
          <button
            onClick={() => setSelected(null)}
            className="text-xl text-gray-900 mr-2"
            aria-label="뒤로"
          >
            ‹
          </button>
          <h1 className="text-base font-bold text-gray-900 mx-auto pr-6">
            관람 정보 입력
          </h1>
        </header>

        <main className="px-5 space-y-6">
          <section>
            <p className="text-xs text-gray-500 mb-2">영화</p>
            <div className="flex justify-center">
              <div className="w-44 aspect-[2/3] bg-gray-200 rounded-xl overflow-hidden">
                {selected.imagePath && (
                  <img
                    src={imageUrl(selected.imagePath)}
                    alt={selected.korTitle}
                    className="w-full h-full object-cover"
                  />
                )}
              </div>
            </div>
            <div className="mt-3 text-center">
              <p className="text-base font-semibold text-gray-900">
                {selected.korTitle}
              </p>
              <p className="text-xs text-gray-500 mt-1">
                {[
                  selected.genreName,
                  selected.director && `${selected.director} 감독`,
                  selected.productionYear && `${selected.productionYear}년`,
                  selected.duration && `${selected.duration}분`,
                ]
                  .filter(Boolean)
                  .join(" · ")}
              </p>
            </div>
          </section>

          <section>
            <p className="text-xs text-gray-500 mb-2">관람일</p>
            <input
              type="date"
              value={watchedDate}
              onChange={(e) => setWatchedDate(e.target.value)}
              className="w-full bg-gray-100 rounded-lg px-4 py-3 text-sm"
            />
          </section>

          <section>
            <p className="text-xs text-gray-500 mb-2">별점</p>
            <div className="flex gap-1">
              {[1, 2, 3, 4, 5].map((n) => (
                <button
                  key={n}
                  type="button"
                  onClick={() => setRating(n)}
                  className="text-2xl"
                >
                  <span
                    className={n <= rating ? "text-blue-500" : "text-gray-300"}
                  >
                    ★
                  </span>
                </button>
              ))}
            </div>
          </section>

          <section>
            <p className="text-xs text-gray-500 mb-2">관람 후기</p>
            <textarea
              value={review}
              onChange={(e) => {
                if (e.target.value.length <= 100) setReview(e.target.value);
              }}
              placeholder="내용 입력 공백 포함 최대 100자"
              className="w-full bg-gray-100 rounded-lg px-4 py-3 text-sm h-28 resize-none"
            />
            <p className="text-[10px] text-gray-400 text-right mt-1">
              {review.length}/100
            </p>
          </section>
        </main>

        <div className="fixed bottom-0 left-1/2 -translate-x-1/2 w-full max-w-[480px] bg-white px-5 py-4 border-t border-gray-100">
          <button
            disabled={!canSubmit}
            onClick={onSubmit}
            className={`w-full py-4 rounded-xl text-sm font-semibold transition-colors ${
              canSubmit ? "bg-gray-900 text-white" : "bg-gray-200 text-gray-400"
            }`}
          >
            {submitting ? "발행 중..." : "완료"}
          </button>
        </div>
      </div>
    );
  }

  // Step 1: 영화 검색
  return (
    <div className="mx-auto max-w-[480px] min-h-screen bg-white">
      <Toast />

      <header className="sticky top-0 bg-white z-10 px-5 pt-12 pb-3">
        <h1 className="text-xl font-bold text-gray-900 mb-3">기록하기</h1>
        <div className="relative">
          <input
            className="w-full bg-gray-100 rounded-full px-4 py-2.5 pr-10 text-sm placeholder-gray-400 focus:outline-none"
            placeholder="관람한 영화를 검색하세요"
            value={inputValue}
            onChange={(e) => setInputValue(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && setKeyword(inputValue)}
          />
          <button
            onClick={() => setKeyword(inputValue)}
            className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500"
            aria-label="검색"
          >
            <svg
              width="18"
              height="18"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <circle cx="11" cy="11" r="8" />
              <path d="m21 21-4.3-4.3" />
            </svg>
          </button>
        </div>
      </header>

      <main className="px-4 py-4 pb-24">
        {loading ? (
          <div className="text-center text-gray-400 py-20 text-sm">
            불러오는 중...
          </div>
        ) : movies.length === 0 ? (
          <div className="text-center text-gray-400 py-20 text-sm">
            검색 결과가 없습니다.
          </div>
        ) : (
          <div className="grid grid-cols-3 gap-3">
            {movies.map((m) => (
              <button
                key={m.seq}
                onClick={() => setSelected(m)}
                className="text-left"
              >
                <div className="aspect-[2/3] bg-gray-200 rounded-xl overflow-hidden">
                  {m.imagePath && (
                    <img
                      src={imageUrl(m.imagePath)}
                      alt={m.korTitle}
                      className="w-full h-full object-cover"
                    />
                  )}
                </div>
                <p className="text-xs font-medium text-gray-900 truncate mt-1.5">
                  {m.korTitle}
                </p>
              </button>
            ))}
          </div>
        )}
      </main>

      <BottomTabBar />
    </div>
  );
}
