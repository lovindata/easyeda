import { ReactComponent as S3ConnectSvg } from "../assets/connectors/s3.svg";
import { ReactComponent as PostgreSQLSvg } from "../assets/connectors/postgreSQL.svg";
import { ReactComponent as MySQLSvg } from "../assets/connectors/mySQL.svg";
import { ReactComponent as MariaDBSvg } from "../assets/connectors/mariaDB.svg";
import { ReactComponent as MongoDBSvg } from "../assets/connectors/mongoDB.svg";

// Connectors
export const Connectors = () => {
  // Render
  return (
    <div className="flex space-x-7">
      <Connector idName="S3" svg={S3ConnectSvg} />
      <Connector idName="PostgreSQL" svg={PostgreSQLSvg} />
      <Connector idName="MySQL" svg={MySQLSvg} />
      <Connector idName="MariaDB" svg={MariaDBSvg} />
      <Connector idName="MongoDB" svg={MongoDBSvg} />
    </div>
  );
};

// Connector
interface ConnectorProps {
  idName: string;
  svg: React.FunctionComponent<
    React.SVGProps<SVGSVGElement> & {
      title?: string | undefined;
    }
  >;
}
const Connector = (props: ConnectorProps) => {
  // Render
  return (
    <button
      className="flex h-24 w-32 items-center justify-center rounded-xl bg-gray-900 fill-green-500
      p-7 drop-shadow-md
      transition-all duration-200 ease-linear 
      hover:bg-green-500 hover:fill-white"
      onClick={() => {}}>
      <props.svg className="h-auto w-auto" />
    </button>
  );
};
