import { SideBar } from "./components/SideBar";
import "./App.css";
import { BrowserRouter } from "react-router-dom";
import { AnimatedRoutes } from "./components/AnimatedRoutes";
import { Connectors } from "./components/Connectors";
import { Profil } from "./components/Profil";

function App() {
  // Render
  return (
    <div className="App flex bg-gray-700">
      <BrowserRouter>
        {/* SideBar */}
        <SideBar />

        {/* Page */}
        <div className="flex flex-1 flex-col">
          <div className="flex justify-around p-10">
            <Connectors />
            <Profil />
          </div>
          <AnimatedRoutes />
        </div>
      </BrowserRouter>
    </div>
  );
}

export default App;
