import { UseFormRegisterReturn } from "react-hook-form";

/**
 * Generic submit button.
 */
export function ButtonSubmitCpt(props: { name: string; isLoading: boolean; extra?: JSX.Element }) {
  return (
    <div className="flex flex-col space-y-2">
      <button className={"btn-primary btn" + (props.isLoading ? " btn-outline loading" : "")}>{props.name}</button>
      {props.extra}
    </div>
  );
}

/**
 * Generic date input.
 */
export function DateInputCpt<A extends string>(props: {
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
      <input
        {...props.registerKey}
        type="date"
        className="input-bordered input-primary input bg-neutral-focus text-neutral-content"
      />
      {props.extra && <div className="flex">{props.extra}</div>}
    </div>
  );
}

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
      <label className="flex space-x-1 text-sm font-semibold">
        <p>{props.header}</p>
        {props.isRequired && <p className="text-accent">*</p>}
      </label>
      <input
        {...props.registerKey}
        type="password"
        className="input-bordered input-primary input bg-neutral-focus text-neutral-content"
      />
      {props.extra && <div className="flex">{props.extra}</div>}
    </div>
  );
}

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
        {props.isRequired && <p className="text-accent">*</p>}
      </label>
      <input
        {...props.registerKey}
        type="text"
        className="input-bordered input-primary input bg-neutral-focus text-neutral-content"
      />
      {props.extra && <div className="flex">{props.extra}</div>}
    </div>
  );
}

/**
 * Generic title.
 */
export function TitleCpt(props: { title: string; desc?: string }) {
  return (
    <div className="flex flex-col items-center space-y-2">
      <h1 className="text-2xl font-bold">{props.title}</h1>
      {props.desc && <p className="contrast-50">{props.desc}</p>}
    </div>
  );
}
