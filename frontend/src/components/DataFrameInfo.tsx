import { EConnector } from "./Connectors";
import { ReactComponent as S3ConnectSvg } from "../assets/connectors/s3.svg";
import { ReactComponent as PostgreSQLSvg } from "../assets/connectors/postgreSQL.svg";
import { ReactComponent as MySQLSvg } from "../assets/connectors/mySQL.svg";
import { ReactComponent as MariaDBSvg } from "../assets/connectors/mariaDB.svg";
import { ReactComponent as MongoDBSvg } from "../assets/connectors/mongoDB.svg";
import { ReactComponent as RemoveCrossSvg } from "../assets/interacts/removeCross.svg";
import { numberFormatter, unixTimestampSecFormatter } from "../utils/stringFormatter";
import { ReactComponent as DownloadIconSvg } from "../assets/downloadIcon.svg";
import { ReactComponent as StarIconSvg } from "../assets/starIcon.svg";

// DataFrame information
export const DataFrameInfo = () => {
  // Retrieve data
  const dataframe = {
    id: 3,
    name: "Velibs",
    connector: EConnector.PostgreSQL,
    nbRows: 12311,
    nbCols: 103,
    dataSizeInBytes: 2300000000,
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
    <div className="flex h-28 w-96 select-none rounded-xl bg-gray-900 drop-shadow-md">
      {/* Connector */}
      <div className="flex h-full fill-green-500 px-3 py-6">{connImg()}</div>

      {/* Infos */}
      <div className="relative flex flex-1 flex-col justify-between p-2">
        {/* AuthorId */}
        <div className="absolute left-2 text-xs italic text-green-500 opacity-50">
          <div>#{profil.id}</div>
          {profil.name}
        </div>

        {/* Remove button */}
        <button className="absolute right-2 bg-gray-900 fill-rose-500 text-xl font-bold" onClick={() => {}}>
          <RemoveCrossSvg className="h-3 w-3" />
        </button>

        {/* Title */}
        <div className="mx-auto flex">
          <div className="text-xl font-bold text-green-500">{dataframe.name}</div>
          <div className="text-xs italic text-green-500 opacity-50">#{dataframe.id}</div>
        </div>

        {/* Content */}
        <div className="flex items-center justify-evenly">
          {/* General DataFrame Info */}
          <div className="flex flex-col text-xs text-green-500">
            <div>{numberFormatter(dataframe.nbRows)} rows</div>
            <div>{numberFormatter(dataframe.nbCols)} columns</div>
            <div>{numberFormatter(dataframe.dataSizeInBytes)} bytes</div>
          </div>

          {/* DataFrame state */}
          <div></div>
        </div>

        {/* Footer */}
        <div className="flex items-center justify-between">
          {/* Updated since */}
          <div className="bottom-2 left-2 text-xs italic text-green-500 opacity-50">
            {unixTimestampSecFormatter(dataframe.createdAtInUnixSecs)}
          </div>

          {/* Downloads & Stars */}
          <div className="flex space-x-2">
            <button className="group/downloads flex items-center space-x-1">
              <DownloadIconSvg
                className="h-4 w-4 fill-rose-500 transition-all duration-200 ease-linear
               group-hover/downloads:fill-white"
              />
              <div
                className="text-xs text-rose-500 transition-all duration-200 ease-linear
               group-hover/downloads:text-white">
                {numberFormatter(dataframe.downloadCount)}
              </div>
            </button>
            <button className="group/stars flex items-center space-x-1">
              <StarIconSvg
                className="h-4 w-4 fill-yellow-500 transition-all duration-200 ease-linear
               group-hover/stars:fill-white"
              />
              <div
                className="text-xs text-yellow-500 transition-all duration-200 ease-linear
               group-hover/stars:text-white">
                {numberFormatter(dataframe.startCount)}
              </div>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
