import { Link, useLocation } from "react-router-dom";
import { Conn, Pipeline } from "../../assets";

/**
 * Sidebar component.
 */
export function SideBarCpt() {
  // Get current path
  const currentPath = useLocation().pathname;

  // Render
  return (
    <div className="flex h-screen w-16 flex-col justify-center bg-slate-800">
      <IconTabLink to="connections" title="Connections" svg={Conn} isCurrent={currentPath == "/app/connections"} />
      <IconTabLink to="pipelines" title="Pipelines" svg={Pipeline} isCurrent={currentPath == "/app/pipelines"} />
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
  isCurrent: boolean;
}) {
  return (
    <div className="relative flex flex-col justify-center p-4">
      <span
        className={`absolute -ml-4 h-2/3 w-1 rounded-r bg-emerald-500 brightness-150
        transition-all duration-300 ease-in-out ${props.isCurrent ? "scale-100" : "scale-0"}`}
      />
      <Link to={props.to} className="peer">
        <props.svg className={`fill-emerald-500 ${props.isCurrent ? "brightness-150" : "hover:brightness-150"}`} />
      </Link>
      <div
        className="pointer-events-none absolute left-full ml-2 origin-left scale-0 select-none rounded-xl bg-slate-800
        p-2 text-sm font-bold
        transition-all duration-300 ease-in-out peer-hover:scale-100"
      >
        {props.title}
      </div>
    </div>
  );
}
