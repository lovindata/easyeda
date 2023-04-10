import { useCallback, useState } from "react";
import {
  MAX_TOASTS,
  Toast,
  ToasterContext,
  ToastLevelEnum,
} from "./ToasterCtx";

/**
 * Toaster provider.
 */
export default function ToasterPvd({
  children,
}: {
  children: React.ReactNode;
}) {
  // Initalize
  const nbMax = 3;
  const timeout = 10000;
  const [toaster, setToaster] = useState<{ toasts: Toast[]; usableId: number }>(
    {
      toasts: [],
      usableId: Number.MIN_SAFE_INTEGER,
    }
  );

  // Adder
  const addToast = useCallback(
    (toast: { level: ToastLevelEnum; header: string; message?: string }) => {
      setToaster(({ toasts, usableId }) => {
        // Add
        const toastToAdd = { id: usableId, ...toast };
        const outToasts =
          toasts.length === MAX_TOASTS
            ? [...toasts.slice(1), toastToAdd]
            : [...toasts, toastToAdd];

        // Timeout remove
        setTimeout(
          () =>
            setToaster(({ toasts, usableId: currUsableId }) => {
              const outToasts = toasts.filter((x) => x.id !== usableId);
              const outUsableId =
                outToasts.length === 0 ? Number.MIN_SAFE_INTEGER : currUsableId;
              return { toasts: outToasts, usableId: outUsableId };
            }),
          timeout
        );

        // Return
        const outUsableId = usableId + 1;
        return { toasts: outToasts, usableId: outUsableId };
      });
    },
    []
  );

  // Render
  return (
    <ToasterContext.Provider
      value={{ toasts: toaster.toasts, addToast: addToast }}
    >
      {children}
    </ToasterContext.Provider>
  );
}
