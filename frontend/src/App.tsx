import React from "react";
import { SideBar } from "./components/SideBar";
import "./App.css";

function App() {
  return (
    <div className="App flex bg-slate-900">
      <SideBar />
      <div className="flex-1 flex flex-col"></div>
    </div>
  );
}

export default App;
