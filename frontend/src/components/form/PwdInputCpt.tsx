import { UseFormRegisterReturn } from "react-hook-form";

/**
 * Generic password input.
 */
export function PwdInputCpt<A extends string>(props: {
  header: string;
  isRequired: boolean;
  extra?: JSX.Element;
  registerKey: UseFormRegisterReturn<A>;
}) {
  return (
    <div className="flex flex-col space-y-2">
      <label className="text-sm font-semibold opacity-75 flex space-x-1">
        <p className=" text-white">{props.header}</p>
        {props.isRequired && <p className="text-red-500">*</p>}
      </label>
      <input
        {...props.registerKey}
        type="password"
        className="bg-slate-800 rounded text-white p-2 focus text-opacity-80 focus:ring-0 border-none"
      />
      {props.extra && <div className="flex">{props.extra}</div>}
    </div>
  );
}
