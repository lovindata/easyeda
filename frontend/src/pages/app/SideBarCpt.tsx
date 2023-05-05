import { useConnRtsIdsTest, useConnRtsList } from "../../api/routes/ConnRtsHk";
import { useNodeRtsStatus } from "../../api/routes/NodeRtsHk";
import { useUserRtsStatus } from "../../api/routes/UserRtsHk";
import { Conn, Pipeline, Profil } from "../../assets";
import { Disclosure } from "@headlessui/react";
import { Link, useLocation } from "react-router-dom";

/**
 * Sidebar component.
 */
export default function SideBarCpt() {
  // Get current path
  const location = useLocation();
  const currentPath = location.pathname;

  // Render
  return (
    <div className="flex h-screen w-12 flex-col">
      <div className="flex grow flex-col justify-center">
        <IconTabLink to="connections" title="Connections" svg={Conn} isCurrent={currentPath == "/app/connections"} />
        <IconTabLink to="pipelines" title="Pipelines" svg={Pipeline} isCurrent={currentPath == "/app/pipelines"} />
      </div>
      <IconUser />
    </div>
  );
}

/**
 * Tooltip.
 */
function Tooltip(props: { text: string; className?: string }) {
  return (
    <div
      className={
        "pointer-events-none select-none rounded bg-neutral px-1.5 py-1 text-xs font-thin text-neutral-content shadow transition-all duration-300 ease-in-out" +
        (props.className ? ` ${props.className}` : "")
      }
    >
      {props.text}
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
    <div className="relative flex flex-col justify-center p-2.5">
      <span
        className={`absolute -ml-2.5 h-full w-0.5 bg-primary brightness-150
        transition-all duration-300 ease-in-out ${props.isCurrent ? "scale-100" : "scale-0"}`}
      />
      <Link to={props.to} className="peer">
        <props.svg className={`fill-primary ${props.isCurrent ? "brightness-150" : "hover:brightness-150"}`} />
      </Link>
      <Tooltip text={props.title} className="absolute left-full ml-1 origin-left scale-0 peer-hover:scale-100" />
    </div>
  );
}

/**
 * Sidebar icon user.
 */
function IconUser() {
  // Hooks & Retrieve data
  const user = useUserRtsStatus();
  const conns = useConnRtsList();
  const isUps = useConnRtsIdsTest(conns?.map((_) => _.id));
  const nodeStatus = useNodeRtsStatus();

  // Process data
  const isUpStat = isUps && { up: isUps.filter((_) => _ === true).length, total: isUps.length };
  const cpuStat = nodeStatus && { usage: nodeStatus.cpu, total: nodeStatus.cpuTotal };
  const ramStat = nodeStatus && { usage: nodeStatus.ram, total: nodeStatus.ramTotal };

  // isUpStat: Text color logic
  let isUpTextColor: string;
  if (isUpStat === undefined) isUpTextColor = "text-warning";
  else if (isUpStat.up === isUpStat.total) isUpTextColor = "text-success";
  else isUpTextColor = "text-error";

  // cpuStat & ramStat: Text color logic
  const usageTextColor = (stat: { usage: number; total: number } | undefined) => {
    if (stat === undefined) return " text-warning";
    else {
      const percentage = stat.usage / stat.total;
      if (percentage > 0.9) return " text-error";
      if (percentage < 0.75) return " text-success";
      return " text-warning";
    }
  };

  // Render
  return (
    <Disclosure>
      {({ open }) => (
        <div className="relative flex flex-col justify-end p-2.5">
          <Disclosure.Button className="relative flex items-center">
            <Profil className={"peer fill-primary" + (open ? " brightness-150" : " hover:brightness-150")} />
            <span
              className={
                "absolute bottom-0 right-0 h-2.5 w-2.5 rounded-full border-2 border-base-100" +
                (user ? " bg-success" : " bg-warning")
              }
            />
            {!open && (
              <Tooltip text="Status" className="absolute left-full ml-3.5 origin-left scale-0 peer-hover:scale-100" />
            )}
          </Disclosure.Button>
          <Disclosure.Panel
            className="absolute left-full ml-1 select-none rounded bg-neutral text-xs font-thin text-neutral-content
          shadow"
          >
            <div className="flex flex-col space-y-2">
              {/* My Status */}
              <div className="flex flex-col items-center space-y-1 px-2 pt-2">
                <h1 className="text-sm font-semibold">User</h1>
                <div className="flex items-center space-x-4">
                  <div className="flex flex-col">
                    <p className="font-bold">{user ? user.username : "???"}</p>
                    <p className="italic">#{user ? user.id : "?"}</p>
                    {user ? <p className="text-success">(Connected)</p> : <p className="text-warning">(Connecting)</p>}
                  </div>
                  <div className="flex flex-col">
                    <div className="flex items-center space-x-1.5">
                      <Conn className="h-5 fill-primary" />
                      <p className={isUpTextColor}>{isUpStat ? `${isUpStat.up}/${isUpStat.total}` : "?/?"}</p>
                    </div>
                    <div className="flex items-center space-x-1.5">
                      <Pipeline className="h-5 fill-primary" />
                      <p className="text-warning">?/?</p>
                    </div>
                  </div>
                </div>
              </div>

              {/* Nodes status */}
              <div className="flex flex-col items-center space-y-1 px-2 pb-2">
                <h1 className="text-sm font-semibold">Nodes</h1>
                <div className="flex space-x-2">
                  <div className="flex flex-col items-center">
                    <p>Node(s)</p>
                    <p>{nodeStatus ? nodeStatus.nbNodes : "?"}</p>
                  </div>
                  <div className={"flex flex-col items-center" + usageTextColor(cpuStat)}>
                    <p>CPU</p>
                    <p>{cpuStat ? `${cpuStat.usage.toFixed(1)}/${cpuStat.total.toFixed(1)}` : "?/?"}</p>
                  </div>
                  <div className={"flex flex-col items-center" + usageTextColor(ramStat)}>
                    <p>RAM</p>
                    <p>{ramStat ? `${ramStat.usage.toFixed(1)}/${ramStat.total.toFixed(1)}` : "?/?"}</p>
                  </div>
                </div>
              </div>
            </div>
          </Disclosure.Panel>
        </div>
      )}
    </Disclosure>
  );
}
