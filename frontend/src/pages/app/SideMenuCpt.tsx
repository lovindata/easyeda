import { useConnRtsList } from "../../api/routes/ConnRtsHk";
import { Add } from "../../assets";
import { Refresh } from "../../assets";
import { ConnIconCpt } from "../../helpers/ConnUtils";

/**
 * Side menu component.
 */
export default function SideMenuCpt() {
  return (
    <div
      className="flex w-64 flex-col divide-y-[20px] divide-base-100 bg-base-300 fill-base-content text-base-content
      shadow-inner transition-all duration-300 ease-in-out"
    >
      <HeaderConnCpt />
      <ContentConnCpt />
    </div>
  );
}

/**
 * Header menu component.
 */
function HeaderConnCpt() {
  return (
    <div className="flex select-none items-center justify-between rounded px-5 py-2.5">
      <div className="text-xs font-semibold">CONNECTIONS</div>
      <div className="flex space-x-1">
        <Refresh
          className="h-5 w-5 cursor-pointer rounded p-0.5
      transition-all duration-300 ease-in-out hover:bg-base-content hover:fill-base-300"
        />
        <Add
          className="h-5 w-5 cursor-pointer rounded p-1
      transition-all duration-300 ease-in-out hover:bg-base-content hover:fill-base-300"
        />
      </div>
    </div>
  );
}

/**
 * Content menu component.
 */
function ContentConnCpt() {
  // Get connections
  const conns = useConnRtsList();

  // Render
  return (
    <div className="flex select-none flex-col space-y-1 px-5 py-2.5">
      {conns?.map((_) => (
        <div key={_.id} className="flex items-center space-x-1 text-sm">
          <ConnIconCpt connType={_.type} className="h-5 w-5 p-0.5" />
          {<div>{_.name}</div>}
          {<div className="font-thin italic">(#{_.id})</div>}
        </div>
      ))}
    </div>
  );
}
