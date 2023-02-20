import { Success, Info, Error, Warning } from "../../assets";
import { Toast, ToastLevelEnum } from "./ToasterCtx";

/**
 * Toaster component.
 */
export function ToasterCpt(props: { toasts: Toast[] }) {
  // Testing toasts
  const toasts = [
    {
      level: ToastLevelEnum.Success,
      header: "Yay! Everything worked!",
      message: "Congrats on the internet loading your request.",
    },
    {
      level: ToastLevelEnum.Info,
      header: "Did you know?",
      message: "Here is something that you might like to know.",
    },
    {
      level: ToastLevelEnum.Warning,
      header: "Uh oh, something went wrong.",
      message: "Sorry! There was a problem with your request.",
    },
    {
      level: ToastLevelEnum.Error,
      header: "Ooooh no!",
      message: "Cannot process your request.",
    },
  ];

  // Render
  return (
    <div className="m-auto flex flex-col space-y-4">
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
  // Build vertical bar & icon
  let VBarCpt: JSX.Element;
  let IconCpt: JSX.Element;
  switch (props.toast.level) {
    case ToastLevelEnum.Success:
      VBarCpt = <span className="rounded h-14 w-1 bg-emerald-500" />;
      IconCpt = <Success className="h-6 w-6 fill-emerald-500" />;
      break;
    case ToastLevelEnum.Info:
      VBarCpt = <span className="rounded h-14 w-1 bg-sky-500" />;
      IconCpt = <Info className="h-6 w-6 fill-sky-500" />;
      break;
    case ToastLevelEnum.Warning:
      VBarCpt = <span className="rounded h-14 w-1 bg-amber-500" />;
      IconCpt = <Warning className="h-6 w-6 fill-amber-500" />;
      break;
    case ToastLevelEnum.Error:
      VBarCpt = <span className="rounded h-14 w-1 bg-rose-500" />;
      IconCpt = <Error className="h-6 w-6 fill-rose-500" />;
      break;
  }

  // Render
  return (
    <div className=" bg-slate-700 rounded flex items-center space-x-4 p-2 opacity-90 hover:opacity-100 transition-all ease-linear">
      {VBarCpt}
      {IconCpt}
      <div className="text-white pr-2 flex flex-col">
        <h1 className="font-semibold">{props.toast.header}</h1>
        <p className="text-sm opacity-50">{props.toast.message}</p>
      </div>
    </div>
  );
}
