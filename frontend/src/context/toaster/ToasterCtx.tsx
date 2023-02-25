import { useState, createContext } from "react";

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
  id: number;
  level: ToastLevelEnum;
  header: string;
  message?: string;
}

/**
 * Toaster context type.
 */
export interface IToasterContext {
  toasts: Toast[];
  addToast: (level: ToastLevelEnum, header: string, message?: string) => void;
  timeout: number;
}

/**
 * Toaster context.
 */
export const ToasterContext = createContext<IToasterContext | undefined>(undefined);

/**
 * Toaster provider.
 */
export function ToasterProvider({ children }: { children: React.ReactNode }) {
  // Initalize
  const nbMax = 3;
  const timeout = 10000;

  // States
  const [toasts, setToasts] = useState<Toast[]>([]);
  const [usableId, setUsableId] = useState(Number.MIN_SAFE_INTEGER);
  const addToast = (level: ToastLevelEnum, header: string, message?: string) => {
    const toastToAdd = { id: usableId, level: level, header: header, message: message };
    setToasts(toasts.length == nbMax ? [...toasts.slice(1), toastToAdd] : [...toasts, toastToAdd]);
    setTimeout(() => setToasts((currToasts) => currToasts.filter((x) => x.id != usableId)), timeout);
    setUsableId(usableId + 1);
  };

  // Render
  return (
    <ToasterContext.Provider value={{ toasts: toasts, addToast: addToast, timeout: timeout }}>
      {children}
    </ToasterContext.Provider>
  );
}
