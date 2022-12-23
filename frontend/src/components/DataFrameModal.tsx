import { CloseButton } from "./helpers/InteractsComp";
import { DownloadCount, StarCount } from "./helpers/CountsComp";

// DataFrame preview modal
interface DataFrameModalProps {
  modalSetter: React.Dispatch<React.SetStateAction<boolean>>;
}
export const DataFrameModal = (props: DataFrameModalProps) => {
  // Render
  return (
    <div className="flex flex-col p-2">
      {/* Header */}
      <div className="flex justify-around">
        {/* AuthorId */}
        <div className="text-xs italic text-green-700">
          <div>#3769</div>
          Elorine
        </div>

        {/* DataFrameId */}
        <div className="relative flex">
          <div className="text-xl font-bold text-green-500">Kuku</div>
          <div className="absolute -right-5 text-xs italic text-green-700">#11</div>
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

      {/* Remove button */}
      <div className="absolute top-2 right-2 ">
        <CloseButton onClick={() => props.modalSetter(false)} />
      </div>

      {/* Grid */}
    </div>
  );
};
