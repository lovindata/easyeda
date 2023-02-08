import { UseFormRegisterReturn } from "react-hook-form";

export function Title(props: { title: string; desc: string }) {
  return (
    <div className="flex flex-col items-center space-y-2">
      <h1 className="text-white font-bold text-2xl">{props.title}</h1>
      <p className="text-white opacity-50">{props.desc}</p>
    </div>
  );
}

export function TextInput<A extends string>(props: {
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
        type="text"
        className="bg-slate-800 rounded text-white p-2 focus text-opacity-80 focus:ring-0 border-none"
      />
      {props.extra && <div className="flex">{props.extra}</div>}
    </div>
  );
}

export function PwdInput<A extends string>(props: {
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

export function DateInput<A extends string>(props: {
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
        type="date"
        className="bg-slate-800 rounded text-white p-2 focus text-opacity-80 focus:ring-0 border-none"
      />
      {props.extra && <div className="flex">{props.extra}</div>}
    </div>
  );
}

export function ButtonSubmit(props: { name: string; extra?: JSX.Element }) {
  return (
    <div className="flex flex-col space-y-2">
      <button className="bg-emerald-500 p-2.5 rounded font-semibold text-white hover:bg-emerald-600 transition-all">
        {props.name}
      </button>
      {props.extra}
    </div>
  );
}
