import { SideBar } from "./components/SideBar";
import "./App.css";
import { BrowserRouter } from "react-router-dom";
import { AnimatedRoutes } from "./components/AnimatedRoutes";
import { Connectors } from "./components/Connectors";
import { ProfilInfo } from "./components/ProfilInfo";

function App() {
  // Render
  return (
    <div className="App flex bg-gray-800">
      <BrowserRouter>
        {/* SideBar */}
        <SideBar />

        {/* Page */}
        <div className="flex flex-1 flex-col">
          {/* Header */}
          <div className="flex items-center justify-around p-10">
            <Connectors />
            <ProfilInfo />
          </div>

          {/* Content */}
          <AnimatedRoutes />
        </div>
      </BrowserRouter>
    </div>
  );
}

export default App;
