"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/stores/auth-store";

export function AuthGuard({ children }: { children: React.ReactNode }) {
  const { isAuthenticated, fetchUser } = useAuthStore();
  const router = useRouter();
  const [checked, setChecked] = useState(false);

  useEffect(() => {
    if (!isAuthenticated) {
      router.replace("/login");
    } else {
      fetchUser().finally(() => setChecked(true));
    }
  }, [isAuthenticated, router, fetchUser]);

  if (!checked) {
    return (
      <div className="flex h-screen items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-gov-blue border-t-transparent" />
      </div>
    );
  }

  return <>{children}</>;
}
