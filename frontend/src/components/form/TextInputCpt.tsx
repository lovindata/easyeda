import { UseFormRegisterReturn } from "react-hook-form";

/**
 * Generic text input.
 */
export function TextInputCpt<A extends string>(props: {
  header: string;
  isRequired: boolean;
  extra?: JSX.Element;
  registerKey: UseFormRegisterReturn<A>;
}) {
  return (
    <div className="flex flex-col space-y-2">
      <label className="flex space-x-1 text-sm font-semibold">
        <p>{props.header}</p>
        {props.isRequired && <p className="text-red-500">*</p>}
      </label>
      <input {...props.registerKey} type="text" className="focus rounded border-none bg-slate-800 p-2 focus:ring-0" />
      {props.extra && <div className="flex">{props.extra}</div>}
    </div>
  );
}
