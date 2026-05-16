"use client";

import { useState, useEffect, useCallback } from "react";
import { useRouter } from "next/navigation";
import { apiFetch, imageUrl } from "./lib/api";
import BottomTabBar from "./components/BottomTabBar";
import Toast from "./components/Toast";

type Movie = {
  seq: number;
  korTitle: string;
  engTitle: string;
  director: string;
  productionYear: string;
  genreName: string;
  imagePath: string;
};

type MovieDetail = Movie & {
  actors: string;
  companyNm: string;
  distributorNm: string;
  duration: string;
  rating: string;
  colorType: string;
  synopsis: string;
  screenwriter: string;
  producer: string;
  releaseDate: string;
  keywords: string;
};

type PageData = {
  content: Movie[];
  totalElements: number;
  totalPages: number;
  number: number;
};

export default function Home() {
  const router = useRouter();
  const [movies, setMovies] = useState<Movie[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [keyword, setKeyword] = useState("");
  const [inputValue, setInputValue] = useState("");
  const [selected, setSelected] = useState<MovieDetail | null>(null);
  const [loading, setLoading] = useState(false);
  const [authed, setAuthed] = useState(false);

  useEffect(() => {
    if (typeof window !== "undefined") {
      if (!localStorage.getItem("token")) {
        router.replace("/onboarding");
      } else {
        setAuthed(true);
      }
    }
  }, [router]);

  const fetchMovies = useCallback(
    async (p = 0) => {
      setLoading(true);
      try {
        const params = new URLSearchParams({
          page: String(p),
          size: "20",
          sort: "seq,desc",
        });
        if (keyword) params.set("keyword", keyword);
        const data = await apiFetch<PageData>(`/api/movies?${params}`);
        setMovies(data.content);
        setTotal(data.totalElements);
        setTotalPages(data.totalPages);
        setPage(data.number);
      } finally {
        setLoading(false);
      }
    },
    [keyword],
  );

  useEffect(() => {
    if (authed) fetchMovies(0);
  }, [authed, fetchMovies]);

  const openDetail = async (seq: number) => {
    try {
      const data = await apiFetch<MovieDetail>(`/api/movies/${seq}`);
      setSelected(data);
    } catch {
      // toast emitted by apiFetch
    }
  };

  const handleSearch = () => {
    setKeyword(inputValue);
  };

  const handleReset = () => {
    setInputValue("");
    setKeyword("");
  };

  if (!authed) return null;

  return (
    <div className="mx-auto max-w-[480px] min-h-screen bg-[#F5F5F5] relative">
      <Toast />

      {/* Header */}
      <header className="sticky top-0 bg-white border-b border-gray-200 z-10 px-4 py-3">
        <div className="flex items-center justify-between mb-3">
          <h1 className="text-lg font-bold text-gray-900">🎬 Filmo</h1>
          <span className="text-xs text-gray-400">
            {total.toLocaleString()}편
          </span>
        </div>
        <div className="flex gap-2">
          <input
            className="flex-1 bg-gray-100 rounded-full px-4 py-2 text-sm placeholder-gray-400 focus:outline-none focus:bg-white focus:border focus:border-gray-300 transition-colors"
            placeholder="영화 제목 / 감독 검색"
            value={inputValue}
            onChange={(e) => setInputValue(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && handleSearch()}
          />
          {inputValue && (
            <button
              onClick={handleReset}
              className="text-gray-400 text-sm px-2"
            >
              ✕
            </button>
          )}
          <button
            onClick={handleSearch}
            className="bg-gray-900 text-white text-sm px-4 py-2 rounded-full font-medium"
          >
            검색
          </button>
        </div>
      </header>

      {/* Movie Grid */}
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
              <div
                key={m.seq}
                onClick={() => openDetail(m.seq)}
                className="cursor-pointer"
              >
                <div className="aspect-[2/3] bg-gray-200 rounded-xl overflow-hidden">
                  {m.imagePath ? (
                    <img
                      src={imageUrl(m.imagePath)}
                      alt={m.korTitle}
                      className="w-full h-full object-cover"
                      onError={(e) => {
                        (e.target as HTMLImageElement).style.display = "none";
                      }}
                    />
                  ) : (
                    <div className="w-full h-full flex items-center justify-center text-gray-400 text-xs">
                      포스터 없음
                    </div>
                  )}
                </div>
                <div className="mt-1.5 px-0.5">
                  <p className="text-xs font-medium text-gray-900 truncate leading-tight">
                    {m.korTitle}
                  </p>
                  <p className="text-[10px] text-yellow-500 mt-0.5">
                    ★ 4.9 (21)
                  </p>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Pagination */}
        {totalPages > 1 && (
          <div className="flex items-center justify-center gap-2 mt-8">
            <button
              onClick={() => fetchMovies(page - 1)}
              disabled={page === 0}
              className="px-3 py-2 rounded-lg bg-white text-sm text-gray-600 border border-gray-200 disabled:opacity-30"
            >
              ← 이전
            </button>
            <span className="text-sm text-gray-500 px-2">
              {page + 1} / {totalPages}
            </span>
            <button
              onClick={() => fetchMovies(page + 1)}
              disabled={page >= totalPages - 1}
              className="px-3 py-2 rounded-lg bg-white text-sm text-gray-600 border border-gray-200 disabled:opacity-30"
            >
              다음 →
            </button>
          </div>
        )}
      </main>

      <BottomTabBar />

      {/* Movie Detail Modal */}
      {selected && (
        <div
          className="fixed inset-0 bg-black/60 flex items-end sm:items-center justify-center z-50"
          onClick={() => setSelected(null)}
        >
          <div
            className="bg-white rounded-t-3xl sm:rounded-2xl w-full max-w-[480px] max-h-[90vh] overflow-y-auto"
            onClick={(e) => e.stopPropagation()}
          >
            {/* Close bar */}
            <div className="flex justify-between items-center px-5 pt-4 pb-2">
              <h2 className="text-base font-bold text-gray-900 leading-tight">
                {selected.korTitle}
              </h2>
              <button
                onClick={() => setSelected(null)}
                className="text-gray-400 text-xl leading-none"
              >
                ✕
              </button>
            </div>

            <div className="flex gap-4 px-5 pb-4">
              <div className="w-28 shrink-0">
                <div className="aspect-[2/3] bg-gray-200 rounded-xl overflow-hidden">
                  {selected.imagePath && (
                    <img
                      src={imageUrl(selected.imagePath)}
                      alt={selected.korTitle}
                      className="w-full h-full object-cover"
                      onError={(e) => {
                        (e.target as HTMLImageElement).style.display = "none";
                      }}
                    />
                  )}
                </div>
              </div>
              <div className="flex-1 min-w-0">
                {selected.engTitle && (
                  <p className="text-xs text-gray-400 mb-2">
                    {selected.engTitle}
                  </p>
                )}
                <div className="flex flex-col gap-1">
                  {(
                    [
                      ["감독", selected.director],
                      ["출연", selected.actors],
                      ["장르", selected.genreName],
                      ["상영시간", selected.duration],
                      ["관람등급", selected.rating],
                      ["색상", selected.colorType],
                      ["제작사", selected.companyNm],
                      ["배급사", selected.distributorNm],
                      ["각본", selected.screenwriter],
                      ["키워드", selected.keywords],
                    ] as [string, string][]
                  )
                    .filter(([, v]) => v)
                    .map(([label, value]) => (
                      <div key={label} className="text-xs">
                        <span className="text-gray-400">{label} </span>
                        <span className="text-gray-800">{value}</span>
                      </div>
                    ))}
                </div>
              </div>
            </div>

            {selected.synopsis && (
              <div className="px-5 pb-6 border-t border-gray-100 pt-4">
                <h3 className="text-xs font-semibold text-gray-400 mb-2 uppercase tracking-wide">
                  시놉시스
                </h3>
                <p className="text-sm text-gray-700 leading-relaxed">
                  {selected.synopsis}
                </p>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
