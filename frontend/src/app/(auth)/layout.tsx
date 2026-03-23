import { GovHeader } from "@/components/gov-header";
import { GovFooter } from "@/components/gov-footer";

export default function AuthLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="flex min-h-screen flex-col">
      <GovHeader />
      <main className="flex flex-1 items-center justify-center px-4 py-12">
        {children}
      </main>
      <GovFooter />
    </div>
  );
}
