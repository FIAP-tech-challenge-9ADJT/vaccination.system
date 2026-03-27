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

// ===== Types =====

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface RoleResponse {
  id: number;
  name: string;
}

export interface UserResponse {
  id: number;
  name: string;
  email: string;
  login: string;
  cpf: string | null;
  birthDate: string | null;
  sex: string | null;
  createdAt: string;
  updatedAt: string;
  roles: RoleResponse[];
}

export interface VaccineResponse {
  id: number;
  name: string;
  manufacturer: string | null;
  type: string;
  requiredDoses: number;
  doseIntervalDays: number | null;
  minAgeMonths: number | null;
  maxAgeMonths: number | null;
  contraindications: string | null;
  description: string | null;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface HealthUnitResponse {
  id: number;
  name: string;
  cnes: string | null;
  address: string | null;
  city: string | null;
  state: string | null;
  phone: string | null;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface VaccinationRecordResponse {
  id: number;
  patientId: number;
  patientName: string;
  vaccineId: number;
  vaccineName: string;
  professionalId: number;
  professionalName: string;
  healthUnitId: number | null;
  healthUnitName: string | null;
  doseNumber: number;
  lotNumber: string | null;
  applicationDate: string;
  notes: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface CampaignResponse {
  id: number;
  name: string;
  vaccineId: number;
  vaccineName: string;
  description: string | null;
  targetAudience: string | null;
  doseGoal: number;
  appliedDoses: number;
  progressPercentage: number;
  startDate: string;
  endDate: string;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface DashboardStats {
  totalPatients: number;
  totalVaccinations: number;
  totalVaccines: number;
  activeCampaigns: number;
}

export interface PendingVaccine {
  vaccineId: number;
  vaccineName: string;
  requiredDoses: number;
  dosesTaken: number;
  nextDose: number;
}

export interface VaccineAlert {
  vaccineId: number;
  vaccineName: string;
  type: "ATRASADA" | "PROXIMA" | "PENDENTE";
  message: string;
}

export interface VaccinationStatus {
  patientName: string;
  totalVaccinesApplied: number;
  pendingVaccines: number;
  status: "EM_DIA" | "PENDENTE";
  validatedAt: string;
}

// ===== API =====

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

  vaccines: {
    list: (token: string, page = 0, size = 10) =>
      request<PageResponse<VaccineResponse>>(`/vaccines?page=${page}&size=${size}`, { token }),
    listActive: (token: string) =>
      request<VaccineResponse[]>("/vaccines/active", { token }),
    findById: (token: string, id: number) =>
      request<VaccineResponse>(`/vaccines/${id}`, { token }),
    create: (token: string, data: Record<string, unknown>) =>
      request<VaccineResponse>("/vaccines", {
        method: "POST",
        token,
        body: JSON.stringify(data),
      }),
    update: (token: string, id: number, data: Record<string, unknown>) =>
      request<VaccineResponse>(`/vaccines/${id}`, {
        method: "PUT",
        token,
        body: JSON.stringify(data),
      }),
    delete: (token: string, id: number) =>
      request<void>(`/vaccines/${id}`, { method: "DELETE", token }),
  },

  healthUnits: {
    list: (token: string, page = 0, size = 10) =>
      request<PageResponse<HealthUnitResponse>>(`/health-units?page=${page}&size=${size}`, { token }),
    listActive: (token: string) =>
      request<HealthUnitResponse[]>("/health-units/active", { token }),
    findById: (token: string, id: number) =>
      request<HealthUnitResponse>(`/health-units/${id}`, { token }),
    create: (token: string, data: Record<string, unknown>) =>
      request<HealthUnitResponse>("/health-units", {
        method: "POST",
        token,
        body: JSON.stringify(data),
      }),
    update: (token: string, id: number, data: Record<string, unknown>) =>
      request<HealthUnitResponse>(`/health-units/${id}`, {
        method: "PUT",
        token,
        body: JSON.stringify(data),
      }),
    delete: (token: string, id: number) =>
      request<void>(`/health-units/${id}`, { method: "DELETE", token }),
  },

  vaccinations: {
    list: (token: string, page = 0, size = 10) =>
      request<PageResponse<VaccinationRecordResponse>>(`/vaccinations?page=${page}&size=${size}`, { token }),
    findById: (token: string, id: number) =>
      request<VaccinationRecordResponse>(`/vaccinations/${id}`, { token }),
    findByPatient: (token: string, patientId: number) =>
      request<VaccinationRecordResponse[]>(`/vaccinations/patient/${patientId}`, { token }),
    create: (token: string, data: Record<string, unknown>) =>
      request<VaccinationRecordResponse>("/vaccinations", {
        method: "POST",
        token,
        body: JSON.stringify(data),
      }),
    update: (token: string, id: number, data: Record<string, unknown>) =>
      request<VaccinationRecordResponse>(`/vaccinations/${id}`, {
        method: "PUT",
        token,
        body: JSON.stringify(data),
      }),
  },

  campaigns: {
    list: (token: string, page = 0, size = 10) =>
      request<PageResponse<CampaignResponse>>(`/campaigns?page=${page}&size=${size}`, { token }),
    listActive: (token: string) =>
      request<CampaignResponse[]>("/campaigns/active", { token }),
    findById: (token: string, id: number) =>
      request<CampaignResponse>(`/campaigns/${id}`, { token }),
    create: (token: string, data: Record<string, unknown>) =>
      request<CampaignResponse>("/campaigns", {
        method: "POST",
        token,
        body: JSON.stringify(data),
      }),
    update: (token: string, id: number, data: Record<string, unknown>) =>
      request<CampaignResponse>(`/campaigns/${id}`, {
        method: "PUT",
        token,
        body: JSON.stringify(data),
      }),
    delete: (token: string, id: number) =>
      request<void>(`/campaigns/${id}`, { method: "DELETE", token }),
  },

  patient: {
    myVaccinationCard: (token: string) =>
      request<VaccinationRecordResponse[]>("/patients/me/vaccination-card", { token }),
    myPendingVaccines: (token: string) =>
      request<PendingVaccine[]>("/patients/me/pending-vaccines", { token }),
    myAlerts: (token: string) =>
      request<VaccineAlert[]>("/patients/me/alerts", { token }),
    validateCard: (patientId: number) =>
      request<VaccinationStatus>(`/patients/${patientId}/vaccination-card/validate`),
    grantConsent: (token: string, companyId: number) =>
      request<{ message: string }>(`/patients/me/consents/${companyId}`, {
        method: "POST",
        token,
      }),
    revokeConsent: (token: string, companyId: number) =>
      request<{ message: string }>(`/patients/me/consents/${companyId}`, {
        method: "DELETE",
        token,
      }),
    myConsents: (token: string) =>
      request<Array<{ companyId: number; companyName: string; grantedAt: string }>>("/patients/me/consents", { token }),
  },

  company: {
    checkStatus: (token: string, patientId: number) =>
      request<VaccinationStatus>(`/company/patients/${patientId}/status`, { token }),
  },

  dashboard: {
    stats: (token: string) =>
      request<DashboardStats>("/dashboard/stats", { token }),
    recentVaccinations: (token: string) =>
      request<VaccinationRecordResponse[]>("/dashboard/recent-vaccinations", { token }),
    activeCampaigns: (token: string) =>
      request<CampaignResponse[]>("/dashboard/active-campaigns", { token }),
  },
};
