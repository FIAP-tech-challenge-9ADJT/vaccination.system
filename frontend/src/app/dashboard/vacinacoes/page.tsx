"use client";

import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import {
  Plus,
  Loader2,
  Pencil,
  ChevronLeft,
  ChevronRight,
  ClipboardList,
  AlertTriangle,
  Info,
} from "lucide-react";
import { toast } from "sonner";

import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Label } from "@/components/ui/label";
import { Badge } from "@/components/ui/badge";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { useAuthStore } from "@/stores/auth-store";
import {
  api,
  type VaccinationRecordResponse,
  type VaccineResponse,
  type HealthUnitResponse,
  ApiError,
} from "@/lib/api";

const vaccinationSchema = z.object({
  patientCpf: z.string().min(11, "CPF deve ter 11 digitos").max(14, "CPF invalido"),
  vaccineId: z.coerce.number().min(1, "Selecione uma vacina"),
  healthUnitId: z.coerce.number().optional().nullable(),
  doseNumber: z.coerce.number().min(1, "Minimo 1"),
  lotNumber: z.string().optional(),
  applicationDate: z.string().min(1, "Data e obrigatoria"),
  notes: z.string().optional(),
});

const updateSchema = z.object({
  doseNumber: z.coerce.number().min(1),
  lotNumber: z.string().optional(),
  applicationDate: z.string().min(1, "Data e obrigatoria"),
  notes: z.string().optional(),
});

type VaccinationFormData = {
  patientCpf: string;
  vaccineId: number;
  healthUnitId?: number | null;
  doseNumber: number;
  lotNumber?: string;
  applicationDate: string;
  notes?: string;
};

type UpdateFormData = {
  doseNumber: number;
  lotNumber?: string;
  applicationDate: string;
  notes?: string;
};

const PAGE_SIZE = 10;

function hasRole(user: { roles: { name: string }[] } | null, role: string) {
  return user?.roles?.some((r) => r.name === role) ?? false;
}

