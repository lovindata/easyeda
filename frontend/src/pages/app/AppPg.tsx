import { Routes, Route } from "react-router-dom";
import { HomePg } from "./home/HomePg";
import { ConnsPg } from "./connections/ConnsPg";
import { PipelinesPg } from "./pipelines/PipelinesPg";
import SideBarCpt from "./SideBarCpt";
import MenuBarCpt from "./MenuBarCpt";

/**
 * Application page.
 */
export function AppPg() {
  return (
    <div className="flex">
      <SideBarCpt />
      <MenuBarCpt />
      <Routes>
        <Route path="" element={<HomePg />} />
        <Route path="connections" element={<ConnsPg />} />
        <Route path="pipelines" element={<PipelinesPg />} />
      </Routes>
    </div>
  );
}
