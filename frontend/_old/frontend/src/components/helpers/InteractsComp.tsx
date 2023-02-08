import { ReactComponent as RemoveCrossSvg } from "../../assets/interacts/removeCross.svg";

/**
 * Close button.
 */
interface CloseButtonProps {
  onClick: () => void;
}
export const CloseButton = (props: CloseButtonProps) => (
  <button
    className="transition-effect rounded-md bg-gray-900 fill-rose-500 p-1
    text-xl font-bold hover:bg-rose-500 hover:fill-white hover:shadow-md"
    onClick={props.onClick}>
    <RemoveCrossSvg className="h-3 w-3" />
  </button>
);
