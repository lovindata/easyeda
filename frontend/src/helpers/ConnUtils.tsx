import { Postgres, Mongo, Database, Schema, Table, Conn } from "../assets";

/**
 * Connection unstyled component.
 */
export function ConnIconCpt(props: { connType: string; className?: string }) {
  if (props.connType == "postgres") return <Postgres className={props.className} />;
  if (props.connType === "mongo") return <Mongo className={props.className} />;
  return <Conn className={props.className} />;
}

// /**
//  * Entity type.
//  */
// export enum EntTypeEnum {
//   database,
//   schema,
//   table,
// }
//
// /**
//  * Connection entity unstyled component.
//  */
// export function EntIconCpt(props: { entType: EntTypeEnum; className?: string }) {
//   let out: JSX.Element;
//   switch (props.entType) {
//     case EntTypeEnum.database:
//       out = <Database className={props.className} />;
//       break;
//     case EntTypeEnum.schema:
//       out = <Schema className={props.className} />;
//       break;
//     case EntTypeEnum.table:
//       out = <Table className={props.className} />;
//       break;
//   }
//   return out;
// }
