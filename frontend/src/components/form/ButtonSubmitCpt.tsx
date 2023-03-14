import { Spinner } from "../../assets";

/**
 * Generic submit button.
 */
export function ButtonSubmitCpt(props: { name: string; isLoading: boolean; extra?: JSX.Element }) {
  return (
    <div className="flex flex-col space-y-2">
      <button
        className={
          "rounded bg-emerald-500 p-2.5 font-semibold text-white transition-all hover:brightness-105" +
          (props.isLoading ? " cursor-not-allowed" : "")
        }
      >
        {props.isLoading ? <Spinner className="mx-auto h-6 w-6 animate-spin invert" /> : props.name}
      </button>
      {props.extra}
    </div>
  );
}
