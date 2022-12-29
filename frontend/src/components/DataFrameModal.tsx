import { CloseButton } from "./helpers/InteractsComp";
import { DownloadCount, StarCount } from "./helpers/CountsComp";

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
    <div className="flex h-full w-full flex-col">
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
      <div className="no-scrollbar overflow-auto scroll-smooth">
        <table className="table-auto">
          {/* Data prev header */}
          <thead className="bg-gray-800">
            <tr>
              <th></th>
              {dataPrev.dataSchema.map(({ colName, colType }, index) => (
                <th className="relative p-2 text-left tracking-wide text-emerald-500">
                  <div className="text-xs font-thin italic text-emerald-700">#{index + 1}</div>
                  <div className="text-sm font-bold">{colName}</div>
                  <div className="text-xs font-light">{colType}</div>
                </th>
              ))}
            </tr>
          </thead>

          {/* Data prev body */}
          <tbody>
            {dataPrev.dataValues.map((rowValues, index) => (
              <tr className={index % 2 ? `bg-gray-800` : `bg-gray-900`}>
                <td className="p-2 text-xs font-thin italic tracking-wide text-emerald-700">#{index + 1}</td>
                {rowValues.map((value, _) => (
                  <td className="p-2 text-xs font-medium tracking-wide text-emerald-500">{value}</td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
