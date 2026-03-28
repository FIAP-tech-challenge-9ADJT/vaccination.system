"use client";

import { useState } from "react";
import {
  Search,
  Loader2,
  UserSearch,
  ClipboardList,
  ChevronDown,
  ChevronUp,
} from "lucide-react";
import { toast } from "sonner";

import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Badge } from "@/components/ui/badge";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { useAuthStore } from "@/stores/auth-store";
import { api, type VaccinationRecordResponse, ApiError } from "@/lib/api";

interface PatientResult {
  id: number;
  name: string;
  cpf: string;
  birthDate: string;
  sex: string;
}

export default function PacientesPage() {
  const { token } = useAuthStore();
  const [query, setQuery] = useState("");
  const [searching, setSearching] = useState(false);
  const [patients, setPatients] = useState<PatientResult[]>([]);
  const [searched, setSearched] = useState(false);

  // Expanded patient vaccination history
  const [expandedId, setExpandedId] = useState<number | null>(null);
  const [records, setRecords] = useState<VaccinationRecordResponse[]>([]);
  const [loadingRecords, setLoadingRecords] = useState(false);

  async function handleSearch() {
    if (!token || !query.trim()) return;
    setSearching(true);
    setSearched(true);
    setExpandedId(null);
    setRecords([]);
    try {
      const results = await api.patient.search(token, query.trim());
      setPatients(results);
      if (results.length === 0) {
        toast.info("Nenhum paciente encontrado.");
      }
    } catch (error) {
      toast.error(
        error instanceof ApiError ? error.message : "Erro ao buscar pacientes"
      );
    } finally {
      setSearching(false);
    }
  }

  async function togglePatient(patientId: number) {
    if (expandedId === patientId) {
      setExpandedId(null);
      setRecords([]);
      return;
    }
    if (!token) return;
    setExpandedId(patientId);
    setLoadingRecords(true);
    try {
      const data = await api.patient.vaccinationCard(token, patientId);
      setRecords(data);
    } catch (error) {
      toast.error(
        error instanceof ApiError
          ? error.message
          : "Erro ao buscar historico vacinal"
      );
      setRecords([]);
    } finally {
      setLoadingRecords(false);
    }
  }

  function formatDate(d: string) {
    return new Date(d + "T00:00:00").toLocaleDateString("pt-BR");
  }

  function formatCpf(cpf: string) {
    if (!cpf || cpf.length !== 11) return cpf;
    return `${cpf.slice(0, 3)}.${cpf.slice(3, 6)}.${cpf.slice(6, 9)}-${cpf.slice(9)}`;
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gov-blue-dark">
          Consultar Pacientes
        </h1>
        <p className="text-muted-foreground">
          Busque pacientes por CPF ou nome para visualizar o historico vacinal
          completo
        </p>
      </div>

      <Card className="shadow-sm">
        <CardHeader>
          <CardTitle className="flex items-center gap-2 text-base">
            <UserSearch className="h-5 w-5 text-gov-blue" /> Buscar Paciente
          </CardTitle>
          <CardDescription>
            Digite o CPF (somente numeros) ou nome do paciente
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="flex gap-4">
            <div className="flex-1 space-y-2">
              <Label>CPF ou Nome</Label>
              <Input
                placeholder="Ex: 12345678901 ou Maria Silva"
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                onKeyDown={(e) => e.key === "Enter" && handleSearch()}
              />
            </div>
            <div className="flex items-end">
              <Button
                className="bg-gov-blue hover:bg-gov-blue/90"
                onClick={handleSearch}
                disabled={searching || !query.trim()}
              >
                {searching ? (
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                ) : (
                  <Search className="mr-2 h-4 w-4" />
                )}
                Buscar
              </Button>
            </div>
          </div>
        </CardContent>
      </Card>

      {searched && patients.length === 0 && !searching && (
        <Card className="shadow-sm">
          <CardContent className="py-12 text-center text-muted-foreground">
            Nenhum paciente encontrado para a busca realizada.
          </CardContent>
        </Card>
      )}

      {patients.length > 0 && (
        <Card className="shadow-sm">
          <CardHeader>
            <CardTitle className="text-base">
              {patients.length} paciente(s) encontrado(s)
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-3">
            {patients.map((p) => (
              <div key={p.id} className="rounded-lg border">
                <button
                  type="button"
                  className="flex w-full items-center justify-between p-4 text-left hover:bg-muted/50 transition-colors"
                  onClick={() => togglePatient(p.id)}
                >
                  <div className="flex items-center gap-4">
                    <div className="flex h-10 w-10 items-center justify-center rounded-full bg-gov-blue/10">
                      <span className="text-sm font-bold text-gov-blue">
                        {p.name.charAt(0).toUpperCase()}
                      </span>
                    </div>
                    <div>
                      <p className="font-medium">{p.name}</p>
                      <div className="flex gap-3 text-sm text-muted-foreground">
                        {p.cpf && <span>CPF: {formatCpf(p.cpf)}</span>}
                        {p.birthDate && (
                          <span>Nasc.: {formatDate(p.birthDate)}</span>
                        )}
                        {p.sex && <span>{p.sex}</span>}
                      </div>
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    <Badge variant="outline" className="text-gov-blue">
                      <ClipboardList className="mr-1 h-3 w-3" />
                      Historico
                    </Badge>
                    {expandedId === p.id ? (
                      <ChevronUp className="h-4 w-4 text-muted-foreground" />
                    ) : (
                      <ChevronDown className="h-4 w-4 text-muted-foreground" />
                    )}
                  </div>
                </button>

                {expandedId === p.id && (
                  <div className="border-t bg-muted/30 p-4">
                    {loadingRecords ? (
                      <div className="flex items-center justify-center py-8">
                        <Loader2 className="h-6 w-6 animate-spin text-gov-blue" />
                      </div>
                    ) : records.length === 0 ? (
                      <p className="py-4 text-center text-sm text-muted-foreground">
                        Nenhum registro de vacinacao encontrado para este
                        paciente.
                      </p>
                    ) : (
                      <div className="rounded-md border bg-background">
                        <Table>
                          <TableHeader>
                            <TableRow>
                              <TableHead>Vacina</TableHead>
                              <TableHead>Dose</TableHead>
                              <TableHead>Data</TableHead>
                              <TableHead>Lote</TableHead>
                              <TableHead>Profissional</TableHead>
                              <TableHead>Unidade</TableHead>
                            </TableRow>
                          </TableHeader>
                          <TableBody>
                            {records.map((r) => (
                              <TableRow key={r.id}>
                                <TableCell className="font-medium">
                                  {r.vaccineName}
                                </TableCell>
                                <TableCell>
                                  <Badge variant="secondary">
                                    {r.doseNumber}a dose
                                  </Badge>
                                </TableCell>
                                <TableCell>
                                  {formatDate(r.applicationDate)}
                                </TableCell>
                                <TableCell>{r.lotNumber || "--"}</TableCell>
                                <TableCell>{r.professionalName}</TableCell>
                                <TableCell>{r.healthUnitName || "--"}</TableCell>
                              </TableRow>
                            ))}
                          </TableBody>
                        </Table>
                      </div>
                    )}
                  </div>
                )}
              </div>
            ))}
          </CardContent>
        </Card>
      )}
    </div>
  );
}
