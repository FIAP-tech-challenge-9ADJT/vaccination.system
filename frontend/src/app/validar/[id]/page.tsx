"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { Loader2, CheckCircle, XCircle, Syringe, ShieldCheck } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { api, type VaccinationStatus } from "@/lib/api";

export default function ValidarPage() {
  const params = useParams();
  const patientId = Number(params.id);
  const [status, setStatus] = useState<VaccinationStatus | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!patientId) return;
    api.patient
      .validateCard(patientId)
      .then(setStatus)
      .catch((err) => setError(err.message || "Paciente não encontrado"))
      .finally(() => setLoading(false));
  }, [patientId]);

  return (
    <div className="min-h-screen bg-gradient-to-b from-gov-blue-dark to-gov-blue flex items-center justify-center p-4">
      <Card className="w-full max-w-md shadow-xl">
        <CardContent className="p-8">
          <div className="flex flex-col items-center text-center">
            <div className="flex h-14 w-14 items-center justify-center rounded-full bg-gov-green mb-4">
              <Syringe className="h-7 w-7 text-white" />
            </div>
            <h1 className="text-lg font-bold text-gov-blue-dark mb-1">
              Sistema Nacional de Vacinação
            </h1>
            <p className="text-sm text-muted-foreground mb-6">
              Validação de Carteira de Vacinação
            </p>

            {loading ? (
              <Loader2 className="h-8 w-8 animate-spin text-gov-blue" />
            ) : error ? (
              <div className="flex flex-col items-center gap-2">
                <XCircle className="h-12 w-12 text-destructive" />
                <p className="text-sm text-destructive">{error}</p>
              </div>
            ) : status ? (
              <div className="w-full space-y-4">
                <div className="flex items-center justify-center gap-3">
                  {status.status === "EM_DIA" ? (
                    <CheckCircle className="h-10 w-10 text-gov-green" />
                  ) : (
                    <ShieldCheck className="h-10 w-10 text-amber-500" />
                  )}
                  <Badge
                    className={`text-base px-4 py-1 ${
                      status.status === "EM_DIA"
                        ? "bg-gov-green hover:bg-gov-green/90"
                        : "bg-amber-500 hover:bg-amber-500/90"
                    }`}
                  >
                    {status.status === "EM_DIA" ? "Em Dia" : "Pendente"}
                  </Badge>
                </div>

                <div className="rounded-lg border p-4 text-left space-y-2">
                  <div className="flex justify-between">
                    <span className="text-sm text-muted-foreground">Paciente</span>
                    <span className="text-sm font-medium">{status.patientName}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-sm text-muted-foreground">Vacinas Aplicadas</span>
                    <span className="text-sm font-medium">{status.totalVaccinesApplied}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-sm text-muted-foreground">Vacinas Pendentes</span>
                    <span className="text-sm font-medium">{status.pendingVaccines}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-sm text-muted-foreground">Validado em</span>
                    <span className="text-sm font-medium">
                      {new Date(status.validatedAt).toLocaleString("pt-BR")}
                    </span>
                  </div>
                </div>
              </div>
            ) : null}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
