import { ReactComponent as DataFramesSvg } from "../assets/dataframes.svg";
import { ReactComponent as RanksSvg } from "../assets/ranks.svg";
import { Link } from "react-router-dom";

// SideBar
export const SideBar = () => {
  // Render
  return (
    <div className="flex h-screen w-14 drop-shadow-md">
      <div className="fixed flex h-screen w-14 flex-col items-center justify-center bg-gray-900">
        <Icon svg={DataFramesSvg} description={"DataFrames"} href={"/dataframes"} />
        <Icon svg={RanksSvg} description={"Ranks"} href={"/ranking"} />
      </div>
    </div>
  );
};

// Icon in SideBar
interface IconProps {
  svg: React.FunctionComponent<
    React.SVGProps<SVGSVGElement> & {
      title?: string | undefined;
    }
  >;
  description: string;
  href: string;
}
const Icon = (props: IconProps) => {
  // Render
  return (
    <Link
      to={props.href}
      className="transition-effect group m-2 flex h-11 w-11
      items-center justify-center rounded-3xl fill-emerald-500 p-2
      hover:rounded-xl hover:bg-emerald-500 hover:fill-white hover:shadow-md">
      {/* Icon */}
      <props.svg className="h-auto w-auto" />

      {/* Tips */}
      <span
        className="transition-effect pointer-events-none absolute left-14 m-2 min-w-max origin-left scale-0
        rounded-xl bg-gray-900 p-2 text-xs font-bold text-white group-hover:scale-100">
        {props.description}
      </span>
    </Link>
  );
};
