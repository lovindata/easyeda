import { useConnRtsList } from "../../api/routes/ConnRtsHk";
import { useNodeRtsStatus } from "../../api/routes/NodeRtsHk";
import { useUserRtsStatus } from "../../api/routes/UserRtsHk";
import { Conn, Pipeline, Profil } from "../../assets";
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
  // States
  const [isOpen, setIsOpen] = useState(false);
  const { node } = useNodeRtsStatus();
  const { user } = useUserRtsStatus();
  const { connsStatus } = useConnRtsList();
  // console.log(user);
  // console.log(node);
  // console.log(connsStatus);

  // Render
  return (
    <div className="relative flex flex-col justify-end p-2.5">
      <div className="peer relative cursor-pointer" onClick={() => setIsOpen((_) => !_)}>
        <Profil className={"peer flex fill-primary" + (isOpen ? " brightness-150" : " hover:brightness-150")} />
        <span
          className={
            "absolute bottom-0 right-0 h-2.5 w-2.5 rounded-full border-2 border-base-100" +
            (user ? " bg-success" : " bg-warning")
          }
        />
      </div>
      <div
        className={
          "pointer-events-none absolute left-full ml-1 min-w-max origin-left select-none rounded" +
          " bg-neutral px-1.5 py-1 text-xs font-thin shadow transition-all duration-300 ease-in-out" +
          (isOpen ? " scale-100" : " scale-0 peer-hover:scale-100")
        }
      >
        <div className="flex flex-col">
          <div className="flex flex-col items-center">
            <h1 className="">My Status</h1>
            <div className="flex">
              <div className="flex flex-col">
                <p className="">{user?.username}</p>
                <p>#{user?.id}</p>
                <p>{user ? "(Connected)" : "(Connecting)"}</p>
              </div>
              <div className="flex flex-col">
                <div className="flex">
                  <Conn className="h-5" />
                  <p>
                    {connsStatus?.filter((_) => _.isUp).length}/{connsStatus?.length}
                  </p>
                </div>
                <div className="flex">
                  <Pipeline className="h-5" />
                  <p>?/?</p>
                </div>
              </div>
            </div>
          </div>

          <div className="flex flex-col items-center">
            <h1 className="">My Status</h1>
            <div className="flex">
              <div className="flex flex-col">
                <p className="">{user?.username}</p>
                <p>#{user?.id}</p>
                <p>{user ? "(Connected)" : "(Connecting)"}</p>
              </div>
              <div className="flex flex-col">
                <div className="flex">
                  <Conn className="h-5" />
                  <p>
                    {connsStatus?.filter((_) => _.isUp).length}/{connsStatus?.length}
                  </p>
                </div>
                <div className="flex">
                  <Pipeline className="h-5" />
                  <p>?/?</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
