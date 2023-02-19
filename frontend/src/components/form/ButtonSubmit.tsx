import { Spinner } from "../../assets";

/**
 * Generic submit button.
 */
export function ButtonSubmit(props: { name: string; isLoading: boolean; extra?: JSX.Element }) {
  return (
    <div className="flex flex-col space-y-2">
      <button className=" bg-emerald-500 p-2.5 rounded font-semibold text-white hover:bg-emerald-600 transition-all">
        {props.isLoading ? <Spinner className="animate-spin invert h-6 w-6 mx-auto" /> : props.name}
      </button>
      {props.extra}
    </div>
  );
}
