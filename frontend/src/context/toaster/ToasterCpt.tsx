import { useEffect, useState } from "react";
import { Success, Info, Error, Warning } from "../../assets";
import { Toast, ToastLevelEnum } from "./ToasterCtx";
import { useToaster } from "./ToasterHk";

/**
 * Toaster component.
 */
export function ToasterCpt() {
  // Taosts state
  const { toasts } = useToaster();

  // Testing toasts
  // const toasts = [
  //   {
  //     level: ToastLevelEnum.Success,
  //     header: "Yay! Everything worked!",
  //     message: "Congrats on the internet loading your request.",
  //   },
  //   {
  //     level: ToastLevelEnum.Info,
  //     header: "Did you know?",
  //     message:
  //       "Here is something that you might like to know. It is a really really long description just to say nothing about what you already know.",
  //   },
  //   {
  //     level: ToastLevelEnum.Warning,
  //     header: "Uh oh, something went wrong. Is it because of your internet connection?",
  //     message: "Sorry! There was a problem with your request.",
  //   },
  //   {
  //     level: ToastLevelEnum.Error,
  //     header: "Cannot process your request.",
  //   },
  // ];

  // Render
  return (
    <div className="absolute right-2 bottom-2 space-y-2">
      {toasts.map((toast, idx) => (
        <ToastCpt toast={toast} key={idx} />
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
  const VBarCpt: JSX.Element = <span className={`flex min-h-[2.5rem] w-1 self-stretch rounded ${bgColor}`} />;
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

  // Mounting effect
  const [isMounted, setIsMounted] = useState(false);
  useEffect(() => {
    setTimeout(() => {
      setIsMounted(true);
    }, 250);
  }, []);

  // Render
  return (
    <div
      className={
        "flex items-center space-x-3 rounded bg-transparent p-1.5 transition-all duration-300 ease-in-out hover:bg-slate-700" +
        ` origin-right ${isMounted ? "scale-x-100" : "scale-x-0"}`
      }
    >
      {VBarCpt}
      {IconCpt}
      <div className="w-64 pr-1.5 text-white">
        <h1 className="text-sm font-semibold">{props.toast.header}</h1>
        {props.toast.message && <p className="text-xs opacity-50">{props.toast.message}</p>}
      </div>
    </div>
  );
}
