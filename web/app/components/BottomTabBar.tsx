"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

const tabs = [
  {
    href: "/",
    label: "홈",
    icon: (
      <svg
        width="22"
        height="22"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
      >
        <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
        <polyline points="9 22 9 12 15 12 15 22" />
      </svg>
    ),
    active: true,
  },
  {
    href: null,
    label: "기록하기",
    icon: (
      <svg
        width="22"
        height="22"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
      >
        <path d="M12 5v14M5 12h14" />
      </svg>
    ),
    active: false,
  },
  {
    href: "/theaters",
    label: "영화관",
    icon: (
      <svg
        width="22"
        height="22"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
      >
        <rect x="2" y="7" width="20" height="15" rx="2" />
        <path d="M17 2l-5 5-5-5" />
      </svg>
    ),
    active: true,
  },
  {
    href: null,
    label: "컬렉션",
    icon: (
      <svg
        width="22"
        height="22"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
      >
        <rect x="3" y="3" width="7" height="7" rx="1" />
        <rect x="14" y="3" width="7" height="7" rx="1" />
        <rect x="3" y="14" width="7" height="7" rx="1" />
        <rect x="14" y="14" width="7" height="7" rx="1" />
      </svg>
    ),
    active: false,
  },
  {
    href: "/me",
    label: "마이",
    icon: (
      <svg
        width="22"
        height="22"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
      >
        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
        <circle cx="12" cy="7" r="4" />
      </svg>
    ),
    active: true,
  },
];

export default function BottomTabBar() {
  const pathname = usePathname();

  return (
    <nav className="fixed bottom-0 left-1/2 -translate-x-1/2 w-full max-w-[480px] bg-white border-t border-gray-200 z-50">
      <div className="flex">
        {tabs.map((tab) => {
          const isCurrent =
            tab.href &&
            (tab.href === "/"
              ? pathname === "/"
              : pathname.startsWith(tab.href));

          if (!tab.active) {
            return (
              <button
                key={tab.label}
                disabled
                className="flex-1 flex flex-col items-center gap-1 py-2 opacity-35 cursor-not-allowed"
              >
                <span className="text-gray-400">{tab.icon}</span>
                <span className="text-[10px] text-gray-400">{tab.label}</span>
              </button>
            );
          }

          return (
            <Link
              key={tab.label}
              href={tab.href!}
              className={`flex-1 flex flex-col items-center gap-1 py-2 transition-colors ${
                isCurrent ? "text-gray-900" : "text-gray-400"
              }`}
            >
              {tab.icon}
              <span
                className={`text-[10px] ${isCurrent ? "font-semibold" : ""}`}
              >
                {tab.label}
              </span>
            </Link>
          );
        })}
      </div>
    </nav>
  );
}
