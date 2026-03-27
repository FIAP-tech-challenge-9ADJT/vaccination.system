"use client";

import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Plus, Loader2, Trash2, Pencil, ChevronLeft, ChevronRight, Megaphone } from "lucide-react";
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
import { api, type CampaignResponse, type VaccineResponse, ApiError } from "@/lib/api";

const campaignSchema = z.object({
  name: z.string().min(1, "Nome é obrigatório").max(200),
  vaccineId: z.coerce.number().min(1, "Selecione uma vacina"),
  description: z.string().optional(),
  targetAudience: z.string().max(200).optional(),
  doseGoal: z.coerce.number().min(1, "Meta deve ser pelo menos 1"),
  startDate: z.string().min(1, "Data de início é obrigatória"),
  endDate: z.string().min(1, "Data de término é obrigatória"),
});

type CampaignFormData = {
  name: string;
  vaccineId: number;
  description?: string;
  targetAudience?: string;
  doseGoal: number;
  startDate: string;
  endDate: string;
};

const PAGE_SIZE = 10;

export default function CampanhasPage() {
  const { token } = useAuthStore();
  const [campaigns, setCampaigns] = useState<CampaignResponse[]>([]);
  const [vaccines, setVaccines] = useState<VaccineResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [createOpen, setCreateOpen] = useState(false);
  const [editCampaign, setEditCampaign] = useState<CampaignResponse | null>(null);
  const [saving, setSaving] = useState(false);
  const [deletingId, setDeletingId] = useState<number | null>(null);

  const createForm = useForm<CampaignFormData>({ resolver: zodResolver(campaignSchema) as never });
  const editForm = useForm<CampaignFormData>({ resolver: zodResolver(campaignSchema) as never });

  async function fetchCampaigns(p = 0) {
    if (!token) return;
    setLoading(true);
    try {
      const res = await api.campaigns.list(token, p, PAGE_SIZE);
      setCampaigns(res.content);
      setPage(res.page);
      setTotalPages(res.totalPages);
      setTotalElements(res.totalElements);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    if (!token) return;
    fetchCampaigns();
    api.vaccines.listActive(token).then(setVaccines).catch(() => {});
  }, [token]);

  useEffect(() => {
    if (editCampaign) {
      editForm.reset({
        name: editCampaign.name,
        vaccineId: editCampaign.vaccineId,
        description: editCampaign.description || "",
        targetAudience: editCampaign.targetAudience || "",
        doseGoal: editCampaign.doseGoal,
        startDate: editCampaign.startDate,
        endDate: editCampaign.endDate,
      });
    }
  }, [editCampaign, editForm]);

  async function onCreateCampaign(data: CampaignFormData) {
    if (!token) return;
    setSaving(true);
    try {
      await api.campaigns.create(token, data);
      toast.success("Campanha criada com sucesso!");
      setCreateOpen(false);
      createForm.reset();
      fetchCampaigns(page);
    } catch (error) {
      toast.error(error instanceof ApiError ? error.message : "Erro ao criar campanha");
    } finally {
      setSaving(false);
    }
  }

  async function onEditCampaign(data: CampaignFormData) {
    if (!token || !editCampaign) return;
    setSaving(true);
    try {
      await api.campaigns.update(token, editCampaign.id, { ...data, active: editCampaign.active });
      toast.success("Campanha atualizada com sucesso!");
      setEditCampaign(null);
      fetchCampaigns(page);
    } catch (error) {
      toast.error(error instanceof ApiError ? error.message : "Erro ao atualizar campanha");
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(id: number) {
    if (!token) return;
    setDeletingId(id);
    try {
      await api.campaigns.delete(token, id);
      toast.success("Campanha removida!");
      fetchCampaigns(page);
    } catch (error) {
      toast.error(error instanceof ApiError ? error.message : "Erro ao remover campanha");
    } finally {
      setDeletingId(null);
    }
  }

  function CampaignForm({ form, onSubmit, label }: {
    form: ReturnType<typeof useForm<CampaignFormData>>;
    onSubmit: (d: CampaignFormData) => void;
    label: string;
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
            <Label>Vacina</Label>
            <select {...form.register("vaccineId")} className="flex h-9 w-full rounded-md border border-input bg-transparent px-3 py-1 text-sm">
              <option value="">Selecione...</option>
              {vaccines.map((v) => <option key={v.id} value={v.id}>{v.name}</option>)}
            </select>
          </div>
        </div>
        <div className="space-y-2">
          <Label>Descrição</Label>
          <Input {...form.register("description")} />
        </div>
        <div className="grid grid-cols-3 gap-4">
          <div className="space-y-2">
            <Label>Público-alvo</Label>
            <Input {...form.register("targetAudience")} />
          </div>
          <div className="space-y-2">
            <Label>Meta de Doses</Label>
            <Input type="number" min={1} {...form.register("doseGoal")} />
          </div>
        </div>
        <div className="grid grid-cols-2 gap-4">
          <div className="space-y-2">
            <Label>Início</Label>
            <Input type="date" {...form.register("startDate")} />
          </div>
          <div className="space-y-2">
            <Label>Término</Label>
            <Input type="date" {...form.register("endDate")} />
          </div>
        </div>
        <div className="flex justify-end gap-3 pt-2">
          <Button type="submit" className="bg-gov-blue hover:bg-gov-blue/90" disabled={saving}>
            {saving && <Loader2 className="mr-2 h-4 w-4 animate-spin" />} {label}
          </Button>
        </div>
      </form>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gov-blue-dark">Campanhas de Vacinação</h1>
          <p className="text-muted-foreground">Gerencie as campanhas de vacinação</p>
        </div>
        <Dialog open={createOpen} onOpenChange={setCreateOpen}>
          <DialogTrigger render={<Button className="bg-gov-green hover:bg-gov-green/90" />}>
            <Plus className="mr-2 h-4 w-4" /> Nova Campanha
          </DialogTrigger>
          <DialogContent className="max-w-2xl">
            <DialogHeader>
              <DialogTitle>Criar Campanha</DialogTitle>
              <DialogDescription>Preencha os dados da campanha.</DialogDescription>
            </DialogHeader>
            <CampaignForm form={createForm} onSubmit={onCreateCampaign} label="Criar" />
          </DialogContent>
        </Dialog>
      </div>

      <Card className="shadow-sm">
        <CardHeader>
          <CardTitle className="flex items-center gap-2 text-base">
            <Megaphone className="h-5 w-5 text-gov-blue" /> Campanhas
          </CardTitle>
          <CardDescription>{totalElements} campanha(s) no total</CardDescription>
        </CardHeader>
        <CardContent>
          {loading ? (
            <div className="flex items-center justify-center py-12"><Loader2 className="h-8 w-8 animate-spin text-gov-blue" /></div>
          ) : campaigns.length === 0 ? (
            <div className="py-12 text-center text-muted-foreground">Nenhuma campanha cadastrada.</div>
          ) : (
            <>
              <div className="space-y-4">
                {campaigns.map((c) => (
                  <div key={c.id} className="rounded-lg border p-4 space-y-3">
                    <div className="flex items-center justify-between">
                      <div>
                        <h3 className="font-semibold">{c.name}</h3>
                        <p className="text-sm text-muted-foreground">Vacina: {c.vaccineName} | Público: {c.targetAudience || "Geral"}</p>
                      </div>
                      <div className="flex items-center gap-2">
                        <Badge className={c.active ? "bg-gov-green hover:bg-gov-green/90" : ""}>{c.active ? "Ativa" : "Inativa"}</Badge>
                        <Button variant="ghost" size="icon" onClick={() => setEditCampaign(c)}><Pencil className="h-4 w-4" /></Button>
                        <AlertDialog>
                          <AlertDialogTrigger render={<Button variant="ghost" size="icon" className="text-destructive" />}>
                            <Trash2 className="h-4 w-4" />
                          </AlertDialogTrigger>
                          <AlertDialogContent>
                            <AlertDialogHeader>
                              <AlertDialogTitle>Confirmar exclusão</AlertDialogTitle>
                              <AlertDialogDescription>Excluir campanha <strong>{c.name}</strong>?</AlertDialogDescription>
                            </AlertDialogHeader>
                            <AlertDialogFooter>
                              <AlertDialogCancel>Cancelar</AlertDialogCancel>
                              <AlertDialogAction className="bg-destructive hover:bg-destructive/90" onClick={() => handleDelete(c.id)} disabled={deletingId === c.id}>
                                Excluir
                              </AlertDialogAction>
                            </AlertDialogFooter>
                          </AlertDialogContent>
                        </AlertDialog>
                      </div>
                    </div>
                    <div className="space-y-1">
                      <div className="flex items-center justify-between text-sm">
                        <span>{c.appliedDoses.toLocaleString("pt-BR")} / {c.doseGoal.toLocaleString("pt-BR")} doses</span>
                        <span className="font-bold text-gov-blue">{c.progressPercentage}%</span>
                      </div>
                      <div className="h-2.5 w-full rounded-full bg-muted">
                        <div className="h-2.5 rounded-full bg-gov-blue transition-all" style={{ width: `${c.progressPercentage}%` }} />
                      </div>
                      <p className="text-xs text-muted-foreground">
                        {new Date(c.startDate + "T00:00:00").toLocaleDateString("pt-BR")} até {new Date(c.endDate + "T00:00:00").toLocaleDateString("pt-BR")}
                      </p>
                    </div>
                  </div>
                ))}
              </div>
              {totalPages > 1 && (
                <div className="flex items-center justify-between pt-4">
                  <p className="text-sm text-muted-foreground">Página {page + 1} de {totalPages}</p>
                  <div className="flex gap-2">
                    <Button variant="outline" size="sm" onClick={() => fetchCampaigns(page - 1)} disabled={page === 0}><ChevronLeft className="mr-1 h-4 w-4" /> Anterior</Button>
                    <Button variant="outline" size="sm" onClick={() => fetchCampaigns(page + 1)} disabled={page >= totalPages - 1}>Próxima <ChevronRight className="ml-1 h-4 w-4" /></Button>
                  </div>
                </div>
              )}
            </>
          )}
        </CardContent>
      </Card>

      <Dialog open={!!editCampaign} onOpenChange={() => setEditCampaign(null)}>
        <DialogContent className="max-w-2xl">
          <DialogHeader>
            <DialogTitle>Editar Campanha</DialogTitle>
            <DialogDescription>Altere os dados da campanha.</DialogDescription>
          </DialogHeader>
          <CampaignForm form={editForm} onSubmit={onEditCampaign} label="Salvar" />
        </DialogContent>
      </Dialog>
    </div>
  );
}
