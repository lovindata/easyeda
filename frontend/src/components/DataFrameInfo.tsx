import { EConnector } from "./Connectors";
import { ReactComponent as S3ConnectSvg } from "../assets/connectors/s3.svg";
import { ReactComponent as PostgreSQLSvg } from "../assets/connectors/postgreSQL.svg";
import { ReactComponent as MySQLSvg } from "../assets/connectors/mySQL.svg";
import { ReactComponent as MariaDBSvg } from "../assets/connectors/mariaDB.svg";
import { ReactComponent as MongoDBSvg } from "../assets/connectors/mongoDB.svg";
import { CloseButton } from "./helpers/InteractsComp";
import { numberFormatter, unixTimestampFormatter, bytesToSizeFormatter } from "../utils/stringFormatter";
import { DownloadCount, StarCount } from "./helpers/CountsComp";
import { ReactComponent as ConnectIconSvg } from "../assets/connectIcon.svg";
import { ReactComponent as OperatorsSvg } from "../assets/operators.svg";
import { ReactComponent as StatsIcon } from "../assets/statsIcon.svg";

// DataFrame information
export const DataFrameInfo = () => {
  // Retrieve data
  const dataframe = {
    id: 3,
    name: "Velibs",
    connector: EConnector.PostgreSQL,
    nbRows: 12311,
    nbCols: 103,
    dataSizeInBytes: 2347121666,
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
    <div className="flex h-32 w-96 rounded-xl bg-gray-900 drop-shadow-md">
      {/* Connector */}
      {<div className="flex h-full fill-emerald-500 px-3 py-6">{connImg()}</div>}

      {/* Infos */}
      <div className="relative flex flex-1 flex-col justify-between p-2">
        {/* AuthorId */}
        <div className="absolute left-2 text-xs italic text-emerald-700">
          <div>#{profil.id}</div>
          {profil.name}
        </div>

        {/* Remove button */}
        <div className="absolute right-2">
          <CloseButton onClick={() => {}} />
        </div>

        {/* Title */}
        <div className="relative mx-auto flex">
          <div className="text-xl font-bold text-emerald-500">{dataframe.name}</div>
          <div className="absolute -right-4 text-xs italic text-emerald-700">#{dataframe.id}</div>
        </div>

        {/* Content */}
        <div className="mx-2 flex items-center justify-between">
          {/* General DataFrame Info */}
          <div className="flex space-x-1 text-xs font-semibold text-emerald-500">
            <div className="flex flex-col">
              <div>{numberFormatter(dataframe.nbRows)}</div>
              <div>{numberFormatter(dataframe.nbCols)}</div>
              <div>{bytesToSizeFormatter(dataframe.dataSizeInBytes)}</div>
            </div>
            <div className="flex flex-col">
              <div>rows</div>
              <div>columns</div>
              <div>of data</div>
            </div>
          </div>

          {/* DataFrame state */}
          <div className="flex space-x-2">
            <button
              className="transition-effect rounded-3xl fill-emerald-500 hover:rounded-md hover:bg-emerald-500 hover:fill-white hover:shadow-md"
              onClick={() => {}}>
              <ConnectIconSvg className="h-9 w-9 p-1.5" />
            </button>
            <button
              className="transition-effect rounded-3xl fill-emerald-500 hover:rounded-md hover:bg-emerald-500 hover:fill-white hover:shadow-md"
              onClick={() => {}}>
              <OperatorsSvg className="h-9 w-9 p-1.5" />
            </button>
            <button
              className="transition-effect rounded-3xl fill-emerald-500 hover:rounded-md hover:bg-emerald-500 hover:fill-white hover:shadow-md"
              onClick={() => {}}>
              <StatsIcon className="h-9 w-9 p-1.5" />
            </button>
          </div>
        </div>

        {/* Footer */}
        <div className="flex items-center justify-between">
          {/* Updated since */}
          <div className="bottom-2 left-2 text-xs italic text-emerald-700">
            {unixTimestampFormatter(dataframe.createdAtInUnixSecs)}
          </div>

          {/* Downloads & Stars */}
          <div className="flex space-x-2">
            <button onClick={() => {}}>
              <DownloadCount count={dataframe.downloadCount} />
            </button>
            <button onClick={() => {}}>
              <StarCount count={dataframe.startCount} />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
