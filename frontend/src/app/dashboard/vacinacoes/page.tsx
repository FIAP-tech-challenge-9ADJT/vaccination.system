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
} from "lucide-react";
import { toast } from "sonner";

import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
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
  patientId: z.coerce.number().min(1, "ID do paciente é obrigatório"),
  vaccineId: z.coerce.number().min(1, "Selecione uma vacina"),
  healthUnitId: z.coerce.number().optional().nullable(),
  doseNumber: z.coerce.number().min(1, "Mínimo 1"),
  lotNumber: z.string().optional(),
  applicationDate: z.string().min(1, "Data é obrigatória"),
  notes: z.string().optional(),
});

const updateSchema = z.object({
  doseNumber: z.coerce.number().min(1),
  lotNumber: z.string().optional(),
  applicationDate: z.string().min(1, "Data é obrigatória"),
  notes: z.string().optional(),
});

type VaccinationFormData = {
  patientId: number;
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
    if (!token) return;
    setSaving(true);
    try {
      await api.vaccinations.create(token, {
        ...data,
        healthUnitId: data.healthUnitId || null,
      });
      toast.success("Vacinação registrada com sucesso!");
      setCreateOpen(false);
      createForm.reset({ doseNumber: 1 });
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
          <DialogContent className="max-w-2xl">
            <DialogHeader>
              <DialogTitle>Registrar Vacinação</DialogTitle>
              <DialogDescription>Preencha os dados do registro de vacinação.</DialogDescription>
            </DialogHeader>
            <form onSubmit={createForm.handleSubmit(onCreateVaccination)} className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label>ID do Paciente</Label>
                  <Input type="number" {...createForm.register("patientId")} />
                  {createForm.formState.errors.patientId && (
                    <p className="text-sm text-destructive">{createForm.formState.errors.patientId.message}</p>
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
                <Input {...createForm.register("notes")} />
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
              <Input {...editForm.register("notes")} />
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
