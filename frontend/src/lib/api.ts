const API_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

interface RequestOptions extends RequestInit {
  token?: string;
}

async function request<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const { token, headers, ...rest } = options;

  const res = await fetch(`${API_URL}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...headers,
    },
    ...rest,
  });

  if (!res.ok) {
    const error = await res.json().catch(() => ({ message: "Erro desconhecido" }));
    throw new ApiError(res.status, error.message || "Erro na requisição");
  }

  if (res.status === 204) return undefined as T;
  return res.json();
}

export class ApiError extends Error {
  constructor(public status: number, message: string) {
    super(message);
  }
}

export const api = {
  auth: {
    login: (data: { login: string; password: string }) =>
      request<{ accessToken: string }>("/auth/login", {
        method: "POST",
        body: JSON.stringify(data),
      }),
    updateProfile: (token: string, data: { password?: string; name?: string; email?: string }) =>
      request<void>("/auth/update-profile", {
        method: "POST",
        token,
        body: JSON.stringify(data),
      }),
  },
  users: {
    create: (data: { name: string; email: string; login: string; password: string }) =>
      request<UserResponse>("/users", {
        method: "POST",
        body: JSON.stringify(data),
      }),
    me: (token: string) =>
      request<UserResponse>("/users/me", { token }),
    findById: (token: string, id: number) =>
      request<UserResponse>(`/users/${id}`, { token }),
    delete: (token: string, id: number) =>
      request<void>(`/users/${id}`, { method: "DELETE", token }),
  },
  admin: {
    listUsers: (token: string, page = 0, size = 10) =>
      request<PageResponse<UserResponse>>(`/admin/users?page=${page}&size=${size}`, { token }),
    findUser: (token: string, id: number) =>
      request<UserResponse>(`/admin/users/${id}`, { token }),
    updateUser: (token: string, id: number, data: { name: string; email: string }) =>
      request<UserResponse>(`/admin/users/${id}`, {
        method: "PUT",
        token,
        body: JSON.stringify(data),
      }),
    deleteUser: (token: string, id: number) =>
      request<void>(`/admin/users/${id}`, { method: "DELETE", token }),
  },
};

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface UserResponse {
  id: number;
  name: string;
  email: string;
  login: string;
  createdAt: string;
  updatedAt: string;
  roles: { id: number; name: string }[];
}
