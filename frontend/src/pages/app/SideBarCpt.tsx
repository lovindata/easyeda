import { Link } from "react-router-dom";
import { Conn, Pipeline } from "../../assets";

/**
 * Sidebar component.
 */
export function SideBarCpt() {
  return (
    <div className="flex h-screen w-16 flex-col justify-center bg-slate-800">
      <IconTabLink to="connections" title="Connections" svg={Conn} />
      <IconTabLink to="pipelines" title="Pipelines" svg={Pipeline} />
    </div>
  );
}

/**
 * Sidebar icon tab component.
 */
function IconTabLink(props: {
  to: string;
  title: string;
  svg: React.FunctionComponent<
    React.SVGProps<SVGSVGElement> & {
      title?: string | undefined;
    }
  >;
}) {
  return (
    <Link to={props.to} className="p-2">
      <props.svg
        className="rounded-xl fill-white p-1.5 transition-all duration-300
      ease-in-out hover:bg-white hover:fill-slate-700"
      />
      <div className="absolute">{props.title}</div>
    </Link>
  );
}
