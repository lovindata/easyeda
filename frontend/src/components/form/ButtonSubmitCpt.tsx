/**
 * Generic submit button.
 */
export function ButtonSubmitCpt(props: { name: string; isLoading: boolean; extra?: JSX.Element }) {
  return (
    <div className="flex flex-col space-y-2">
      <button className={"btn-primary btn" + (props.isLoading ? " loading" : "")}>{props.name}</button>
      {props.extra}
    </div>
  );
}
