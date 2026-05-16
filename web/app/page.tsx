"use client";

import { useState, useEffect, useCallback } from "react";

const API = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

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
  const [movies, setMovies] = useState<Movie[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [keyword, setKeyword] = useState("");
  const [genre, setGenre] = useState("");
  const [year, setYear] = useState("");
  const [selected, setSelected] = useState<MovieDetail | null>(null);
  const [loading, setLoading] = useState(false);

  const fetchMovies = useCallback(
    async (p = 0) => {
      setLoading(true);
      const params = new URLSearchParams({ page: String(p), size: "20" });
      if (keyword) params.set("keyword", keyword);
      if (genre) params.set("genre", genre);
      if (year) params.set("year", year);
      const res = await fetch(`${API}/api/movies?${params}`);
      const json = await res.json();
      const data: PageData = json.data;
      setMovies(data.content);
      setTotal(data.totalElements);
      setTotalPages(data.totalPages);
      setPage(data.number);
      setLoading(false);
    },
    [keyword, genre, year],
  );

  useEffect(() => {
    fetchMovies(0);
  }, [fetchMovies]);

  const openDetail = async (seq: number) => {
    const res = await fetch(`${API}/api/movies/${seq}`);
    const json = await res.json();
    setSelected(json.data);
  };

  const imageUrl = (path: string) =>
    `${API}/api/movies/image/${path.replace("fileFolder/", "")}`;

  const GENRES = ["극영화", "다큐멘터리", "애니메이션", "실험영화"];
  const YEARS = Array.from({ length: 10 }, (_, i) => String(2025 - i));

  return (
    <div className="min-h-screen bg-zinc-950 text-white">
      <header className="border-b border-zinc-800 px-6 py-4 flex items-center gap-6">
        <h1 className="text-xl font-bold tracking-tight">
          🎬 독립영화 아카이브
        </h1>
        <span className="text-zinc-400 text-sm">
          {total.toLocaleString()}편
        </span>
      </header>

      <div className="px-6 py-4 flex flex-wrap gap-3 border-b border-zinc-800">
        <input
          className="bg-zinc-900 border border-zinc-700 rounded px-3 py-2 text-sm w-52 placeholder-zinc-500 focus:outline-none focus:border-zinc-500"
          placeholder="제목 / 감독 검색"
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && fetchMovies(0)}
        />
        <select
          className="bg-zinc-900 border border-zinc-700 rounded px-3 py-2 text-sm focus:outline-none"
          value={genre}
          onChange={(e) => setGenre(e.target.value)}
        >
          <option value="">장르 전체</option>
          {GENRES.map((g) => (
            <option key={g} value={g}>
              {g}
            </option>
          ))}
        </select>
        <select
          className="bg-zinc-900 border border-zinc-700 rounded px-3 py-2 text-sm focus:outline-none"
          value={year}
          onChange={(e) => setYear(e.target.value)}
        >
          <option value="">연도 전체</option>
          {YEARS.map((y) => (
            <option key={y} value={y}>
              {y}
            </option>
          ))}
        </select>
        <button
          onClick={() => fetchMovies(0)}
          className="bg-white text-black text-sm px-4 py-2 rounded font-medium hover:bg-zinc-200"
        >
          검색
        </button>
        <button
          onClick={() => {
            setKeyword("");
            setGenre("");
            setYear("");
          }}
          className="text-zinc-400 text-sm px-3 py-2 hover:text-white"
        >
          초기화
        </button>
      </div>

      <main className="px-6 py-6">
        {loading ? (
          <div className="text-center text-zinc-500 py-20">불러오는 중...</div>
        ) : (
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-4">
            {movies.map((m) => (
              <div
                key={m.seq}
                onClick={() => openDetail(m.seq)}
                className="cursor-pointer group"
              >
                <div className="aspect-[2/3] bg-zinc-800 rounded overflow-hidden">
                  {m.imagePath ? (
                    <img
                      src={imageUrl(m.imagePath)}
                      alt={m.korTitle}
                      className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-200"
                      onError={(e) => {
                        (e.target as HTMLImageElement).style.display = "none";
                      }}
                    />
                  ) : (
                    <div className="w-full h-full flex items-center justify-center text-zinc-600 text-xs">
                      포스터 없음
                    </div>
                  )}
                </div>
                <div className="mt-2">
                  <p className="text-sm font-medium truncate">{m.korTitle}</p>
                  <p className="text-xs text-zinc-400 truncate">
                    {m.director} · {m.productionYear}
                  </p>
                  <p className="text-xs text-zinc-500 truncate">
                    {m.genreName}
                  </p>
                </div>
              </div>
            ))}
          </div>
        )}

        {totalPages > 1 && (
          <div className="flex justify-center gap-2 mt-8">
            <button
              onClick={() => fetchMovies(page - 1)}
              disabled={page === 0}
              className="px-3 py-1 rounded bg-zinc-800 text-sm disabled:opacity-30 hover:bg-zinc-700"
            >
              ←
            </button>
            <span className="px-3 py-1 text-sm text-zinc-400">
              {page + 1} / {totalPages}
            </span>
            <button
              onClick={() => fetchMovies(page + 1)}
              disabled={page >= totalPages - 1}
              className="px-3 py-1 rounded bg-zinc-800 text-sm disabled:opacity-30 hover:bg-zinc-700"
            >
              →
            </button>
          </div>
        )}
      </main>

      {selected && (
        <div
          className="fixed inset-0 bg-black/80 flex items-center justify-center z-50 p-4"
          onClick={() => setSelected(null)}
        >
          <div
            className="bg-zinc-900 rounded-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="flex gap-5 p-6">
              <div className="w-36 shrink-0">
                <div className="aspect-[2/3] bg-zinc-800 rounded overflow-hidden">
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
                <h2 className="text-lg font-bold">{selected.korTitle}</h2>
                <p className="text-zinc-400 text-sm mb-3">
                  {selected.engTitle}
                </p>
                <div className="grid grid-cols-2 gap-x-4 gap-y-1 text-sm">
                  {(
                    [
                      ["감독", selected.director],
                      ["출연", selected.actors],
                      ["제작연도", selected.productionYear],
                      ["개봉", selected.releaseDate],
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
                      <div key={label}>
                        <span className="text-zinc-500">{label} </span>
                        <span className="text-zinc-200">{value}</span>
                      </div>
                    ))}
                </div>
              </div>
            </div>
            {selected.synopsis && (
              <div className="px-6 pb-6">
                <h3 className="text-sm font-semibold text-zinc-400 mb-2">
                  시놉시스
                </h3>
                <p className="text-sm text-zinc-300 leading-relaxed">
                  {selected.synopsis}
                </p>
              </div>
            )}
            <div className="px-6 pb-6 text-right">
              <button
                onClick={() => setSelected(null)}
                className="text-sm text-zinc-500 hover:text-white"
              >
                닫기
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
