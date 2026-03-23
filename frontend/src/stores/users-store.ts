import { create } from "zustand";
import { api, type UserResponse } from "@/lib/api";

interface UsersState {
  users: UserResponse[];
  loading: boolean;
  page: number;
  totalPages: number;
  totalElements: number;
  fetchUsers: (token: string, page?: number, size?: number) => Promise<void>;
  addUser: (user: UserResponse) => void;
  updateUser: (token: string, id: number, data: { name: string; email: string }) => Promise<UserResponse>;
  deleteUser: (token: string, id: number) => Promise<void>;
}

export const useUsersStore = create<UsersState>()((set, get) => ({
  users: [],
  loading: false,
  page: 0,
  totalPages: 0,
  totalElements: 0,

  fetchUsers: async (token, page = 0, size = 10) => {
    set({ loading: true });
    try {
      const res = await api.admin.listUsers(token, page, size);
      set({
        users: res.content,
        page: res.page,
        totalPages: res.totalPages,
        totalElements: res.totalElements,
      });
    } finally {
      set({ loading: false });
    }
  },

  addUser: (user) => {
    set((state) => ({
      users: [user, ...state.users],
      totalElements: state.totalElements + 1,
    }));
  },

  updateUser: async (token, id, data) => {
    const updated = await api.admin.updateUser(token, id, data);
    set((state) => ({
      users: state.users.map((u) => (u.id === id ? updated : u)),
    }));
    return updated;
  },

  deleteUser: async (token, id) => {
    await api.admin.deleteUser(token, id);
    set((state) => ({
      users: state.users.filter((u) => u.id !== id),
      totalElements: state.totalElements - 1,
    }));
  },
}));
