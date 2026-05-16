"use client";

import { useState, useEffect, useCallback } from "react";
import { useRouter } from "next/navigation";
import { apiFetch } from "../lib/api";
import BottomTabBar from "../components/BottomTabBar";
import Toast from "../components/Toast";

type Theater = {
  theaCd: string;
  theaName: string;
  address: string;
  phone: string;
  seatCount: number;
  naverMapUrl?: string;
};

type PageData = {
  content: Theater[];
  totalElements: number;
  totalPages: number;
  number: number;
};

export default function TheatersPage() {
  const router = useRouter();
  const [theaters, setTheaters] = useState<Theater[]>([]);
  const [savedSet, setSavedSet] = useState<Set<string>>(new Set());
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [keyword, setKeyword] = useState("");
  const [inputValue, setInputValue] = useState("");
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

  const fetchSaved = useCallback(async () => {
    try {
      const data = await apiFetch<PageData>(
        "/api/users/me/theaters?page=0&size=200",
      );
      setSavedSet(new Set(data.content.map((t) => t.theaCd)));
    } catch {
      // ignore
    }
  }, []);

  const fetchTheaters = useCallback(
    async (p = 0) => {
      setLoading(true);
      try {
        const params = new URLSearchParams({ page: String(p), size: "20" });
        if (keyword) params.set("keyword", keyword);
        const data = await apiFetch<PageData>(`/api/theaters?${params}`);
        setTheaters(data.content);
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
    if (authed) {
      fetchSaved();
      fetchTheaters(0);
    }
  }, [authed, fetchSaved, fetchTheaters]);

  const toggleSave = async (theaCd: string) => {
    const isSaved = savedSet.has(theaCd);
    try {
      if (isSaved) {
        await apiFetch(`/api/theaters/${theaCd}/save`, { method: "DELETE" });
        setSavedSet((prev) => {
          const next = new Set(prev);
          next.delete(theaCd);
          return next;
        });
      } else {
        await apiFetch(`/api/theaters/${theaCd}/save`, { method: "POST" });
        setSavedSet((prev) => new Set(prev).add(theaCd));
      }
    } catch {
      // toast emitted by apiFetch
    }
  };

  const handleSearch = () => {
    setKeyword(inputValue);
  };

  if (!authed) return null;

  return (
    <div className="mx-auto max-w-[480px] min-h-screen bg-[#F5F5F5]">
      <Toast />

      {/* Header */}
      <header className="sticky top-0 bg-white border-b border-gray-200 z-10 px-4 py-3">
        <div className="flex items-center justify-between mb-3">
          <h1 className="text-lg font-bold text-gray-900">독립영화관</h1>
          <span className="text-xs text-gray-400">
            {total.toLocaleString()}곳
          </span>
        </div>
        <div className="flex gap-2">
          <input
            className="flex-1 bg-gray-100 rounded-full px-4 py-2 text-sm placeholder-gray-400 focus:outline-none focus:bg-white focus:border focus:border-gray-300 transition-colors"
            placeholder="영화관 이름 검색"
            value={inputValue}
            onChange={(e) => setInputValue(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && handleSearch()}
          />
          {inputValue && (
            <button
              onClick={() => {
                setInputValue("");
                setKeyword("");
              }}
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

      <main className="px-4 py-4 pb-24">
        {loading ? (
          <div className="text-center text-gray-400 py-20 text-sm">
            불러오는 중...
          </div>
        ) : theaters.length === 0 ? (
          <div className="text-center text-gray-400 py-20 text-sm">
            영화관이 없습니다.
          </div>
        ) : (
          <div className="flex flex-col gap-3">
            {theaters.map((t) => {
              const saved = savedSet.has(t.theaCd);
              return (
                <div
                  key={t.theaCd}
                  className="bg-white rounded-2xl p-4 shadow-sm border border-gray-100"
                >
                  <div className="flex items-start justify-between gap-2">
                    <div className="flex-1 min-w-0">
                      <h2 className="font-semibold text-gray-900 truncate">
                        {t.theaName}
                      </h2>
                      {t.address && (
                        <p className="text-xs text-gray-500 mt-1 leading-relaxed">
                          {t.address}
                        </p>
                      )}
                      <div className="flex gap-3 mt-2">
                        {t.phone && (
                          <span className="text-xs text-gray-400">
                            📞 {t.phone}
                          </span>
                        )}
                        {t.seatCount > 0 && (
                          <span className="text-xs text-gray-400">
                            💺 {t.seatCount}석
                          </span>
                        )}
                      </div>
                    </div>
                    <div className="flex flex-col gap-2 shrink-0">
                      <button
                        onClick={() => toggleSave(t.theaCd)}
                        className={`text-xs px-3 py-1.5 rounded-full border font-medium transition-colors ${
                          saved
                            ? "bg-gray-900 text-white border-gray-900"
                            : "bg-white text-gray-600 border-gray-300 hover:border-gray-500"
                        }`}
                      >
                        {saved ? "저장됨" : "저장"}
                      </button>
                      {t.naverMapUrl ? (
                        <a
                          href={t.naverMapUrl}
                          target="_blank"
                          rel="noopener noreferrer"
                          className="text-xs px-3 py-1.5 rounded-full border border-gray-300 text-gray-600 text-center hover:border-gray-500 transition-colors"
                        >
                          지도
                        </a>
                      ) : (
                        <button
                          disabled
                          className="text-xs px-3 py-1.5 rounded-full border border-gray-200 text-gray-300 cursor-not-allowed"
                        >
                          지도
                        </button>
                      )}
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        )}

        {/* Pagination */}
        {totalPages > 1 && (
          <div className="flex items-center justify-center gap-2 mt-8">
            <button
              onClick={() => fetchTheaters(page - 1)}
              disabled={page === 0}
              className="px-3 py-2 rounded-lg bg-white text-sm text-gray-600 border border-gray-200 disabled:opacity-30"
            >
              ← 이전
            </button>
            <span className="text-sm text-gray-500 px-2">
              {page + 1} / {totalPages}
            </span>
            <button
              onClick={() => fetchTheaters(page + 1)}
              disabled={page >= totalPages - 1}
              className="px-3 py-2 rounded-lg bg-white text-sm text-gray-600 border border-gray-200 disabled:opacity-30"
            >
              다음 →
            </button>
          </div>
        )}
      </main>

      <BottomTabBar />
    </div>
  );
}
