import { useConnRtsIdsTest, useConnRtsList } from "../../api/routes/ConnRtsHk";
import { useNodeRtsStatus } from "../../api/routes/NodeRtsHk";
import { useUserRtsStatus } from "../../api/routes/UserRtsHk";
import { Conn, Pipeline, Profil } from "../../assets";
import { Disclosure } from "@headlessui/react";
import { useState } from "react";
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
      <div
        className="pointer-events-none absolute left-full ml-1 origin-left scale-0 select-none rounded
        bg-neutral px-1.5 py-1 text-xs font-thin
        shadow transition-all duration-300 ease-in-out peer-hover:scale-100"
      >
        {props.title}
      </div>
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

  // Text color logic
  const upTextColor = (stat: { up: number; total: number } | undefined) => {
    if (stat === undefined) return " text-warning";
    if (stat.up === stat.total) return " text-success";
    return " text-error";
  };
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
          <Disclosure.Button className="flex relative items-center">
            <Profil className={"fill-primary peer" + (open ? " brightness-150" : " hover:brightness-150")} />
            <span
              className={
                "absolute bottom-0 right-0 h-2.5 w-2.5 rounded-full border-2 border-base-100" +
                (user ? " bg-success" : " bg-warning")
              }
            />
            {!open && (
              <div
                className="pointer-events-none absolute left-full ml-3.5 origin-left select-none rounded
                bg-neutral px-1.5 py-1 text-xs font-thin
                shadow transition-all duration-300 ease-in-out scale-0 peer-hover:scale-100"
              >
                Status
              </div>
            )}
          </Disclosure.Button>
          <Disclosure.Panel
            className={"select-none absolute left-full ml-1 rounded bg-neutral text-xs font-thin shadow"}
          >
            <div className="flex flex-col space-y-2">
              {/* My Status */}
              <div className="flex flex-col items-center px-2 pt-2 space-y-1">
                <h1 className="font-semibold text-sm">User</h1>
                <div className="flex items-center space-x-4">
                  <div className="flex flex-col">
                    <p className="font-bold">{user ? user.username : "???"}</p>
                    <p className="italic">#{user ? user.id : "?"}</p>
                    {user ? <p className="text-success">(Connected)</p> : <p className="text-warning">(Connecting)</p>}
                  </div>
                  <div className="flex flex-col">
                    <div className="flex space-x-1.5 items-center">
                      <Conn className="h-5 fill-primary" />
                      <p
                        className={upTextColor(
                          isUps && { up: isUps.filter((_) => _ === true).length, total: isUps.length }
                        )}
                      >
                        {isUps ? `${isUps.filter((_) => _ === true).length}/${isUps.length}` : "?/?"}
                      </p>
                    </div>
                    <div className="flex space-x-1.5 items-center">
                      <Pipeline className="h-5 fill-primary" />
                      <p className="text-warning">?/?</p>
                    </div>
                  </div>
                </div>
              </div>

              {/* Nodes status */}
              <div className="flex flex-col items-center px-2 pb-2 space-y-1">
                <h1 className="font-semibold text-sm">Nodes</h1>
                <div className="flex space-x-2">
                  <div className="flex flex-col items-center">
                    <p>Node(s)</p>
                    <p>{nodeStatus ? nodeStatus.nbNodes : "?"}</p>
                  </div>
                  <div
                    className={
                      "flex flex-col items-center" +
                      usageTextColor(nodeStatus && { usage: nodeStatus.cpu, total: nodeStatus.cpuTotal })
                    }
                  >
                    <p>CPU</p>
                    <p>{nodeStatus ? `${nodeStatus.cpu.toFixed(1)}/${nodeStatus.cpuTotal.toFixed(1)}` : "?/?"}</p>
                  </div>
                  <div
                    className={
                      "flex flex-col items-center" +
                      usageTextColor(nodeStatus && { usage: nodeStatus.ram, total: nodeStatus.ramTotal })
                    }
                  >
                    <p>RAM</p>
                    <p>{nodeStatus ? `${nodeStatus.ram.toFixed(1)}/${nodeStatus.ramTotal.toFixed(1)}` : "?/?"}</p>
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
