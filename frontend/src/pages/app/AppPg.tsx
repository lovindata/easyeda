import { Route, Routes } from "react-router-dom";
import { ConnsPg } from "./connections/ConnsPg";
import { HomePg } from "./home/HomePg";
import MenuBarCpt from "./MenuBarCpt";
import { PipelinesPg } from "./pipelines/PipelinesPg";
import SideBarCpt from "./SideBarCpt";

/**
 * Application page.
 */
export default function AppPg() {
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
