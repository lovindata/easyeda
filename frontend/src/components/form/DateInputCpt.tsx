import { UseFormRegisterReturn } from "react-hook-form";

/**
 * Generic date input.
 */
export default function DateInputCpt<A extends string>(props: {
  header: string;
  isRequired: boolean;
  extra?: JSX.Element;
  registerKey: UseFormRegisterReturn<A>;
}) {
  return (
    <div className="flex flex-col space-y-2">
      <label className="flex space-x-1 text-sm font-semibold">
        <p>{props.header}</p>
        {props.isRequired && <p className="text-accent">*</p>}
      </label>
      <input {...props.registerKey} type="date" className="input-bordered input-primary input" />
      {props.extra && <div className="flex">{props.extra}</div>}
    </div>
  );
}
