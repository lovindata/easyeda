import { SideBar } from "./components/SideBar";
import "./App.css";
import { BrowserRouter } from "react-router-dom";
import { AnimatedRoutes } from "./components/AnimatedRoutes";
import { Connectors } from "./components/Connectors";
import { ProfilInfo } from "./components/ProfilInfo";
import { DataFrameModal } from "./components/DataFrameModal";
import { useState } from "react";

function App() {
  // Modal logic
  const [isDataFrameModal, setDataFrameModal] = useState(false);
  const isAnyModal = isDataFrameModal;

  // Render
  return (
    <div className="App flex bg-gray-700">
      <BrowserRouter>
        {/* SideBar */}
        <SideBar />

        {/* Page */}
        <div className="relative flex flex-1 flex-col">
          {/* Header */}
          <div className="flex items-center justify-around p-10">
            <Connectors modalSetter={setDataFrameModal} />
            <ProfilInfo />
          </div>

          {/* Content */}
          <AnimatedRoutes />

          {/* Modal */}
          {isAnyModal && (
            <div className="absolute inset-0 m-auto h-5/6 w-5/6 rounded-xl bg-gray-900 bg-opacity-90 drop-shadow-md">
              <div className="flex flex-1 flex-col">
                {isDataFrameModal && <DataFrameModal modalSetter={setDataFrameModal} />}
              </div>
            </div>
          )}
        </div>
      </BrowserRouter>
    </div>
  );
}

export default App;