export default function VacinacoesPage() {
  const { token, user } = useAuthStore();
  const isMedico = hasRole(user, "MEDICO");
  const [records, setRecords] = useState<VaccinationRecordResponse[]>([]);
  const [vaccines, setVaccines] = useState<VaccineResponse[]>([]);
  const [healthUnits, setHealthUnits] = useState<HealthUnitResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [createOpen, setCreateOpen] = useState(false);
  const [editRecord, setEditRecord] = useState<VaccinationRecordResponse | null>(null);
  const [saving, setSaving] = useState(false);
  const [previousDoses, setPreviousDoses] = useState<VaccinationRecordResponse[]>([]);
  const [loadingDoses, setLoadingDoses] = useState(false);
  const [resolvedPatient, setResolvedPatient] = useState<{ id: number; name: string } | null>(null);
  const [cpfError, setCpfError] = useState<string | null>(null);
  const [searchingCpf, setSearchingCpf] = useState(false);

  const createForm = useForm<VaccinationFormData>({
    resolver: zodResolver(vaccinationSchema) as never,
    defaultValues: { doseNumber: 1 },
  });

  const editForm = useForm<UpdateFormData>({
    resolver: zodResolver(updateSchema) as never,
  });

  async function fetchRecords(p = 0) {
    if (!token) return;
    setLoading(true);
    try {
      const res = await api.vaccinations.list(token, p, PAGE_SIZE);
      setRecords(res.content);
      setPage(res.page);
      setTotalPages(res.totalPages);
      setTotalElements(res.totalElements);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    if (!token) return;
    fetchRecords();
    api.vaccines.listActive(token).then(setVaccines).catch(() => {});
    api.healthUnits.listActive(token).then(setHealthUnits).catch(() => {});
  }, [token]);

  // Watch CPF to resolve patient
  const watchedCpf = createForm.watch("patientCpf");
  const watchedVaccineId = createForm.watch("vaccineId");

  useEffect(() => {
    const digits = (watchedCpf || "").replace(/\D/g, "");
    if (!token || digits.length < 11) {
      setResolvedPatient(null);
      setCpfError(null);
      setPreviousDoses([]);
      return;
    }
    let cancelled = false;
    setSearchingCpf(true);
    setCpfError(null);
    api.patient.search(token, digits)
      .then((results) => {
        if (cancelled) return;
        if (results.length > 0) {
          setResolvedPatient({ id: results[0].id, name: results[0].name });
          setCpfError(null);
        } else {
          setResolvedPatient(null);
          setCpfError("Nenhum paciente encontrado com este CPF");
        }
      })
      .catch(() => {
        if (!cancelled) {
          setResolvedPatient(null);
          setCpfError("Erro ao buscar paciente");
        }
      })
      .finally(() => { if (!cancelled) setSearchingCpf(false); });
    return () => { cancelled = true; };
  }, [token, watchedCpf]);

  // Fetch previous doses when patient is resolved and vaccine is selected
  useEffect(() => {
    if (!token || !resolvedPatient || !watchedVaccineId) {
      setPreviousDoses([]);
      return;
    }
    let cancelled = false;
    setLoadingDoses(true);
    api.vaccinations
      .findByPatientAndVaccine(token, resolvedPatient.id, Number(watchedVaccineId))
      .then((data) => {
        if (!cancelled) {
          setPreviousDoses(data);
          if (data.length > 0) {
            const maxDose = Math.max(...data.map((d) => d.doseNumber));
            createForm.setValue("doseNumber", maxDose + 1);
          } else {
            createForm.setValue("doseNumber", 1);
          }
        }
      })
      .catch(() => { if (!cancelled) setPreviousDoses([]); })
      .finally(() => { if (!cancelled) setLoadingDoses(false); });
    return () => { cancelled = true; };
  }, [token, resolvedPatient, watchedVaccineId]);

  // Compute interval warning
  const selectedVaccine = vaccines.find((v) => v.id === Number(watchedVaccineId));
  const intervalWarning = (() => {
    if (!selectedVaccine || previousDoses.length === 0) return null;
    const watchedDate = createForm.watch("applicationDate");
    const lastDose = previousDoses.reduce((a, b) =>
      a.doseNumber > b.doseNumber ? a : b
    );

    const warnings: { type: "interval" | "exceeded"; message: string }[] = [];

    // Check if dose exceeds required
    const nextDose = lastDose.doseNumber + 1;
    if (nextDose > selectedVaccine.requiredDoses) {
      warnings.push({
        type: "exceeded",
        message: `Esta vacina requer apenas ${selectedVaccine.requiredDoses} dose(s). O paciente ja completou o esquema vacinal.`,
      });
    }

    // Check interval
    if (selectedVaccine.doseIntervalDays && watchedDate) {
      const lastDate = new Date(lastDose.applicationDate + "T00:00:00");
      const newDate = new Date(watchedDate + "T00:00:00");
      const diffDays = Math.floor(
        (newDate.getTime() - lastDate.getTime()) / (1000 * 60 * 60 * 24)
      );
      if (diffDays < selectedVaccine.doseIntervalDays) {
        warnings.push({
          type: "interval",
          message: `Intervalo minimo entre doses: ${selectedVaccine.doseIntervalDays} dias. Ultima dose em ${new Date(lastDose.applicationDate + "T00:00:00").toLocaleDateString("pt-BR")}. Intervalo atual: ${diffDays} dia(s).`,
        });
      }
    }

    return warnings.length > 0 ? warnings : null;
  })();

  useEffect(() => {
    if (editRecord) {
      editForm.reset({
        doseNumber: editRecord.doseNumber,
        lotNumber: editRecord.lotNumber || "",
        applicationDate: editRecord.applicationDate,
        notes: editRecord.notes || "",
      });
    }
  }, [editRecord, editForm]);

  async function onCreateVaccination(data: VaccinationFormData) {
    if (!token || !resolvedPatient) {
      toast.error("Paciente nao encontrado. Verifique o CPF informado.");
      return;
    }
    setSaving(true);
    try {
      await api.vaccinations.create(token, {
        patientId: resolvedPatient.id,
        vaccineId: data.vaccineId,
        healthUnitId: data.healthUnitId || null,
        doseNumber: data.doseNumber,
        lotNumber: data.lotNumber,
        applicationDate: data.applicationDate,
        notes: data.notes,
      });
      toast.success("Vacinação registrada com sucesso!");
      setCreateOpen(false);
      createForm.reset({ doseNumber: 1 });
      setPreviousDoses([]);
      setResolvedPatient(null);
      setCpfError(null);
      fetchRecords(page);
    } catch (error) {
      toast.error(error instanceof ApiError ? error.message : "Erro ao registrar vacinação");
    } finally {
      setSaving(false);
    }
  }

  async function onUpdateVaccination(data: UpdateFormData) {
    if (!token || !editRecord) return;
    setSaving(true);
    try {
      await api.vaccinations.update(token, editRecord.id, data);
      toast.success("Registro atualizado com sucesso!");
      setEditRecord(null);
      fetchRecords(page);
    } catch (error) {
      toast.error(error instanceof ApiError ? error.message : "Erro ao atualizar registro");
    } finally {
      setSaving(false);
    }
  }

  function formatDate(d: string) {
    return new Date(d + "T00:00:00").toLocaleDateString("pt-BR");
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gov-blue-dark">Vacinações</h1>
          <p className="text-muted-foreground">Registros de vacinação do sistema</p>
        </div>
        <Dialog open={createOpen} onOpenChange={setCreateOpen}>
          <DialogTrigger render={<Button className="bg-gov-green hover:bg-gov-green/90" />}>
            <Plus className="mr-2 h-4 w-4" /> Registrar Vacinação
          </DialogTrigger>
          <DialogContent className="max-w-[90vw] w-full">
            <DialogHeader>
              <DialogTitle>Registrar Vacinação</DialogTitle>
              <DialogDescription>Preencha os dados do registro de vacinação.</DialogDescription>
            </DialogHeader>
            <form onSubmit={createForm.handleSubmit(onCreateVaccination)} className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label>CPF do Paciente</Label>
                  <Input
                    placeholder="Ex: 12345678901"
                    maxLength={14}
                    {...createForm.register("patientCpf")}
                  />
                  {searchingCpf && (
                    <p className="text-sm text-muted-foreground flex items-center gap-1">
                      <Loader2 className="h-3 w-3 animate-spin" /> Buscando paciente...
                    </p>
                  )}
                  {resolvedPatient && (
                    <p className="text-sm text-gov-green font-medium">
                      Paciente: {resolvedPatient.name}
                    </p>
                  )}
                  {cpfError && (
                    <p className="text-sm text-destructive">{cpfError}</p>
                  )}
                  {createForm.formState.errors.patientCpf && !cpfError && (
                    <p className="text-sm text-destructive">{createForm.formState.errors.patientCpf.message}</p>
                  )}
                </div>
                <div className="space-y-2">
                  <Label>Vacina</Label>
                  <select
                    {...createForm.register("vaccineId")}
                    className="flex h-9 w-full rounded-md border border-input bg-transparent px-3 py-1 text-sm"
                  >
                    <option value="">Selecione...</option>
                    {vaccines.map((v) => (
                      <option key={v.id} value={v.id}>{v.name}</option>
                    ))}
                  </select>
                  {createForm.formState.errors.vaccineId && (
                    <p className="text-sm text-destructive">{createForm.formState.errors.vaccineId.message}</p>
                  )}
                </div>
              </div>
              {/* Previous doses info */}
              {resolvedPatient && watchedVaccineId && (
                <div className="rounded-md border p-3 bg-muted/30">
                  <p className="text-sm font-medium flex items-center gap-1.5 mb-2">
                    <Info className="h-4 w-4 text-gov-blue" />
                    Doses anteriores desta vacina
                  </p>
                  {loadingDoses ? (
                    <div className="flex items-center gap-2 text-sm text-muted-foreground">
                      <Loader2 className="h-3 w-3 animate-spin" /> Carregando...
                    </div>
                  ) : previousDoses.length === 0 ? (
                    <p className="text-sm text-muted-foreground">Nenhuma dose anterior registrada para este paciente.</p>
                  ) : (
                    <div className="space-y-1.5">
                      {previousDoses
                        .sort((a, b) => a.doseNumber - b.doseNumber)
                        .map((d) => (
                          <div key={d.id} className="flex items-center gap-3 text-sm bg-background rounded px-2.5 py-1.5 border">
                            <Badge variant="secondary" className="text-xs">{d.doseNumber}a dose</Badge>
                            <span>{formatDate(d.applicationDate)}</span>
                            <span className="text-muted-foreground">Lote: {d.lotNumber || "--"}</span>
                            <span className="text-muted-foreground">Prof: {d.professionalName}</span>
                            {d.healthUnitName && <span className="text-muted-foreground">{d.healthUnitName}</span>}
                          </div>
                        ))}
                    </div>
                  )}
                </div>
              )}

              <div className="grid grid-cols-3 gap-4">
                <div className="space-y-2">
                  <Label>Dose N°</Label>
                  <Input type="number" min={1} {...createForm.register("doseNumber")} />
                </div>
                <div className="space-y-2">
                  <Label>Lote</Label>
                  <Input {...createForm.register("lotNumber")} />
                </div>
                <div className="space-y-2">
                  <Label>Data de Aplicação</Label>
                  <Input type="date" {...createForm.register("applicationDate")} />
                  {createForm.formState.errors.applicationDate && (
                    <p className="text-sm text-destructive">{createForm.formState.errors.applicationDate.message}</p>
                  )}
                </div>
              </div>

              {/* Interval & dose warnings */}
              {intervalWarning && intervalWarning.map((w, i) => (
                <div
                  key={i}
                  className={`flex items-start gap-2 rounded-md border p-3 text-sm ${
                    w.type === "exceeded"
                      ? "border-red-200 bg-red-50 text-red-800"
                      : "border-amber-200 bg-amber-50 text-amber-800"
                  }`}
                >
                  <AlertTriangle className="h-4 w-4 mt-0.5 shrink-0" />
                  <span>{w.message}</span>
                </div>
              ))}
              <div className="space-y-2">
                <Label>Unidade de Saúde</Label>
                <select
                  {...createForm.register("healthUnitId")}
                  className="flex h-9 w-full rounded-md border border-input bg-transparent px-3 py-1 text-sm"
                >
                  <option value="">Nenhuma</option>
                  {healthUnits.map((u) => (
                    <option key={u.id} value={u.id}>{u.name}</option>
                  ))}
                </select>
              </div>
              <div className="space-y-2">
                <Label>Observações</Label>
                <Textarea rows={3} placeholder="Observações sobre a aplicação..." {...createForm.register("notes")} />
              </div>
              <div className="flex justify-end gap-3 pt-2">
                <Button type="button" variant="outline" onClick={() => setCreateOpen(false)}>Cancelar</Button>
                <Button type="submit" className="bg-gov-blue hover:bg-gov-blue/90" disabled={saving}>
                  {saving && <Loader2 className="mr-2 h-4 w-4 animate-spin" />} Registrar
                </Button>
              </div>
            </form>
          </DialogContent>
        </Dialog>
      </div>

      <Card className="shadow-sm">
        <CardHeader>
          <CardTitle className="flex items-center gap-2 text-base">
            <ClipboardList className="h-5 w-5 text-gov-blue" /> Registros de Vacinação
          </CardTitle>
          <CardDescription>{totalElements} registro(s) no total</CardDescription>
        </CardHeader>
        <CardContent>
          {loading ? (
            <div className="flex items-center justify-center py-12">
              <Loader2 className="h-8 w-8 animate-spin text-gov-blue" />
            </div>
          ) : records.length === 0 ? (
            <div className="py-12 text-center text-muted-foreground">Nenhum registro de vacinação.</div>
          ) : (
            <>
              <div className="rounded-md border">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Paciente</TableHead>
                      <TableHead>Vacina</TableHead>
                      <TableHead>Dose</TableHead>
                      <TableHead>Lote</TableHead>
                      <TableHead>Data</TableHead>
                      <TableHead>Profissional</TableHead>
                      <TableHead>Unidade</TableHead>
                      {isMedico && <TableHead className="w-16">Ações</TableHead>}
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {records.map((r) => (
                      <TableRow key={r.id}>
                        <TableCell className="font-medium">{r.patientName}</TableCell>
                        <TableCell>{r.vaccineName}</TableCell>
                        <TableCell>
                          <Badge variant="secondary">{r.doseNumber}ª dose</Badge>
                        </TableCell>
                        <TableCell>{r.lotNumber || "—"}</TableCell>
                        <TableCell>{formatDate(r.applicationDate)}</TableCell>
                        <TableCell>{r.professionalName}</TableCell>
                        <TableCell>{r.healthUnitName || "—"}</TableCell>
                        {isMedico && (
                          <TableCell>
                            <Button variant="ghost" size="icon" onClick={() => setEditRecord(r)}>
                              <Pencil className="h-4 w-4" />
                            </Button>
                          </TableCell>
                        )}
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </div>
              {totalPages > 1 && (
                <div className="flex items-center justify-between pt-4">
                  <p className="text-sm text-muted-foreground">Página {page + 1} de {totalPages}</p>
                  <div className="flex gap-2">
                    <Button variant="outline" size="sm" onClick={() => fetchRecords(page - 1)} disabled={page === 0}>
                      <ChevronLeft className="mr-1 h-4 w-4" /> Anterior
                    </Button>
                    <Button variant="outline" size="sm" onClick={() => fetchRecords(page + 1)} disabled={page >= totalPages - 1}>
                      Próxima <ChevronRight className="ml-1 h-4 w-4" />
                    </Button>
                  </div>
                </div>
              )}
            </>
          )}
        </CardContent>
      </Card>

      {/* Edit Dialog - only for MEDICO */}
      <Dialog open={!!editRecord} onOpenChange={() => setEditRecord(null)}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Editar Registro</DialogTitle>
            <DialogDescription>Apenas médicos podem editar registros de vacinação.</DialogDescription>
          </DialogHeader>
          <form onSubmit={editForm.handleSubmit(onUpdateVaccination)} className="space-y-4">
            <div className="grid grid-cols-3 gap-4">
              <div className="space-y-2">
                <Label>Dose N°</Label>
                <Input type="number" min={1} {...editForm.register("doseNumber")} />
              </div>
              <div className="space-y-2">
                <Label>Lote</Label>
                <Input {...editForm.register("lotNumber")} />
              </div>
              <div className="space-y-2">
                <Label>Data</Label>
                <Input type="date" {...editForm.register("applicationDate")} />
              </div>
            </div>
            <div className="space-y-2">
              <Label>Observações</Label>
              <Textarea rows={3} placeholder="Observações sobre a aplicação..." {...editForm.register("notes")} />
            </div>
            <div className="flex justify-end gap-3 pt-2">
              <Button type="button" variant="outline" onClick={() => setEditRecord(null)}>Cancelar</Button>
              <Button type="submit" className="bg-gov-blue hover:bg-gov-blue/90" disabled={saving}>
                {saving && <Loader2 className="mr-2 h-4 w-4 animate-spin" />} Salvar
              </Button>
            </div>
          </form>
        </DialogContent>
      </Dialog>
    </div>
  );
}
