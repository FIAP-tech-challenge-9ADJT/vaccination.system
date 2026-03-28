"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import {
  LayoutDashboard,
  Users,
  Syringe,
  LogOut,
  Building2,
  ClipboardList,
  Megaphone,
  CreditCard,
  Bell,
  Building,
  UserSearch,
} from "lucide-react";
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from "@/components/ui/sidebar";
import { useAuthStore } from "@/stores/auth-store";

function hasRole(user: { roles: { name: string }[] } | null, role: string): boolean {
  return user?.roles?.some((r) => r.name === role) ?? false;
}

export function AppSidebar() {
  const pathname = usePathname();
  const { user, logout } = useAuthStore();

  const isAdmin = hasRole(user, "ADMIN");
  const isPaciente = hasRole(user, "PACIENTE") || hasRole(user, "USER");
  const isEnfermeiro = hasRole(user, "ENFERMEIRO");
  const isMedico = hasRole(user, "MEDICO");
  const isEmpresa = hasRole(user, "EMPRESA");
  const isProfessional = isEnfermeiro || isMedico;

  const menuItems = [];

  // Dashboard - admin, enfermeiro, medico
  if (isAdmin || isProfessional) {
    menuItems.push({ title: "Dashboard", href: "/dashboard", icon: LayoutDashboard });
  }

  // Paciente menu
  if (isPaciente) {
    menuItems.push({ title: "Minha Carteira", href: "/dashboard/minha-carteira", icon: CreditCard });
    menuItems.push({ title: "Alertas", href: "/dashboard/alertas", icon: Bell });
  }

  // Professional menu
  if (isProfessional || isAdmin) {
    menuItems.push({ title: "Consultar Pacientes", href: "/dashboard/pacientes", icon: UserSearch });
    menuItems.push({ title: "Vacinações", href: "/dashboard/vacinacoes", icon: ClipboardList });
  }

  // Admin menu
  if (isAdmin) {
    menuItems.push({ title: "Usuários", href: "/dashboard/usuarios", icon: Users });
    menuItems.push({ title: "Vacinas", href: "/dashboard/vacinas", icon: Syringe });
    menuItems.push({ title: "Unidades de Saúde", href: "/dashboard/unidades", icon: Building2 });
    menuItems.push({ title: "Campanhas", href: "/dashboard/campanhas", icon: Megaphone });
  }

  // Company menu
  if (isEmpresa) {
    menuItems.push({ title: "Consultar Status", href: "/dashboard/consultar-status", icon: Building });
  }

  return (
    <Sidebar>
      <SidebarHeader className="border-b border-sidebar-border px-4 py-4">
        <div className="flex items-center gap-3">
          <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-gov-green">
            <Syringe className="h-5 w-5 text-white" />
          </div>
          <div className="flex flex-col">
            <span className="text-xs font-medium opacity-70">Sistema Nacional de</span>
            <span className="text-sm font-bold">VACINAÇÃO</span>
          </div>
        </div>
      </SidebarHeader>

      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupLabel>Menu Principal</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              {menuItems.map((item) => (
                <SidebarMenuItem key={item.href}>
                  <SidebarMenuButton
                    render={<Link href={item.href} />}
                    isActive={pathname === item.href}
                  >
                    <item.icon className="h-4 w-4" />
                    <span>{item.title}</span>
                  </SidebarMenuButton>
                </SidebarMenuItem>
              ))}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>

      <SidebarFooter className="border-t border-sidebar-border p-4">
        <div className="flex flex-col gap-3">
          {user && (
            <div className="flex flex-col text-sm">
              <span className="font-medium truncate">{user.name}</span>
              <span className="text-xs opacity-70 truncate">{user.email}</span>
              <div className="flex gap-1 mt-1">
                {user.roles.map((role) => (
                  <span key={role.id} className="text-[10px] px-1.5 py-0.5 rounded bg-sidebar-accent">
                    {role.name}
                  </span>
                ))}
              </div>
            </div>
          )}
          <SidebarMenu>
            <SidebarMenuItem>
              <SidebarMenuButton
                onClick={() => {
                  logout();
                  window.location.href = "/login";
                }}
              >
                <LogOut className="h-4 w-4" />
                <span>Sair</span>
              </SidebarMenuButton>
            </SidebarMenuItem>
          </SidebarMenu>
        </div>
      </SidebarFooter>
    </Sidebar>
  );
}
