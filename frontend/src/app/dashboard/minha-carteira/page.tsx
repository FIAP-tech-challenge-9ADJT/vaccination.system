"use client";

import { useEffect, useState } from "react";
import { Loader2, CreditCard, Syringe, QrCode } from "lucide-react";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { useAuthStore } from "@/stores/auth-store";
import { api, type VaccinationRecordResponse, type PendingVaccine } from "@/lib/api";

export default function MinhaCarteiraPage() {
  const { token, user } = useAuthStore();
  const [records, setRecords] = useState<VaccinationRecordResponse[]>([]);
  const [pending, setPending] = useState<PendingVaccine[]>([]);
  const [loading, setLoading] = useState(true);
  const [qrUrl, setQrUrl] = useState<string | null>(null);

  useEffect(() => {
    if (!token) return;
    setLoading(true);
    Promise.all([
      api.patient.myVaccinationCard(token),
      api.patient.myPendingVaccines(token),
    ])
      .then(([cards, pend]) => {
        setRecords(cards);
        setPending(pend);
      })
      .finally(() => setLoading(false));
  }, [token]);

  useEffect(() => {
    if (user?.id) {
      const baseUrl = typeof window !== "undefined" ? window.location.origin : "";
      setQrUrl(`${baseUrl}/validar/${user.id}`);
    }
  }, [user]);

  function formatDate(d: string) {
    return new Date(d + "T00:00:00").toLocaleDateString("pt-BR");
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
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gov-blue-dark">Minha Carteira de Vacinação</h1>
          <p className="text-muted-foreground">
            {user?.name} — {records.length} vacina(s) aplicada(s), {pending.length} pendente(s)
          </p>
        </div>
        {qrUrl && (
          <div className="flex flex-col items-center gap-1">
            <div className="flex h-20 w-20 items-center justify-center rounded-lg border-2 border-gov-blue bg-white">
              <QrCode className="h-14 w-14 text-gov-blue" />
            </div>
            <span className="text-[10px] text-muted-foreground">QR Code da Carteira</span>
          </div>
        )}
      </div>

      {/* Pending vaccines */}
      {pending.length > 0 && (
        <Card className="border-amber-200 bg-amber-50 shadow-sm">
          <CardHeader>
            <CardTitle className="flex items-center gap-2 text-base text-amber-800">
              <Syringe className="h-5 w-5" /> Vacinas Pendentes
            </CardTitle>
            <CardDescription className="text-amber-700">
              Você tem {pending.length} vacina(s) que ainda precisam ser aplicadas
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-2">
              {pending.map((p) => (
                <div key={p.vaccineId} className="flex items-center justify-between rounded-lg border border-amber-200 bg-white p-3">
                  <div>
                    <p className="text-sm font-medium">{p.vaccineName}</p>
                    <p className="text-xs text-muted-foreground">
                      {p.dosesTaken}/{p.requiredDoses} dose(s) aplicada(s) — Próxima: {p.nextDose}ª dose
                    </p>
                  </div>
                  <Badge variant="secondary" className="bg-amber-100 text-amber-800">Pendente</Badge>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      )}

      {/* Vaccination history */}
      <Card className="shadow-sm">
        <CardHeader>
          <CardTitle className="flex items-center gap-2 text-base">
            <CreditCard className="h-5 w-5 text-gov-blue" /> Histórico de Vacinação
          </CardTitle>
        </CardHeader>
        <CardContent>
          {records.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-8 text-center text-muted-foreground">
              <Syringe className="mb-3 h-10 w-10 opacity-30" />
              <p className="text-sm">Nenhuma vacinação registrada ainda.</p>
            </div>
          ) : (
            <div className="space-y-3">
              {records.map((r) => (
                <div key={r.id} className="flex items-center justify-between rounded-lg border p-3">
                  <div className="space-y-0.5">
                    <p className="text-sm font-medium">{r.vaccineName}</p>
                    <p className="text-xs text-muted-foreground">
                      {r.doseNumber}ª dose — Lote: {r.lotNumber || "N/A"} — Prof: {r.professionalName}
                    </p>
                    {r.healthUnitName && (
                      <p className="text-xs text-muted-foreground">Unidade: {r.healthUnitName}</p>
                    )}
                  </div>
                  <div className="flex items-center gap-3">
                    <span className="text-xs text-muted-foreground">{formatDate(r.applicationDate)}</span>
                    <Badge className="bg-gov-green hover:bg-gov-green/90">Aplicada</Badge>
                  </div>
                </div>
              ))}
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
