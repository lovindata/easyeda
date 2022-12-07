import dataframes from "../assets/dataframes.png";

export const SideBar = () => {
  // Render
  return (
    <div className="flex flex-col items-center justify-center h-screen bg-slate-700 w-16 drop-shadow-2xl">
      {/* DataFrames */}
      <div className="bg-slate-500 m-2 p-2 hover:bg-stone-300 hover:rounded-xl rounded-full transition-all drop-shadow-2xl">
        <img src={dataframes} alt="" className="cursor-pointer" />
      </div>
    </div>
  );
};
