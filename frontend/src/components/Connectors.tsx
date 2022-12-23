import { ReactComponent as S3ConnectSvg } from "../assets/connectors/s3.svg";
import { ReactComponent as PostgreSQLSvg } from "../assets/connectors/postgreSQL.svg";
import { ReactComponent as MySQLSvg } from "../assets/connectors/mySQL.svg";
import { ReactComponent as MariaDBSvg } from "../assets/connectors/mariaDB.svg";
import { ReactComponent as MongoDBSvg } from "../assets/connectors/mongoDB.svg";

// Enum of connectors
export enum EConnector {
  S3,
  PostgreSQL,
  MySQL,
  MariaDB,
  MongoDB,
}

// Connectors
interface ConnectorsProps {
  modalSetter: React.Dispatch<React.SetStateAction<boolean>>;
}
export const Connectors = (props: ConnectorsProps) => {
  // Render
  return (
    <div className="flex space-x-7">
      <Connector connType={EConnector.S3} svg={S3ConnectSvg} modalSetter={props.modalSetter} />
      <Connector connType={EConnector.PostgreSQL} svg={PostgreSQLSvg} modalSetter={props.modalSetter} />
      <Connector connType={EConnector.MySQL} svg={MySQLSvg} modalSetter={props.modalSetter} />
      <Connector connType={EConnector.MariaDB} svg={MariaDBSvg} modalSetter={props.modalSetter} />
      <Connector connType={EConnector.MongoDB} svg={MongoDBSvg} modalSetter={props.modalSetter} />
    </div>
  );
};

// Connector
interface ConnectorProps {
  connType: EConnector;
  svg: React.FunctionComponent<
    React.SVGProps<SVGSVGElement> & {
      title?: string | undefined;
    }
  >;
  modalSetter: React.Dispatch<React.SetStateAction<boolean>>;
}
const Connector = (props: ConnectorProps) => {
  // Render
  return (
    <button
      className="transition-effect flex h-24 w-32 items-center justify-center rounded-xl bg-gray-900
      fill-green-500 p-7 drop-shadow-md hover:bg-green-500 hover:fill-white"
      onClick={() => props.modalSetter(true)}>
      <props.svg className="h-auto w-auto" />
    </button>
  );
};
