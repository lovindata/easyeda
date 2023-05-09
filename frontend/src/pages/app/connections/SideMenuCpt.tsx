import { useConnRtsList } from "../../../api/routes/ConnRtsHk";
import { usePostgresRtsDbs } from "../../../api/routes/conn/PostgresRtsHk";
import { Add, Dropdown, Mongo, Postgres } from "../../../assets";
import { Refresh } from "../../../assets";
import { Disclosure } from "@headlessui/react";
import { useState } from "react";

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
  const conns = useConnRtsList();
  return (
    <div className="flex select-none flex-col space-y-1 px-5 py-2.5">
      {conns?.map((_) => {
        switch (_.type) {
          case "postgres":
            return <PostgresCpt key={_.id} name={_.name} id={_.id} />;
          case "mongo":
            return <MongoCpt key={_.id} name={_.name} id={_.id} />;
        }
      })}
    </div>
  );
}

/**
 * Postgres component.
 */
function PostgresCpt(props: { name: string; id: number }) {
  // States
  const [enable, setEnable] = useState(false);
  const dbs = usePostgresRtsDbs(props.id, enable);
  console.log(dbs);

  // Render
  return (
    <Disclosure>
      {/* Disclosure button & Connection info */}
      <div className="flex items-center space-x-1 transition-all duration-300 ease-in-out">
        <Disclosure.Button>
          {({ open }) => (
            <Dropdown
              className={"h-3 w-3 cursor-pointer transition-all duration-300 ease-in-out" + (open ? " rotate-90" : "")}
              onClick={() => setEnable(open)}
            />
          )}
        </Disclosure.Button>
        <Postgres className="h-5 w-5 p-0.5" />
        {<div className="text-sm">{props.name}</div>}
        {<div className="font-thin italic">(#{props.id})</div>}
      </div>

      {/* Databases info */}
      <Disclosure.Panel>
        <div className="flex flex-col">{!dbs ? <div>Loading...</div> : <div>{dbs}</div>}</div>
      </Disclosure.Panel>
    </Disclosure>
  );
}

/**
 * Mongo component.
 */
function MongoCpt(props: { name: string; id: number }) {
  return (
    <div className="flex items-center space-x-1 rounded">
      <Dropdown className="h-3 w-3" />
      <Mongo className="h-5 w-5 p-0.5" />
      {<div className="text-sm">{props.name}</div>}
      {<div className="font-thin italic">(#{props.id})</div>}
    </div>
  );
}
