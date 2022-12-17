import { EConnector } from "./Connectors";
import { ReactComponent as S3ConnectSvg } from "../assets/connectors/s3.svg";
import { ReactComponent as PostgreSQLSvg } from "../assets/connectors/postgreSQL.svg";
import { ReactComponent as MySQLSvg } from "../assets/connectors/mySQL.svg";
import { ReactComponent as MariaDBSvg } from "../assets/connectors/mariaDB.svg";
import { ReactComponent as MongoDBSvg } from "../assets/connectors/mongoDB.svg";
import { ReactComponent as RemoveCrossSvg } from "../assets/interacts/removeCross.svg";

// DataFrame information
export const DataFrameInfo = () => {
  // Retrieve data
  const dataframe = {
    id: 3,
    name: "Velibs",
    connector: EConnector.PostgreSQL,
    nbRows: 12311,
    nbCols: 103,
    dataSizeInBytes: 2300000,
    createdAtInUnixSecs: 1671291416,
    downloadCount: 1313,
    startCount: 43,
    authorId: 3769,
  };
  const profil = {
    id: 3769,
    name: "Elorine",
  };

  // Define connector image
  const connImg = () => {
    switch (dataframe.connector) {
      case EConnector.S3:
        return <S3ConnectSvg className="h-auto w-auto" />;
      case EConnector.PostgreSQL:
        return <PostgreSQLSvg className="h-auto w-auto" />;
      case EConnector.MySQL:
        return <MySQLSvg className="h-auto w-auto" />;
      case EConnector.MariaDB:
        return <MariaDBSvg className="h-auto w-auto" />;
      case EConnector.MongoDB:
        return <MongoDBSvg className="h-auto w-auto" />;
    }
  };

  // Render
  return (
    <div className="flex h-28 w-96 select-none bg-gray-900 drop-shadow-md">
      {/* Connector */}
      <div className="flex h-full fill-green-500 px-3 py-6">{connImg()}</div>

      {/* Infos */}
      <div className="flex flex-1 flex-col p-2">
        {/* Header */}
        <div className="flex flex-row items-center justify-between">
          <div className="text-xs text-green-700">
            {profil.name}#{profil.id}
          </div>

          {/*<button className="-mt-9 -mr-3 rounded-full bg-gray-900 fill-rose-500 p-2 text-xl font-bold">
            <RemoveCrossSvg />
  </button>*/}
        </div>

        {/* Title */}
        <div></div>

        {/* Content */}
        <div>
          {/* General DataFrame Info */}
          <div></div>
          {/* DataFrame state */}
          <div></div>
        </div>

        {/* Footer */}
        <div></div>
      </div>
    </div>
  );
};
