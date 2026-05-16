"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { apiFetch } from "../lib/api";
import BottomTabBar from "../components/BottomTabBar";
import Toast from "../components/Toast";

type UserInfo = {
  id: number;
  loginId: string;
  nickname: string;
  intro: string;
  ticketCount: number;
  savedTicketCount: number;
  savedTheaterCount: number;
};

export default function MePage() {
  const router = useRouter();
  const [user, setUser] = useState<UserInfo | null>(null);
  const [editNickname, setEditNickname] = useState(false);
  const [editIntro, setEditIntro] = useState(false);
  const [nickname, setNickname] = useState("");
  const [intro, setIntro] = useState("");
  const [saving, setSaving] = useState(false);
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

  useEffect(() => {
    if (!authed) return;
    apiFetch<UserInfo>("/api/users/me")
      .then((data) => {
        setUser(data);
        setNickname(data.nickname ?? "");
        setIntro(data.intro ?? "");
      })
      .catch(() => {});
  }, [authed]);

  const saveField = async (field: "nickname" | "intro") => {
    if (!user) return;
    setSaving(true);
    try {
      const body = field === "nickname" ? { nickname } : { intro };
      const updated = await apiFetch<UserInfo>("/api/users/me", {
        method: "PATCH",
        body: JSON.stringify(body),
      });
      setUser(updated);
      setNickname(updated.nickname ?? "");
      setIntro(updated.intro ?? "");
      if (field === "nickname") setEditNickname(false);
      else setEditIntro(false);
    } catch {
      // toast emitted by apiFetch
    } finally {
      setSaving(false);
    }
  };

  const handleLogout = () => {
    localStorage.clear();
    router.replace("/onboarding");
  };

  if (!authed || !user) return null;

  return (
    <div className="mx-auto max-w-[480px] min-h-screen bg-[#F5F5F5]">
      <Toast />

      {/* Header */}
      <header className="sticky top-0 bg-white border-b border-gray-200 z-10 px-4 py-4">
        <h1 className="text-lg font-bold text-gray-900">마이페이지</h1>
      </header>

      <main className="px-4 py-4 pb-24 flex flex-col gap-4">
        {/* Profile Card */}
        <div className="bg-white rounded-2xl p-5 shadow-sm border border-gray-100">
          <div className="flex items-center gap-4 mb-4">
            <div className="w-14 h-14 rounded-full bg-gray-200 flex items-center justify-center text-2xl shrink-0">
              🎬
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-xs text-gray-400 mb-0.5">{user.loginId}</p>
              {editNickname ? (
                <div className="flex gap-2 items-center">
                  <input
                    className="flex-1 border border-gray-300 rounded-lg px-2 py-1 text-sm focus:outline-none focus:border-gray-500"
                    value={nickname}
                    onChange={(e) => setNickname(e.target.value)}
                    onKeyDown={(e) =>
                      e.key === "Enter" && saveField("nickname")
                    }
                    autoFocus
                  />
                  <button
                    onClick={() => saveField("nickname")}
                    disabled={saving}
                    className="text-xs text-white bg-gray-900 px-2.5 py-1 rounded-lg disabled:opacity-50"
                  >
                    저장
                  </button>
                  <button
                    onClick={() => {
                      setEditNickname(false);
                      setNickname(user.nickname ?? "");
                    }}
                    className="text-xs text-gray-400"
                  >
                    취소
                  </button>
                </div>
              ) : (
                <button
                  onClick={() => setEditNickname(true)}
                  className="text-left group flex items-center gap-1"
                >
                  <span className="font-semibold text-gray-900">
                    {user.nickname}
                  </span>
                  <span className="text-gray-300 text-xs group-hover:text-gray-500">
                    ✏️
                  </span>
                </button>
              )}
            </div>
          </div>

          {/* Intro */}
          <div>
            <p className="text-xs text-gray-400 mb-1">자기소개</p>
            {editIntro ? (
              <div className="flex flex-col gap-2">
                <textarea
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:border-gray-500 resize-none"
                  rows={3}
                  value={intro}
                  onChange={(e) => setIntro(e.target.value)}
                  autoFocus
                />
                <div className="flex gap-2">
                  <button
                    onClick={() => saveField("intro")}
                    disabled={saving}
                    className="text-xs text-white bg-gray-900 px-3 py-1.5 rounded-lg disabled:opacity-50"
                  >
                    저장
                  </button>
                  <button
                    onClick={() => {
                      setEditIntro(false);
                      setIntro(user.intro ?? "");
                    }}
                    className="text-xs text-gray-400"
                  >
                    취소
                  </button>
                </div>
              </div>
            ) : (
              <button
                onClick={() => setEditIntro(true)}
                className="text-left w-full group"
              >
                <p className="text-sm text-gray-600 group-hover:text-gray-800">
                  {user.intro || (
                    <span className="text-gray-300">
                      자기소개를 입력해보세요 ✏️
                    </span>
                  )}
                </p>
              </button>
            )}
          </div>
        </div>

        {/* Stats Card */}
        <div className="bg-white rounded-2xl p-5 shadow-sm border border-gray-100">
          <h2 className="text-sm font-semibold text-gray-700 mb-4">활동</h2>
          <div className="grid grid-cols-3 gap-2 text-center">
            <div>
              <p className="text-2xl font-bold text-gray-900">
                {user.ticketCount}
              </p>
              <p className="text-xs text-gray-400 mt-1">작성한 티켓</p>
            </div>
            <div>
              <p className="text-2xl font-bold text-gray-900">
                {user.savedTicketCount}
              </p>
              <p className="text-xs text-gray-400 mt-1">저장한 티켓</p>
            </div>
            <div>
              <p className="text-2xl font-bold text-gray-900">
                {user.savedTheaterCount}
              </p>
              <p className="text-xs text-gray-400 mt-1">저장한 영화관</p>
            </div>
          </div>
        </div>

        {/* Logout */}
        <button
          onClick={handleLogout}
          className="w-full bg-white rounded-2xl py-4 text-sm text-red-500 font-medium shadow-sm border border-gray-100 hover:bg-red-50 transition-colors"
        >
          로그아웃
        </button>
      </main>

      <BottomTabBar />
    </div>
  );
}
