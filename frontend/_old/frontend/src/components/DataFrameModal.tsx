import { CloseButton } from "./helpers/InteractsComp";
import { DownloadCount, StarCount } from "./helpers/CountsComp";
import { numberFormatter } from "../utils/stringFormatter";
import { ReactComponent as ConnectIconSvg } from "../assets/connectIcon.svg";
import { useState } from "react";

// Connection form
const ConnectionForm = () => {
  return <div className="absolute inset-x-1/2 bottom-10 h-1/4 w-1/3 bg-white bg-opacity-90"></div>;
};

// Connection modal
const ConnectionModal = () => {
  // Define hooks
  const [isInConfig, switchIsInConfig] = useState(false);

  // Render
  return (
    <>
      {/* Icon */}
      <div className="absolute inset-x-1/2 -bottom-14">
        <button
          className={
            "rounded-xl " +
            (isInConfig
              ? "bg-emerald-500 fill-white shadow-md"
              : "transition-effect bg-gray-900 fill-emerald-500 hover:bg-emerald-500 hover:fill-white hover:shadow-md")
          }
          onClick={() => {
            switchIsInConfig(!isInConfig);
          }}>
          <ConnectIconSvg className="h-9 w-9 p-1.5" />
        </button>
      </div>

      {/* Form */}
      {isInConfig && <ConnectionForm />}
    </>
  );
};

// DataFrame preview modal
interface DataFrameModalProps {
  modalSetter: React.Dispatch<React.SetStateAction<boolean>>;
}
export const DataFrameModal = (props: DataFrameModalProps) => {
  // Dummy table
  const dataPrev = {
    dataSchema: [
      { colName: "Pricing", colType: "Numerical" },
      { colName: "Status", colType: "Categorical" },
      { colName: "When", colType: "Timestamp" },
      { colName: "Pricing", colType: "Numerical" },
      { colName: "Status", colType: "Categorical" },
      { colName: "When", colType: "Timestamp" },
      { colName: "Pricing", colType: "Numerical" },
      { colName: "Status", colType: "Categorical" },
      { colName: "When", colType: "Timestamp" },
      { colName: "Pricing", colType: "Numerical" },
      { colName: "Status", colType: "Categorical" },
      { colName: "When", colType: "Timestamp" },
      { colName: "Pricing", colType: "Numerical" },
      { colName: "Status", colType: "Categorical" },
      { colName: "When", colType: "Timestamp" },
      { colName: "Pricing", colType: "Numerical" },
      { colName: "Status", colType: "Categorical" },
      { colName: "When", colType: "Timestamp" },
      { colName: "Pricing", colType: "Numerical" },
      { colName: "Status", colType: "Categorical" },
      { colName: "When", colType: "Timestamp" },
    ],
    dataValues: [
      [
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
      ],
      [
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
      ],
      [
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
      ],
      [
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
      ],
      [
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
      ],
      [
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
      ],
      [
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
      ],
      [
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
      ],
      [
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
      ],
      [
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
        "9.31",
        "Delivered",
        "2021-04-08 12:21:57.001",
      ],
    ],
  };

  // Render
  return (
    <div className="relative flex h-full w-full flex-col">
      {/* Remove button */}
      <div className="absolute top-2 right-2">
        <CloseButton onClick={() => props.modalSetter(false)} />
      </div>

      {/* Header */}
      <div className="flex justify-around p-2">
        {/* AuthorId */}
        <div className="text-xs italic text-emerald-700">
          <div>#3769</div>
          Elorine
        </div>

        {/* DataFrameId */}
        <div className="relative flex">
          <div className="text-xl font-bold text-emerald-500">Kuku</div>
          <div className="absolute -right-5 text-xs italic text-emerald-700">#11</div>
        </div>

        {/* Downloads & Stars */}
        <div className="flex space-x-2">
          <button onClick={() => {}}>
            <DownloadCount count={0} />
          </button>
          <button onClick={() => {}}>
            <StarCount count={0} />
          </button>
        </div>
      </div>

      {/* Data preview */}
      <div className="overflow-overlay scroll-smooth">
        <table className="table-auto">
          {/* Data prev header */}
          <thead className="bg-gray-800">
            <tr>
              <th key="col#0"></th>
              {dataPrev.dataSchema.map(({ colName, colType }, idxCol) => {
                const idxColStartAtOne = idxCol + 1;
                return (
                  <th className="relative p-2 text-left tracking-wide text-emerald-500" key={`col#${idxColStartAtOne}`}>
                    <div className="text-xs font-thin italic text-emerald-700">#{idxColStartAtOne}</div>
                    <div className="text-sm font-bold">{colName}</div>
                    <div className="text-xs font-light">{colType}</div>
                  </th>
                );
              })}
            </tr>
          </thead>

          {/* Data prev body */}
          <tbody>
            {dataPrev.dataValues.map((rowValues, idxRow) => {
              const idxRowStartAtOne = idxRow + 1;
              return (
                <tr className={idxRow % 2 ? `bg-gray-800` : `bg-gray-900`} key={`row#${idxRowStartAtOne}`}>
                  <td className="p-2 text-xs font-thin italic tracking-wide text-emerald-700" key="col#0">
                    #{idxRowStartAtOne}
                  </td>
                  {rowValues.map((value, idxCol) => (
                    <td className="p-2 text-xs font-medium tracking-wide text-emerald-500" key={`col#${idxCol + 1}`}>
                      {value}
                    </td>
                  ))}
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>

      {/* Footer */}
      <div className="flex justify-around p-2 text-xs italic text-emerald-700">
        <div>{`10/${numberFormatter(12311)} rows`}</div>
        <div>{`21/${numberFormatter(103)} columns`}</div>
      </div>

      {/* Modals */}
      <ConnectionModal />
    </div>
  );
};
