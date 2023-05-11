import { useConnRtsList } from "../../../api/routes/ConnRtsHk";
import { useMongoRtsDbs, useMongoRtsColls } from "../../../api/routes/conn/MongoRtsHk";
import { usePostgresRtsDbs, usePostgresRtsSchs, usePostgresRtsTabs } from "../../../api/routes/conn/PostgresRtsHk";
import { Add, Collection, Database, Dropdown, Mongo, Postgres, Schema, Table } from "../../../assets";
import { Refresh } from "../../../assets";
import { Disclosure } from "@headlessui/react";
import { useState } from "react";

/**
 * Side menu component.
 */
export default function SideMenuCpt() {
  return (
    <div
      className="flex h-screen w-64 flex-col divide-y-8 divide-base-100 bg-base-300 fill-base-content text-base-content
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
    <div className="flex select-none items-center justify-between px-5 py-2.5">
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
    <div
      className="flex select-none flex-col space-y-1 overflow-hidden scroll-smooth px-5 py-2.5
      text-sm scrollbar-thin scrollbar-thumb-neutral hover:overflow-auto"
    >
      {conns?.map((_) => {
        switch (_.type) {
          case "postgres":
            return <PostgresCpt key={_.id} connName={_.name} connId={_.id} />;
          case "mongo":
            return <MongoCpt key={_.id} connName={_.name} connId={_.id} />;
        }
      })}
    </div>
  );
}

/**
 * Generic disclosure component.
 */
function GenericDisclosureCpt(props: {
  onDiscloseClick?: () => void;
  disclosureHeader: JSX.Element;
  disclosureContent?: JSX.Element;
}) {
  // If only header render
  if (!props.disclosureContent)
    return (
      <div
        className="flex cursor-pointer items-center space-x-1 rounded p-0.5
        transition-all duration-300 ease-in-out hover:backdrop-contrast-50"
      >
        {props.disclosureHeader}
      </div>
    );

  // Else
  return (
    <Disclosure>
      {/* Disclosure button & Info */}
      <Disclosure.Button onClick={props.onDiscloseClick} className="min-w-full">
        {({ open }) => (
          <div
            className="flex items-center space-x-1 rounded p-0.5
            transition-all duration-300 ease-in-out hover:backdrop-contrast-50"
          >
            <Dropdown
              className={"h-3 w-3 min-w-max transition-all duration-300 ease-in-out" + (open ? " rotate-90" : "")}
            />
            {props.disclosureHeader}
          </div>
        )}
      </Disclosure.Button>

      {/* Disclosure content */}
      <Disclosure.Panel>
        <div className="flex flex-col">{props.disclosureContent}</div>
      </Disclosure.Panel>
    </Disclosure>
  );
}

/**
 * Postgres connection component.
 */
function PostgresCpt(props: { connId: number; connName: string }) {
  const [enable, setEnable] = useState(false);
  const dbs = usePostgresRtsDbs(props.connId, enable);
  return (
    <GenericDisclosureCpt
      onDiscloseClick={() => setEnable(!enable)}
      disclosureHeader={
        <>
          <Postgres className="h-5 w-5 min-w-max p-0.5" />
          <div>{props.connName}</div>
          <div className="font-thin italic">(#{props.connId})</div>
        </>
      }
      disclosureContent={
        <>
          {dbs?.map((_, idx) => (
            <div key={idx} className="ml-2.5">
              <PostgresDbCpt connId={props.connId} database={_} />
            </div>
          ))}
        </>
      }
    />
  );
}

/**
 * Postgres database component.
 */
function PostgresDbCpt(props: { connId: number; database: string }) {
  const [enable, setEnable] = useState(false);
  const schs = usePostgresRtsSchs(props.connId, props.database, enable);
  return (
    <GenericDisclosureCpt
      onDiscloseClick={() => setEnable(!enable)}
      disclosureHeader={
        <>
          <Database className="h-5 w-5 min-w-max p-0.5" />
          <div>{props.database}</div>
        </>
      }
      disclosureContent={
        <>
          {schs?.map((_, idx) => (
            <div key={idx} className="ml-2.5">
              <PostgresSchCpt connId={props.connId} database={props.database} schema={_} />
            </div>
          ))}
        </>
      }
    />
  );
}

/**
 * Postgres schema component.
 */
function PostgresSchCpt(props: { connId: number; database: string; schema: string }) {
  const [enable, setEnable] = useState(false);
  const tabs = usePostgresRtsTabs(props.connId, props.database, props.schema, enable);
  return (
    <GenericDisclosureCpt
      onDiscloseClick={() => setEnable(!enable)}
      disclosureHeader={
        <>
          <Schema className="h-5 w-5 min-w-max p-0.5" />
          <div>{props.schema}</div>
        </>
      }
      disclosureContent={
        <>
          {tabs?.map((_, idx) => (
            <div key={idx} className="ml-7">
              <PostgresTabCpt table={_} />
            </div>
          ))}
        </>
      }
    />
  );
}

/**
 * Postgres table component.
 */
function PostgresTabCpt(props: { table: string }) {
  return (
    <GenericDisclosureCpt
      disclosureHeader={
        <>
          <Table className="h-5 w-5 min-w-max p-0.5" />
          <div className="truncate">{props.table}</div>
        </>
      }
    />
  );
}

/**
 * Mongo connection component.
 */
function MongoCpt(props: { connId: number; connName: string }) {
  const [enable, setEnable] = useState(false);
  const dbs = useMongoRtsDbs(props.connId, enable);
  return (
    <GenericDisclosureCpt
      onDiscloseClick={() => setEnable(!enable)}
      disclosureHeader={
        <>
          <Mongo className="h-5 w-5 min-w-max p-0.5" />
          <div>{props.connName}</div>
          <div className="font-thin italic">(#{props.connId})</div>
        </>
      }
      disclosureContent={
        <>
          {dbs?.map((_, idx) => (
            <div key={idx} className="ml-2.5">
              <MongoDbCpt connId={props.connId} database={_} />
            </div>
          ))}
        </>
      }
    />
  );
}

/**
 * Mongo database component.
 */
function MongoDbCpt(props: { connId: number; database: string }) {
  const [enable, setEnable] = useState(false);
  const schs = useMongoRtsColls(props.connId, props.database, enable);
  return (
    <GenericDisclosureCpt
      onDiscloseClick={() => setEnable(!enable)}
      disclosureHeader={
        <>
          <Database className="h-5 w-5 min-w-max p-0.5" />
          <div>{props.database}</div>
        </>
      }
      disclosureContent={
        <>
          {schs?.map((_, idx) => (
            <div key={idx} className="ml-7">
              <MongoCollCpt collection={_} />
            </div>
          ))}
        </>
      }
    />
  );
}

/**
 * Mongo collection component.
 */
function MongoCollCpt(props: { collection: string }) {
  return (
    <GenericDisclosureCpt
      disclosureHeader={
        <>
          <Collection className="h-5 w-5 min-w-max p-0.5" />
          <div>{props.collection}</div>
        </>
      }
    />
  );
}
