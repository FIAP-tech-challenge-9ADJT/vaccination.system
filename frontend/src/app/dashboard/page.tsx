"use client";

import {
  Users,
  Syringe,
  ShieldCheck,
  CalendarCheck,
  TrendingUp,
  Activity,
} from "lucide-react";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";

const stats = [
  {
    title: "Pacientes Cadastrados",
    value: "12.847",
    change: "+8,2%",
    icon: Users,
    color: "bg-gov-blue",
  },
  {
    title: "Vacinas Aplicadas",
    value: "34.521",
    change: "+12,5%",
    icon: Syringe,
    color: "bg-gov-green",
  },
  {
    title: "Cobertura Vacinal",
    value: "87,3%",
    change: "+3,1%",
    icon: ShieldCheck,
    color: "bg-gov-blue-dark",
  },
  {
    title: "Agendamentos Hoje",
    value: "248",
    change: "+15,8%",
    icon: CalendarCheck,
    color: "bg-[#C2850C]",
  },
];

const recentVaccinations = [
  { patient: "Maria Silva", vaccine: "COVID-19 - Bivalente", date: "23/03/2026", status: "Aplicada" },
  { patient: "João Santos", vaccine: "Influenza 2026", date: "23/03/2026", status: "Aplicada" },
  { patient: "Ana Oliveira", vaccine: "Hepatite B - 2ª dose", date: "23/03/2026", status: "Agendada" },
  { patient: "Pedro Costa", vaccine: "Tétano - Reforço", date: "22/03/2026", status: "Aplicada" },
  { patient: "Lucia Fernandes", vaccine: "Febre Amarela", date: "22/03/2026", status: "Aplicada" },
];

const campaigns = [
  { name: "Campanha Nacional de Vacinação contra Influenza", progress: 72, target: "50.000 doses" },
  { name: "Campanha de Multivacinação Infantil", progress: 58, target: "25.000 crianças" },
  { name: "COVID-19 - Dose de Reforço 2026", progress: 45, target: "80.000 doses" },
];

export default function DashboardPage() {
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
        {stats.map((stat) => (
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
              <div className="mt-2 flex items-center gap-1 text-sm">
                <TrendingUp className="h-3 w-3 text-gov-green" />
                <span className="font-medium text-gov-green">{stat.change}</span>
                <span className="text-muted-foreground">vs. mês anterior</span>
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
            <div className="space-y-3">
              {recentVaccinations.map((item, i) => (
                <div
                  key={i}
                  className="flex items-center justify-between rounded-lg border p-3"
                >
                  <div className="space-y-0.5">
                    <p className="text-sm font-medium">{item.patient}</p>
                    <p className="text-xs text-muted-foreground">{item.vaccine}</p>
                  </div>
                  <div className="flex items-center gap-3">
                    <span className="text-xs text-muted-foreground">{item.date}</span>
                    <Badge
                      variant={item.status === "Aplicada" ? "default" : "secondary"}
                      className={
                        item.status === "Aplicada"
                          ? "bg-gov-green hover:bg-gov-green/90"
                          : ""
                      }
                    >
                      {item.status}
                    </Badge>
                  </div>
                </div>
              ))}
            </div>
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
            <div className="space-y-5">
              {campaigns.map((campaign, i) => (
                <div key={i} className="space-y-2">
                  <div className="flex items-center justify-between">
                    <p className="text-sm font-medium">{campaign.name}</p>
                    <span className="text-sm font-bold text-gov-blue">{campaign.progress}%</span>
                  </div>
                  <div className="h-2.5 w-full rounded-full bg-muted">
                    <div
                      className="h-2.5 rounded-full bg-gov-blue transition-all"
                      style={{ width: `${campaign.progress}%` }}
                    />
                  </div>
                  <p className="text-xs text-muted-foreground">Meta: {campaign.target}</p>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
