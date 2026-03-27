"use client";

import { useState } from "react";
import { Search, Loader2, CheckCircle, XCircle, Building } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Badge } from "@/components/ui/badge";
import { toast } from "sonner";
import { useAuthStore } from "@/stores/auth-store";
import { api, type VaccinationStatus, ApiError } from "@/lib/api";

export default function ConsultarStatusPage() {
  const { token } = useAuthStore();
  const [patientId, setPatientId] = useState("");
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<VaccinationStatus | null>(null);

  async function handleSearch() {
    if (!token || !patientId) return;
    setLoading(true);
    setResult(null);
    try {
      const status = await api.company.checkStatus(token, Number(patientId));
      setResult(status);
    } catch (error) {
      toast.error(error instanceof ApiError ? error.message : "Erro ao consultar status");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gov-blue-dark">Consultar Status Vacinal</h1>
        <p className="text-muted-foreground">Consulte o status de vacinação de um paciente (com autorização)</p>
      </div>

      <Card className="shadow-sm">
        <CardHeader>
          <CardTitle className="flex items-center gap-2 text-base">
            <Building className="h-5 w-5 text-gov-blue" /> Buscar Paciente
          </CardTitle>
          <CardDescription>O paciente deve ter autorizado a consulta pela sua empresa</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="flex gap-4">
            <div className="flex-1 space-y-2">
              <Label>ID do Paciente</Label>
              <Input
                type="number"
                placeholder="Digite o ID do paciente"
                value={patientId}
                onChange={(e) => setPatientId(e.target.value)}
                onKeyDown={(e) => e.key === "Enter" && handleSearch()}
              />
            </div>
            <div className="flex items-end">
              <Button
                className="bg-gov-blue hover:bg-gov-blue/90"
                onClick={handleSearch}
                disabled={loading || !patientId}
              >
                {loading ? <Loader2 className="mr-2 h-4 w-4 animate-spin" /> : <Search className="mr-2 h-4 w-4" />}
                Consultar
              </Button>
            </div>
          </div>
        </CardContent>
      </Card>

      {result && (
        <Card className={`shadow-sm ${result.status === "EM_DIA" ? "border-green-200" : "border-amber-200"}`}>
          <CardContent className="p-6">
            <div className="flex items-center gap-4">
              {result.status === "EM_DIA" ? (
                <div className="flex h-16 w-16 items-center justify-center rounded-full bg-green-100">
                  <CheckCircle className="h-8 w-8 text-gov-green" />
                </div>
              ) : (
                <div className="flex h-16 w-16 items-center justify-center rounded-full bg-amber-100">
                  <XCircle className="h-8 w-8 text-amber-600" />
                </div>
              )}
              <div className="flex-1">
                <h3 className="text-lg font-semibold">{result.patientName}</h3>
                <div className="mt-1 flex items-center gap-3">
                  <Badge className={result.status === "EM_DIA" ? "bg-gov-green hover:bg-gov-green/90" : "bg-amber-500 hover:bg-amber-500/90"}>
                    {result.status === "EM_DIA" ? "Em Dia" : "Pendente"}
                  </Badge>
                  <span className="text-sm text-muted-foreground">
                    {result.totalVaccinesApplied} vacina(s) aplicada(s)
                  </span>
                  {result.pendingVaccines > 0 && (
                    <span className="text-sm text-amber-600">
                      {result.pendingVaccines} pendente(s)
                    </span>
                  )}
                </div>
                <p className="mt-2 text-xs text-muted-foreground">
                  Consultado em: {new Date(result.validatedAt).toLocaleString("pt-BR")}
                </p>
              </div>
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
