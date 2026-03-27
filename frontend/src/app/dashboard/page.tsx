"use client";

import { useEffect, useState } from "react";
import {
  Users,
  Syringe,
  ShieldCheck,
  Megaphone,
  Activity,
  Loader2,
} from "lucide-react";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { useAuthStore } from "@/stores/auth-store";
import {
  api,
  type DashboardStats,
  type VaccinationRecordResponse,
  type CampaignResponse,
} from "@/lib/api";

export default function DashboardPage() {
  const { token } = useAuthStore();
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [recentVaccinations, setRecentVaccinations] = useState<VaccinationRecordResponse[]>([]);
  const [campaigns, setCampaigns] = useState<CampaignResponse[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!token) return;
    setLoading(true);
    Promise.all([
      api.dashboard.stats(token),
      api.dashboard.recentVaccinations(token),
      api.dashboard.activeCampaigns(token),
    ])
      .then(([s, r, c]) => {
        setStats(s);
        setRecentVaccinations(r);
        setCampaigns(c);
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [token]);

  if (loading) {
    return (
      <div className="flex items-center justify-center py-20">
        <Loader2 className="h-8 w-8 animate-spin text-gov-blue" />
      </div>
    );
  }

  const statCards = [
    {
      title: "Pacientes Vacinados",
      value: stats?.totalPatients?.toLocaleString("pt-BR") ?? "0",
      icon: Users,
      color: "bg-gov-blue",
    },
    {
      title: "Vacinas Aplicadas",
      value: stats?.totalVaccinations?.toLocaleString("pt-BR") ?? "0",
      icon: Syringe,
      color: "bg-gov-green",
    },
    {
      title: "Vacinas Cadastradas",
      value: stats?.totalVaccines?.toLocaleString("pt-BR") ?? "0",
      icon: ShieldCheck,
      color: "bg-gov-blue-dark",
    },
    {
      title: "Campanhas Ativas",
      value: stats?.activeCampaigns?.toString() ?? "0",
      icon: Megaphone,
      color: "bg-[#C2850C]",
    },
  ];

  function formatDate(d: string) {
    return new Date(d + "T00:00:00").toLocaleDateString("pt-BR");
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gov-blue-dark">Dashboard</h1>
        <p className="text-muted-foreground">
          Visão geral do Sistema Nacional de Vacinação
        </p>
      </div>

      {/* Stats Cards */}
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        {statCards.map((stat) => (
          <Card key={stat.title} className="shadow-sm">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div className="space-y-1">
                  <p className="text-sm text-muted-foreground">{stat.title}</p>
                  <p className="text-2xl font-bold">{stat.value}</p>
                </div>
                <div className={`flex h-12 w-12 items-center justify-center rounded-lg ${stat.color}`}>
                  <stat.icon className="h-6 w-6 text-white" />
                </div>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      <div className="grid gap-6 lg:grid-cols-2">
        {/* Recent Vaccinations */}
        <Card className="shadow-sm">
          <CardHeader>
            <CardTitle className="flex items-center gap-2 text-lg">
              <Activity className="h-5 w-5 text-gov-blue" />
              Vacinações Recentes
            </CardTitle>
            <CardDescription>Últimas vacinações registradas no sistema</CardDescription>
          </CardHeader>
          <CardContent>
            {recentVaccinations.length === 0 ? (
              <div className="flex flex-col items-center justify-center py-8 text-center text-muted-foreground">
                <Syringe className="mb-3 h-10 w-10 opacity-30" />
                <p className="text-sm">Nenhuma vacinação registrada ainda.</p>
              </div>
            ) : (
              <div className="space-y-3">
                {recentVaccinations.map((item) => (
                  <div
                    key={item.id}
                    className="flex items-center justify-between rounded-lg border p-3"
                  >
                    <div className="space-y-0.5">
                      <p className="text-sm font-medium">{item.patientName}</p>
                      <p className="text-xs text-muted-foreground">
                        {item.vaccineName} — {item.doseNumber}ª dose
                      </p>
                    </div>
                    <div className="flex items-center gap-3">
                      <span className="text-xs text-muted-foreground">
                        {formatDate(item.applicationDate)}
                      </span>
                      <Badge className="bg-gov-green hover:bg-gov-green/90">
                        Aplicada
                      </Badge>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </CardContent>
        </Card>

        {/* Campaigns */}
        <Card className="shadow-sm">
          <CardHeader>
            <CardTitle className="flex items-center gap-2 text-lg">
              <ShieldCheck className="h-5 w-5 text-gov-blue" />
              Campanhas em Andamento
            </CardTitle>
            <CardDescription>Progresso das campanhas de vacinação ativas</CardDescription>
          </CardHeader>
          <CardContent>
            {campaigns.length === 0 ? (
              <div className="flex flex-col items-center justify-center py-8 text-center text-muted-foreground">
                <ShieldCheck className="mb-3 h-10 w-10 opacity-30" />
                <p className="text-sm">Nenhuma campanha ativa no momento.</p>
              </div>
            ) : (
              <div className="space-y-5">
                {campaigns.map((campaign) => (
                  <div key={campaign.id} className="space-y-2">
                    <div className="flex items-center justify-between">
                      <p className="text-sm font-medium">{campaign.name}</p>
                      <span className="text-sm font-bold text-gov-blue">
                        {campaign.progressPercentage}%
                      </span>
                    </div>
                    <div className="h-2.5 w-full rounded-full bg-muted">
                      <div
                        className="h-2.5 rounded-full bg-gov-blue transition-all"
                        style={{ width: `${campaign.progressPercentage}%` }}
                      />
                    </div>
                    <p className="text-xs text-muted-foreground">
                      Meta: {campaign.doseGoal.toLocaleString("pt-BR")} doses |{" "}
                      {campaign.appliedDoses.toLocaleString("pt-BR")} aplicadas
                    </p>
                  </div>
                ))}
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
