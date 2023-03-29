import { useEffect, useState } from "react";
import { Success, Info, Error, Warning } from "../../assets";
import { Toast, ToastLevelEnum } from "./ToasterCtx";
import { useToaster } from "./ToasterHk";
import { Transition } from "@headlessui/react";

/**
 * Toaster component.
 */
export function ToasterCpt() {
  // Taosts state
  const { toasts } = useToaster();

  // Render
  return (
    <div className="fixed right-2 bottom-2 select-none space-y-2">
      {toasts.map((toast, _) => (
        <ToastCpt toast={toast} key={toast.id} />
      ))}
    </div>
  );
}

/**
 * Toast component.
 */
function ToastCpt(props: { toast: Toast }) {
  // Generic color
  let bgColor: string;
  let fillColor: string;
  switch (props.toast.level) {
    case ToastLevelEnum.Success:
      bgColor = "bg-emerald-500";
      fillColor = "fill-emerald-500";
      break;
    case ToastLevelEnum.Info:
      bgColor = "bg-sky-500";
      fillColor = "fill-sky-500";
      break;
    case ToastLevelEnum.Warning:
      bgColor = "bg-amber-500";
      fillColor = "fill-amber-500";
      break;
    case ToastLevelEnum.Error:
      bgColor = "bg-rose-500";
      fillColor = "fill-rose-500";
      break;
  }

  // Build vertical bar & icon
  const VBarCpt = <span className={`flex min-h-[2.5rem] w-1 self-stretch rounded ${bgColor}`} />;
  const iconCSS = `w-5 ${fillColor}`;
  let IconCpt: JSX.Element;
  switch (props.toast.level) {
    case ToastLevelEnum.Success:
      IconCpt = <Success className={iconCSS} />;
      break;
    case ToastLevelEnum.Info:
      IconCpt = <Info className={iconCSS} />;
      break;
    case ToastLevelEnum.Warning:
      IconCpt = <Warning className={iconCSS} />;
      break;
    case ToastLevelEnum.Error:
      IconCpt = <Error className={iconCSS} />;
      break;
  }

  // Render transition
  const duration = 300;
  const { timeout } = useToaster();
  const [isShowing, setIsShowing] = useState(false);
  useEffect(() => {
    setIsShowing(true);
    setTimeout(() => setIsShowing(false), timeout - duration);
  }, []);

  // Render
  return (
    <Transition
      show={isShowing}
      enterFrom="scale-x-0"
      enterTo="scale-x-100"
      leaveFrom="scale-x-100"
      leaveTo="scale-x-0"
      className={`duration-${duration} flex origin-right items-center space-x-3 rounded bg-transparent p-1.5 transition-all ease-in-out hover:bg-slate-700`}
    >
      {VBarCpt}
      {IconCpt}
      <div className="w-64 pr-1.5">
        <h1 className="text-sm font-semibold">{props.toast.header}</h1>
        {props.toast.message && <p className="text-xs brightness-75">{props.toast.message}</p>}
      </div>
    </Transition>
  );
}
