import { create } from "zustand";
import { persist } from "zustand/middleware";
import { api, type UserResponse } from "@/lib/api";

interface AuthState {
  token: string | null;
  user: UserResponse | null;
  isAuthenticated: boolean;
  login: (login: string, password: string) => Promise<void>;
  logout: () => void;
  fetchUser: () => Promise<void>;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      token: null,
      user: null,
      isAuthenticated: false,

      login: async (login: string, password: string) => {
        const { accessToken } = await api.auth.login({ login, password });
        set({ token: accessToken, isAuthenticated: true });
        const user = await api.users.me(accessToken);
        set({ user });
      },

      logout: () => {
        set({ token: null, user: null, isAuthenticated: false });
      },

      fetchUser: async () => {
        const { token } = get();
        if (!token) return;
        try {
          const user = await api.users.me(token);
          set({ user });
        } catch {
          set({ token: null, user: null, isAuthenticated: false });
        }
      },
    }),
    {
      name: "vaccination-auth",
      partialize: (state) => ({ token: state.token, isAuthenticated: state.isAuthenticated }),
    }
  )
);
