import { Transition } from "@headlessui/react";
import { useEffect, useState } from "react";
import { Error, Info, Success, Warning } from "../../assets";
import { TIMEOUT, Toast, ToastLevelEnum } from "./ToasterCtx";
import useToaster from "./ToasterHk";

/**
 * Toaster component.
 */
export default function ToasterCpt() {
  // Taosts state
  const { toasts } = useToaster();

  // Render
  return (
    <div className="fixed bottom-2 right-2 select-none space-y-2">
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
      bgColor = "bg-success";
      fillColor = "fill-success";
      break;
    case ToastLevelEnum.Info:
      bgColor = "bg-info";
      fillColor = "fill-info";
      break;
    case ToastLevelEnum.Warning:
      bgColor = "bg-warning";
      fillColor = "fill-warning";
      break;
    case ToastLevelEnum.Error:
      bgColor = "bg-error";
      fillColor = "fill-error";
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
  const [isShowing, setIsShowing] = useState(false);
  useEffect(() => {
    setIsShowing(true);
    setTimeout(() => setIsShowing(false), TIMEOUT * 1000 - duration);
  }, []);

  // Render
  return (
    <Transition
      show={isShowing}
      enterFrom="scale-x-0"
      enterTo="scale-x-100"
      leaveFrom="scale-x-100"
      leaveTo="scale-x-0"
      className={`duration-${duration} flex origin-right items-center space-x-3 rounded bg-transparent p-1.5 transition-all ease-in-out hover:bg-neutral`}
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
