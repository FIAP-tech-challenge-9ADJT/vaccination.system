"use client";

import { Syringe } from "lucide-react";

export function GovHeader() {
  return (
    <header className="bg-gov-blue-dark text-white">
      <div className="h-1 bg-gov-yellow" />
      <div className="flex items-center gap-3 px-6 py-3">
        <div className="flex items-center gap-2">
          <div className="flex h-8 w-8 items-center justify-center rounded-full bg-gov-green">
            <Syringe className="h-4 w-4 text-white" />
          </div>
          <div className="flex flex-col">
            <span className="text-xs font-medium leading-tight opacity-80">
              Sistema Nacional de
            </span>
            <span className="text-sm font-bold leading-tight">
              VACINAÇÃO
            </span>
          </div>
        </div>
      </div>
    </header>
  );
}
