"use client";

import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  Plus,
  Loader2,
  Trash2,
  Pencil,
  ChevronLeft,
  ChevronRight,
  Users,
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
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "@/components/ui/alert-dialog";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { useAuthStore } from "@/stores/auth-store";
import { useUsersStore } from "@/stores/users-store";
import { api, type UserResponse, ApiError } from "@/lib/api";
import {
  createUserSchema,
  editUserSchema,
  type CreateUserFormData,
  type EditUserFormData,
} from "@/lib/validations";

const PAGE_SIZE = 10;

export default function UsuariosPage() {
  const { token } = useAuthStore();
  const {
    users,
    loading,
    page,
    totalPages,
    totalElements,
    fetchUsers,
    addUser,
    updateUser,
    deleteUser,
  } = useUsersStore();

  const [createOpen, setCreateOpen] = useState(false);
  const [editUser, setEditUser] = useState<UserResponse | null>(null);
  const [creating, setCreating] = useState(false);
  const [editing, setEditing] = useState(false);
  const [deletingId, setDeletingId] = useState<number | null>(null);

  const createForm = useForm<CreateUserFormData>({
    resolver: zodResolver(createUserSchema),
  });

  const editForm = useForm<EditUserFormData>({
    resolver: zodResolver(editUserSchema),
  });

  useEffect(() => {
    if (token) fetchUsers(token, 0, PAGE_SIZE);
  }, [token, fetchUsers]);

  useEffect(() => {
    if (editUser) {
      editForm.reset({ name: editUser.name, email: editUser.email });
    }
  }, [editUser, editForm]);

  async function onCreateUser(data: CreateUserFormData) {
    setCreating(true);
    try {
      const user = await api.users.create(data);
      addUser(user);
      toast.success("Usuário criado com sucesso!");
      setCreateOpen(false);
      createForm.reset();
    } catch (error) {
      const message =
        error instanceof ApiError ? error.message : "Erro ao criar usuário";
      toast.error(message);
    } finally {
      setCreating(false);
    }
  }

  async function onEditUser(data: EditUserFormData) {
    if (!token || !editUser) return;
    setEditing(true);
    try {
      await updateUser(token, editUser.id, data);
      toast.success("Usuário atualizado com sucesso!");
      setEditUser(null);
    } catch (error) {
      const message =
        error instanceof ApiError ? error.message : "Erro ao atualizar usuário";
      toast.error(message);
    } finally {
      setEditing(false);
    }
  }

  async function handleDelete(id: number) {
    if (!token) return;
    setDeletingId(id);
    try {
      await deleteUser(token, id);
      toast.success("Usuário removido com sucesso!");
    } catch (error) {
      const message =
        error instanceof ApiError ? error.message : "Erro ao remover usuário";
      toast.error(message);
    } finally {
      setDeletingId(null);
    }
  }

  function goToPage(p: number) {
    if (token) fetchUsers(token, p, PAGE_SIZE);
  }

  function formatDate(dateStr: string) {
    return new Date(dateStr).toLocaleDateString("pt-BR", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gov-blue-dark">Usuários</h1>
          <p className="text-muted-foreground">
            Gerencie os usuários cadastrados no sistema
          </p>
        </div>

        {/* Create Dialog */}
        <Dialog open={createOpen} onOpenChange={setCreateOpen}>
          <DialogTrigger
            render={<Button className="bg-gov-green hover:bg-gov-green/90" />}
          >
            <Plus className="mr-2 h-4 w-4" />
            Novo Usuário
          </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Cadastrar Novo Usuário</DialogTitle>
              <DialogDescription>
                Preencha os dados para criar um novo usuário no sistema.
              </DialogDescription>
            </DialogHeader>
            <form
              onSubmit={createForm.handleSubmit(onCreateUser)}
              className="space-y-4"
            >
              <div className="space-y-2">
                <Label htmlFor="create-name">Nome Completo</Label>
                <Input id="create-name" {...createForm.register("name")} />
                {createForm.formState.errors.name && (
                  <p className="text-sm text-destructive">
                    {createForm.formState.errors.name.message}
                  </p>
                )}
              </div>
              <div className="space-y-2">
                <Label htmlFor="create-email">E-mail</Label>
                <Input
                  id="create-email"
                  type="email"
                  {...createForm.register("email")}
                />
                {createForm.formState.errors.email && (
                  <p className="text-sm text-destructive">
                    {createForm.formState.errors.email.message}
                  </p>
                )}
              </div>
              <div className="space-y-2">
                <Label htmlFor="create-login">Login</Label>
                <Input id="create-login" {...createForm.register("login")} />
                {createForm.formState.errors.login && (
                  <p className="text-sm text-destructive">
                    {createForm.formState.errors.login.message}
                  </p>
                )}
              </div>
              <div className="space-y-2">
                <Label htmlFor="create-password">Senha</Label>
                <Input
                  id="create-password"
                  type="password"
                  {...createForm.register("password")}
                />
                {createForm.formState.errors.password && (
                  <p className="text-sm text-destructive">
                    {createForm.formState.errors.password.message}
                  </p>
                )}
              </div>
              <div className="flex justify-end gap-3 pt-2">
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => setCreateOpen(false)}
                >
                  Cancelar
                </Button>
                <Button
                  type="submit"
                  className="bg-gov-blue hover:bg-gov-blue/90"
                  disabled={creating}
                >
                  {creating && (
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  )}
                  Cadastrar
                </Button>
              </div>
            </form>
          </DialogContent>
        </Dialog>
      </div>

      {/* Users Table */}
      <Card className="shadow-sm">
        <CardHeader>
          <CardTitle className="flex items-center gap-2 text-base">
            <Users className="h-5 w-5 text-gov-blue" />
            Usuários Cadastrados
          </CardTitle>
          <CardDescription>
            {totalElements} usuário(s) no total
          </CardDescription>
        </CardHeader>
        <CardContent>
          {loading ? (
            <div className="flex items-center justify-center py-12">
              <Loader2 className="h-8 w-8 animate-spin text-gov-blue" />
            </div>
          ) : users.length === 0 ? (
            <div className="py-12 text-center text-muted-foreground">
              Nenhum usuário cadastrado.
            </div>
          ) : (
            <>
              <div className="rounded-md border">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead className="w-16">ID</TableHead>
                      <TableHead>Nome</TableHead>
                      <TableHead>Login</TableHead>
                      <TableHead>E-mail</TableHead>
                      <TableHead>Roles</TableHead>
                      <TableHead>Criado em</TableHead>
                      <TableHead className="w-24 text-right">Ações</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {users.map((user) => (
                      <TableRow key={user.id}>
                        <TableCell className="font-mono text-sm">
                          {user.id}
                        </TableCell>
                        <TableCell className="font-medium">
                          {user.name}
                        </TableCell>
                        <TableCell>{user.login}</TableCell>
                        <TableCell>{user.email}</TableCell>
                        <TableCell>
                          <div className="flex gap-1">
                            {user.roles.map((role) => (
                              <Badge
                                key={role.id}
                                variant={
                                  role.name === "ADMIN"
                                    ? "default"
                                    : "secondary"
                                }
                                className={
                                  role.name === "ADMIN"
                                    ? "bg-gov-blue hover:bg-gov-blue/90"
                                    : ""
                                }
                              >
                                {role.name}
                              </Badge>
                            ))}
                          </div>
                        </TableCell>
                        <TableCell className="text-sm text-muted-foreground">
                          {formatDate(user.createdAt)}
                        </TableCell>
                        <TableCell className="text-right">
                          <div className="flex justify-end gap-1">
                            <Button
                              variant="ghost"
                              size="icon"
                              onClick={() => setEditUser(user)}
                            >
                              <Pencil className="h-4 w-4" />
                            </Button>
                            <AlertDialog>
                              <AlertDialogTrigger
                                render={
                                  <Button
                                    variant="ghost"
                                    size="icon"
                                    className="text-destructive hover:text-destructive"
                                  />
                                }
                              >
                                <Trash2 className="h-4 w-4" />
                              </AlertDialogTrigger>
                              <AlertDialogContent>
                                <AlertDialogHeader>
                                  <AlertDialogTitle>
                                    Confirmar exclusão
                                  </AlertDialogTitle>
                                  <AlertDialogDescription>
                                    Tem certeza que deseja excluir o usuário{" "}
                                    <strong>{user.name}</strong>? Esta ação não
                                    pode ser desfeita.
                                  </AlertDialogDescription>
                                </AlertDialogHeader>
                                <AlertDialogFooter>
                                  <AlertDialogCancel>Cancelar</AlertDialogCancel>
                                  <AlertDialogAction
                                    className="bg-destructive hover:bg-destructive/90"
                                    onClick={() => handleDelete(user.id)}
                                    disabled={deletingId === user.id}
                                  >
                                    {deletingId === user.id && (
                                      <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                                    )}
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

              {/* Pagination */}
              {totalPages > 1 && (
                <div className="flex items-center justify-between pt-4">
                  <p className="text-sm text-muted-foreground">
                    Página {page + 1} de {totalPages}
                  </p>
                  <div className="flex gap-2">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => goToPage(page - 1)}
                      disabled={page === 0}
                    >
                      <ChevronLeft className="mr-1 h-4 w-4" />
                      Anterior
                    </Button>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => goToPage(page + 1)}
                      disabled={page >= totalPages - 1}
                    >
                      Próxima
                      <ChevronRight className="ml-1 h-4 w-4" />
                    </Button>
                  </div>
                </div>
              )}
            </>
          )}
        </CardContent>
      </Card>

      {/* Edit User Dialog */}
      <Dialog open={!!editUser} onOpenChange={() => setEditUser(null)}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Editar Usuário</DialogTitle>
            <DialogDescription>
              Altere os dados do usuário{" "}
              <strong>{editUser?.login}</strong>.
            </DialogDescription>
          </DialogHeader>
          <form
            onSubmit={editForm.handleSubmit(onEditUser)}
            className="space-y-4"
          >
            <div className="space-y-2">
              <Label htmlFor="edit-name">Nome Completo</Label>
              <Input id="edit-name" {...editForm.register("name")} />
              {editForm.formState.errors.name && (
                <p className="text-sm text-destructive">
                  {editForm.formState.errors.name.message}
                </p>
              )}
            </div>
            <div className="space-y-2">
              <Label htmlFor="edit-email">E-mail</Label>
              <Input
                id="edit-email"
                type="email"
                {...editForm.register("email")}
              />
              {editForm.formState.errors.email && (
                <p className="text-sm text-destructive">
                  {editForm.formState.errors.email.message}
                </p>
              )}
            </div>
            <div className="flex justify-end gap-3 pt-2">
              <Button
                type="button"
                variant="outline"
                onClick={() => setEditUser(null)}
              >
                Cancelar
              </Button>
              <Button
                type="submit"
                className="bg-gov-blue hover:bg-gov-blue/90"
                disabled={editing}
              >
                {editing && (
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                )}
                Salvar
              </Button>
            </div>
          </form>
        </DialogContent>
      </Dialog>
    </div>
  );
}
