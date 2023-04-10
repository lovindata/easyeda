import { createContext } from "react";

/**
 * Maximum number of simultanous toasts.
 */
export const MAX_TOASTS = 3;

/**
 * Toast timeout in seconds.
 */
export const TIMEOUT = 10;

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
  addToast: (toast: {
    level: ToastLevelEnum;
    header: string;
    message?: string;
  }) => void;
}

/**
 * Toaster context.
 */
export const ToasterContext = createContext<IToasterContext | undefined>(
  undefined
);
