import { SideBar } from "./components/SideBar";
import "./App.css";
import { BrowserRouter } from "react-router-dom";
import { AnimatedRoutes } from "./components/AnimatedRoutes";

function App() {
  // Render
  return (
    <div className="App flex bg-gray-700">
      <BrowserRouter>
        {/* SideBar */}
        <SideBar />

        {/* Page */}
        <div className="flex flex-1 flex-col">
          <AnimatedRoutes />
        </div>
      </BrowserRouter>
    </div>
  );
}

export default App;
