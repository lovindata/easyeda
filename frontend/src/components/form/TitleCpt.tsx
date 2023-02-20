/**
 * Generic title.
 */
export function TitleCpt(props: { title: string; desc: string }) {
  return (
    <div className="flex flex-col items-center space-y-2">
      <h1 className="text-white font-bold text-2xl">{props.title}</h1>
      <p className="text-white opacity-50">{props.desc}</p>
    </div>
  );
}
