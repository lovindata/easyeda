import { useState, useEffect, createContext, useCallback } from "react";
import { ToasterCpt } from "./ToasterCpt";

/**
 * Toaster level.
 */
export enum ToastLevelEnum {
  Success,
  Info,
  Warning,
  Error,
}

/**
 * Toast type.
 */
export interface Toast {
  level: ToastLevelEnum;
  header: string;
  message?: string;
}

/**
 * Toaster context type.
 */
export interface IToasterContext {
  toasts: Toast[];
  addToast: (toast: Toast) => void;
}

/**
 * Toaster context.
 */
export const ToasterContext = createContext<IToasterContext | undefined>(undefined);

/**
 * Toaster provider.
 */
export function ToasterProvider({ children }: { children: React.ReactNode }) {
  // States
  const [toasts, setToasts] = useState<Toast[]>([]);
  const addToast = useCallback((toast: Toast) => setToasts([...toasts, toast]), [setToasts]);

  // Effect on `toasts`
  useEffect(() => {
    if (toasts.length > 0) {
      const remover = setTimeout(() => setToasts(toasts.slice(1)), 3000);
      return () => clearTimeout(remover);
    }
  }, [toasts]);

  // Render
  return (
    <ToasterContext.Provider value={{ toasts: toasts, addToast: addToast }}>
      <ToasterCpt toasts={toasts} />
      {children}
    </ToasterContext.Provider>
  );
}
