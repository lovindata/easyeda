import React from "react";
import { SideBar } from "./components/SideBar";
import "./App.css";

function App() {
  return (
    <div className="App flex bg-gray-700">
      <SideBar />
      <div className="flex flex-1 flex-col"></div>
    </div>
  );
}

export default App;
