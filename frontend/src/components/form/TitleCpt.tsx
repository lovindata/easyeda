/**
 * Generic title.
 */
export function TitleCpt(props: { title: string; desc?: string }) {
  return (
    <div className="flex flex-col items-center space-y-2">
      <h1 className="text-2xl font-bold text-white">{props.title}</h1>
      {props.desc && <p className="brightness-75">{props.desc}</p>}
    </div>
  );
}
