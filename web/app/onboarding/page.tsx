"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { apiFetch } from "../lib/api";

type Mode = "choose" | "auto-signup" | "manual-signup" | "login";

export default function OnboardingPage() {
  const router = useRouter();
  const [mode, setMode] = useState<Mode>("choose");
  const [loginId, setLoginId] = useState("");
  const [password, setPassword] = useState("");
  const [nickname, setNickname] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    if (typeof window !== "undefined" && localStorage.getItem("token")) {
      router.replace("/");
    }
  }, [router]);

  const fetchRandomNickname = async () => {
    setLoading(true);
    setError("");
    try {
      const data = await apiFetch<{ nickname: string }>(
        "/api/auth/nickname/random",
      );
      setNickname(data.nickname);
      setMode("auto-signup");
    } catch {
      setError("닉네임을 가져오지 못했습니다.");
    } finally {
      setLoading(false);
    }
  };

  const handleSignup = async () => {
    if (!loginId || !password || !nickname) {
      setError("모든 항목을 입력해주세요.");
      return;
    }
    setLoading(true);
    setError("");
    try {
      const data = await apiFetch<{ accessToken: string }>("/api/auth/signup", {
        method: "POST",
        body: JSON.stringify({ loginId, password, nickname }),
      });
      localStorage.setItem("token", data.accessToken);
      router.replace("/");
    } catch (e) {
      setError(e instanceof Error ? e.message : "회원가입에 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  const handleLogin = async () => {
    if (!loginId || !password) {
      setError("아이디와 비밀번호를 입력해주세요.");
      return;
    }
    setLoading(true);
    setError("");
    try {
      const data = await apiFetch<{ accessToken: string }>("/api/auth/login", {
        method: "POST",
        body: JSON.stringify({ loginId, password }),
      });
      localStorage.setItem("token", data.accessToken);
      router.replace("/");
    } catch (e) {
      setError(e instanceof Error ? e.message : "로그인에 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="mx-auto max-w-[480px] min-h-screen bg-[#F5F5F5] flex flex-col items-center justify-center px-6">
      {/* Title */}
      <div className="text-center mb-10">
        <div className="text-5xl mb-4">🎬</div>
        <h1 className="text-3xl font-bold text-gray-900 mb-2">
          독립영화 티켓북
        </h1>
        <p className="text-gray-500 text-sm">당신의 영화 여정을 기록하세요</p>
      </div>

      {mode === "choose" && (
        <div className="w-full flex flex-col gap-4">
          <button
            onClick={fetchRandomNickname}
            disabled={loading}
            className="w-full bg-white rounded-2xl px-6 py-5 flex items-center gap-4 shadow-sm border border-gray-100 hover:border-gray-300 transition-colors text-left disabled:opacity-50"
          >
            <span className="text-2xl">✨</span>
            <div>
              <p className="font-semibold text-gray-900">자동 닉네임 생성</p>
              <p className="text-xs text-gray-400 mt-0.5">
                랜덤 닉네임으로 빠르게 시작
              </p>
            </div>
          </button>
          <button
            onClick={() => {
              setMode("manual-signup");
              setNickname("");
            }}
            className="w-full bg-white rounded-2xl px-6 py-5 flex items-center gap-4 shadow-sm border border-gray-100 hover:border-gray-300 transition-colors text-left"
          >
            <span className="text-2xl">✏️</span>
            <div>
              <p className="font-semibold text-gray-900">닉네임 직접 입력</p>
              <p className="text-xs text-gray-400 mt-0.5">
                나만의 닉네임으로 시작
              </p>
            </div>
          </button>

          <div className="text-center mt-4">
            <button
              onClick={() => setMode("login")}
              className="text-sm text-gray-500 underline underline-offset-2"
            >
              이미 계정이 있어요 → 로그인
            </button>
          </div>
        </div>
      )}

      {(mode === "auto-signup" || mode === "manual-signup") && (
        <div className="w-full flex flex-col gap-3">
          <h2 className="font-semibold text-gray-800 mb-1">회원가입</h2>

          <input
            className="w-full bg-white border border-gray-200 rounded-xl px-4 py-3 text-sm focus:outline-none focus:border-gray-400"
            placeholder="아이디"
            value={loginId}
            onChange={(e) => setLoginId(e.target.value)}
          />
          <input
            type="password"
            className="w-full bg-white border border-gray-200 rounded-xl px-4 py-3 text-sm focus:outline-none focus:border-gray-400"
            placeholder="비밀번호"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <input
            className="w-full bg-white border border-gray-200 rounded-xl px-4 py-3 text-sm focus:outline-none focus:border-gray-400"
            placeholder="닉네임"
            value={nickname}
            onChange={(e) => setNickname(e.target.value)}
            readOnly={mode === "auto-signup"}
          />

          {error && <p className="text-red-500 text-xs">{error}</p>}

          <button
            onClick={handleSignup}
            disabled={loading}
            className="w-full bg-gray-900 text-white rounded-xl py-3 text-sm font-semibold disabled:opacity-50 mt-1"
          >
            {loading ? "처리 중..." : "가입하기"}
          </button>
          <button
            onClick={() => {
              setMode("choose");
              setLoginId("");
              setPassword("");
              setNickname("");
              setError("");
            }}
            className="text-sm text-gray-400 text-center"
          >
            ← 돌아가기
          </button>
        </div>
      )}

      {mode === "login" && (
        <div className="w-full flex flex-col gap-3">
          <h2 className="font-semibold text-gray-800 mb-1">로그인</h2>

          <input
            className="w-full bg-white border border-gray-200 rounded-xl px-4 py-3 text-sm focus:outline-none focus:border-gray-400"
            placeholder="아이디"
            value={loginId}
            onChange={(e) => setLoginId(e.target.value)}
          />
          <input
            type="password"
            className="w-full bg-white border border-gray-200 rounded-xl px-4 py-3 text-sm focus:outline-none focus:border-gray-400"
            placeholder="비밀번호"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && handleLogin()}
          />

          {error && <p className="text-red-500 text-xs">{error}</p>}

          <button
            onClick={handleLogin}
            disabled={loading}
            className="w-full bg-gray-900 text-white rounded-xl py-3 text-sm font-semibold disabled:opacity-50 mt-1"
          >
            {loading ? "처리 중..." : "로그인"}
          </button>
          <button
            onClick={() => {
              setMode("choose");
              setError("");
            }}
            className="text-sm text-gray-400 text-center"
          >
            ← 돌아가기
          </button>
        </div>
      )}
    </div>
  );
}
