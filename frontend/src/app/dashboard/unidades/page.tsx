"use client";

import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import {
  Plus,
  Loader2,
  Trash2,
  Pencil,
  ChevronLeft,
  ChevronRight,
  Building2,
} from "lucide-react";
import { toast } from "sonner";

import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Badge } from "@/components/ui/badge";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle, AlertDialogTrigger } from "@/components/ui/alert-dialog";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { useAuthStore } from "@/stores/auth-store";
import { api, type HealthUnitResponse, ApiError } from "@/lib/api";

const unitSchema = z.object({
  name: z.string().min(1, "Nome é obrigatório").max(200),
  cnes: z.string().max(20).optional(),
  address: z.string().max(300).optional(),
  city: z.string().max(100).optional(),
  state: z.string().max(2).optional(),
  phone: z.string().max(20).optional(),
});

type UnitFormData = z.infer<typeof unitSchema>;

const PAGE_SIZE = 10;

export default function UnidadesPage() {
  const { token } = useAuthStore();
  const [units, setUnits] = useState<HealthUnitResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [createOpen, setCreateOpen] = useState(false);
  const [editUnit, setEditUnit] = useState<HealthUnitResponse | null>(null);
  const [saving, setSaving] = useState(false);
  const [deletingId, setDeletingId] = useState<number | null>(null);

  const createForm = useForm<UnitFormData>({ resolver: zodResolver(unitSchema) });
  const editForm = useForm<UnitFormData>({ resolver: zodResolver(unitSchema) });

  async function fetchUnits(p = 0) {
    if (!token) return;
    setLoading(true);
    try {
      const res = await api.healthUnits.list(token, p, PAGE_SIZE);
      setUnits(res.content);
      setPage(res.page);
      setTotalPages(res.totalPages);
      setTotalElements(res.totalElements);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => { fetchUnits(); }, [token]);

  useEffect(() => {
    if (editUnit) {
      editForm.reset({
        name: editUnit.name,
        cnes: editUnit.cnes || "",
        address: editUnit.address || "",
        city: editUnit.city || "",
        state: editUnit.state || "",
        phone: editUnit.phone || "",
      });
    }
  }, [editUnit, editForm]);

  async function onCreateUnit(data: UnitFormData) {
    if (!token) return;
    setSaving(true);
    try {
      await api.healthUnits.create(token, data);
      toast.success("Unidade cadastrada com sucesso!");
      setCreateOpen(false);
      createForm.reset();
      fetchUnits(page);
    } catch (error) {
      toast.error(error instanceof ApiError ? error.message : "Erro ao cadastrar unidade");
    } finally {
      setSaving(false);
    }
  }

  async function onEditUnit(data: UnitFormData) {
    if (!token || !editUnit) return;
    setSaving(true);
    try {
      await api.healthUnits.update(token, editUnit.id, { ...data, active: editUnit.active });
      toast.success("Unidade atualizada com sucesso!");
      setEditUnit(null);
      fetchUnits(page);
    } catch (error) {
      toast.error(error instanceof ApiError ? error.message : "Erro ao atualizar unidade");
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(id: number) {
    if (!token) return;
    setDeletingId(id);
    try {
      await api.healthUnits.delete(token, id);
      toast.success("Unidade removida com sucesso!");
      fetchUnits(page);
    } catch (error) {
      toast.error(error instanceof ApiError ? error.message : "Erro ao remover unidade");
    } finally {
      setDeletingId(null);
    }
  }

  function UnitForm({ form, onSubmit, submitLabel }: {
    form: ReturnType<typeof useForm<UnitFormData>>;
    onSubmit: (data: UnitFormData) => void;
    submitLabel: string;
  }) {
    return (
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
        <div className="grid grid-cols-2 gap-4">
          <div className="space-y-2">
            <Label>Nome</Label>
            <Input {...form.register("name")} />
            {form.formState.errors.name && <p className="text-sm text-destructive">{form.formState.errors.name.message}</p>}
          </div>
          <div className="space-y-2">
            <Label>CNES</Label>
            <Input {...form.register("cnes")} />
          </div>
        </div>
        <div className="space-y-2">
          <Label>Endereço</Label>
          <Input {...form.register("address")} />
        </div>
        <div className="grid grid-cols-3 gap-4">
          <div className="space-y-2">
            <Label>Cidade</Label>
            <Input {...form.register("city")} />
          </div>
          <div className="space-y-2">
            <Label>Estado (UF)</Label>
            <Input maxLength={2} {...form.register("state")} />
          </div>
          <div className="space-y-2">
            <Label>Telefone</Label>
            <Input {...form.register("phone")} />
          </div>
        </div>
        <div className="flex justify-end gap-3 pt-2">
          <Button type="submit" className="bg-gov-blue hover:bg-gov-blue/90" disabled={saving}>
            {saving && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
            {submitLabel}
          </Button>
        </div>
      </form>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gov-blue-dark">Unidades de Saúde</h1>
          <p className="text-muted-foreground">Gerencie as unidades de saúde cadastradas</p>
        </div>
        <Dialog open={createOpen} onOpenChange={setCreateOpen}>
          <DialogTrigger render={<Button className="bg-gov-green hover:bg-gov-green/90" />}>
            <Plus className="mr-2 h-4 w-4" /> Nova Unidade
          </DialogTrigger>
          <DialogContent className="max-w-2xl">
            <DialogHeader>
              <DialogTitle>Cadastrar Nova Unidade</DialogTitle>
              <DialogDescription>Preencha os dados da unidade de saúde.</DialogDescription>
            </DialogHeader>
            <UnitForm form={createForm} onSubmit={onCreateUnit} submitLabel="Cadastrar" />
          </DialogContent>
        </Dialog>
      </div>

      <Card className="shadow-sm">
        <CardHeader>
          <CardTitle className="flex items-center gap-2 text-base">
            <Building2 className="h-5 w-5 text-gov-blue" />
            Unidades Cadastradas
          </CardTitle>
          <CardDescription>{totalElements} unidade(s) no total</CardDescription>
        </CardHeader>
        <CardContent>
          {loading ? (
            <div className="flex items-center justify-center py-12">
              <Loader2 className="h-8 w-8 animate-spin text-gov-blue" />
            </div>
          ) : units.length === 0 ? (
            <div className="py-12 text-center text-muted-foreground">Nenhuma unidade cadastrada.</div>
          ) : (
            <>
              <div className="rounded-md border">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Nome</TableHead>
                      <TableHead>CNES</TableHead>
                      <TableHead>Cidade/UF</TableHead>
                      <TableHead>Telefone</TableHead>
                      <TableHead>Status</TableHead>
                      <TableHead className="w-24 text-right">Ações</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {units.map((u) => (
                      <TableRow key={u.id}>
                        <TableCell className="font-medium">{u.name}</TableCell>
                        <TableCell>{u.cnes || "—"}</TableCell>
                        <TableCell>{[u.city, u.state].filter(Boolean).join("/") || "—"}</TableCell>
                        <TableCell>{u.phone || "—"}</TableCell>
                        <TableCell>
                          <Badge className={u.active ? "bg-gov-green hover:bg-gov-green/90" : ""}>{u.active ? "Ativa" : "Inativa"}</Badge>
                        </TableCell>
                        <TableCell className="text-right">
                          <div className="flex justify-end gap-1">
                            <Button variant="ghost" size="icon" onClick={() => setEditUnit(u)}><Pencil className="h-4 w-4" /></Button>
                            <AlertDialog>
                              <AlertDialogTrigger render={<Button variant="ghost" size="icon" className="text-destructive hover:text-destructive" />}>
                                <Trash2 className="h-4 w-4" />
                              </AlertDialogTrigger>
                              <AlertDialogContent>
                                <AlertDialogHeader>
                                  <AlertDialogTitle>Confirmar exclusão</AlertDialogTitle>
                                  <AlertDialogDescription>
                                    Tem certeza que deseja excluir a unidade <strong>{u.name}</strong>?
                                  </AlertDialogDescription>
                                </AlertDialogHeader>
                                <AlertDialogFooter>
                                  <AlertDialogCancel>Cancelar</AlertDialogCancel>
                                  <AlertDialogAction className="bg-destructive hover:bg-destructive/90" onClick={() => handleDelete(u.id)} disabled={deletingId === u.id}>
                                    {deletingId === u.id && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                                    Excluir
                                  </AlertDialogAction>
                                </AlertDialogFooter>
                              </AlertDialogContent>
                            </AlertDialog>
                          </div>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </div>
              {totalPages > 1 && (
                <div className="flex items-center justify-between pt-4">
                  <p className="text-sm text-muted-foreground">Página {page + 1} de {totalPages}</p>
                  <div className="flex gap-2">
                    <Button variant="outline" size="sm" onClick={() => fetchUnits(page - 1)} disabled={page === 0}>
                      <ChevronLeft className="mr-1 h-4 w-4" /> Anterior
                    </Button>
                    <Button variant="outline" size="sm" onClick={() => fetchUnits(page + 1)} disabled={page >= totalPages - 1}>
                      Próxima <ChevronRight className="ml-1 h-4 w-4" />
                    </Button>
                  </div>
                </div>
              )}
            </>
          )}
        </CardContent>
      </Card>

      <Dialog open={!!editUnit} onOpenChange={() => setEditUnit(null)}>
        <DialogContent className="max-w-2xl">
          <DialogHeader>
            <DialogTitle>Editar Unidade</DialogTitle>
            <DialogDescription>Altere os dados da unidade <strong>{editUnit?.name}</strong>.</DialogDescription>
          </DialogHeader>
          <UnitForm form={editForm} onSubmit={onEditUnit} submitLabel="Salvar" />
        </DialogContent>
      </Dialog>
    </div>
  );
}
