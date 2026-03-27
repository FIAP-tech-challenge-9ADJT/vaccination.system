"use client";

import { useEffect, useState } from "react";
import { Loader2, Bell, AlertTriangle, Clock, Info } from "lucide-react";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { useAuthStore } from "@/stores/auth-store";
import { api, type VaccineAlert } from "@/lib/api";

export default function AlertasPage() {
  const { token } = useAuthStore();
  const [alerts, setAlerts] = useState<VaccineAlert[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!token) return;
    api.patient
      .myAlerts(token)
      .then(setAlerts)
      .finally(() => setLoading(false));
  }, [token]);

  function getAlertStyle(type: string) {
    switch (type) {
      case "ATRASADA":
        return { icon: AlertTriangle, color: "bg-red-50 border-red-200", badge: "bg-gov-red hover:bg-gov-red/90", text: "text-red-800" };
      case "PROXIMA":
        return { icon: Clock, color: "bg-blue-50 border-blue-200", badge: "bg-gov-blue hover:bg-gov-blue/90", text: "text-blue-800" };
      default:
        return { icon: Info, color: "bg-amber-50 border-amber-200", badge: "bg-amber-500 hover:bg-amber-500/90", text: "text-amber-800" };
    }
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center py-20">
        <Loader2 className="h-8 w-8 animate-spin text-gov-blue" />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gov-blue-dark">Alertas de Vacinação</h1>
        <p className="text-muted-foreground">
          {alerts.length === 0 ? "Você está em dia com suas vacinas!" : `${alerts.length} alerta(s) encontrado(s)`}
        </p>
      </div>

      {alerts.length === 0 ? (
        <Card className="border-green-200 bg-green-50 shadow-sm">
          <CardContent className="flex items-center gap-4 p-6">
            <div className="flex h-12 w-12 items-center justify-center rounded-full bg-gov-green">
              <Bell className="h-6 w-6 text-white" />
            </div>
            <div>
              <p className="font-medium text-green-800">Tudo em dia!</p>
              <p className="text-sm text-green-700">Suas vacinas estão todas atualizadas.</p>
            </div>
          </CardContent>
        </Card>
      ) : (
        <div className="space-y-3">
          {alerts.map((alert, i) => {
            const style = getAlertStyle(alert.type);
            const Icon = style.icon;
            return (
              <Card key={i} className={`${style.color} shadow-sm`}>
                <CardContent className="flex items-center gap-4 p-4">
                  <Icon className={`h-5 w-5 ${style.text}`} />
                  <div className="flex-1">
                    <p className={`text-sm font-medium ${style.text}`}>{alert.vaccineName}</p>
                    <p className={`text-xs ${style.text} opacity-80`}>{alert.message}</p>
                  </div>
                  <Badge className={style.badge}>{alert.type}</Badge>
                </CardContent>
              </Card>
            );
          })}
        </div>
      )}
    </div>
  );
}
