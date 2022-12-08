import { ReactComponent as DataFramesSvg } from "../assets/dataframes.svg";
import { ReactComponent as OperatorsSvg } from "../assets/operators.svg";

// SideBar
export const SideBar = () => {
  // Render
  return (
    <div className="flex h-screen w-14 flex-col items-center justify-center bg-gray-900 drop-shadow">
      <Icon svg={DataFramesSvg} />
      <Icon svg={OperatorsSvg} />
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
}
const Icon = (props: IconProps) => (
  <div
    className="m-2 flex h-11 w-11 cursor-pointer items-center justify-center rounded-3xl bg-gray-800
    fill-rose-500 p-2 drop-shadow transition-all duration-300 ease-linear hover:rounded-xl
     hover:bg-rose-500 hover:fill-white">
    <props.svg className="h-auto w-auto" />
  </div>
);
