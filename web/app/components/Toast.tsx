"use client";

import { useEffect, useState } from "react";

export default function Toast() {
  const [message, setMessage] = useState<string | null>(null);

  useEffect(() => {
    const handler = (e: Event) => {
      const msg = (e as CustomEvent<string>).detail;
      setMessage(msg);
      setTimeout(() => setMessage(null), 3000);
    };
    window.addEventListener("filmo-toast", handler);
    return () => window.removeEventListener("filmo-toast", handler);
  }, []);

  if (!message) return null;

  return (
    <div className="fixed top-4 left-1/2 -translate-x-1/2 z-[100] max-w-[440px] w-[calc(100%-2rem)]">
      <div className="bg-gray-800 text-white text-sm px-4 py-3 rounded-xl shadow-lg text-center">
        {message}
      </div>
    </div>
  );
}
