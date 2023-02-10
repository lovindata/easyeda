/**
 * Generic submit button.
 */
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
