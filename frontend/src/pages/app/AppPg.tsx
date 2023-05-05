import GenericDisclosureCpt from "./GenericDisclosureCpt";
import SideBarCpt from "./SideBarCpt";
import { ConnsPg } from "./connections/ConnsPg";
import { HomePg } from "./home/HomePg";
import { PipelinesPg } from "./pipelines/PipelinesPg";
import { Route, Routes } from "react-router-dom";

/**
 * Application page.
 */
export default function AppPg() {
  return (
    <div className="flex">
      <SideBarCpt />
      <GenericDisclosureCpt />
      <Routes>
        <Route path="" element={<HomePg />} />
        <Route path="connections" element={<ConnsPg />} />
        <Route path="pipelines" element={<PipelinesPg />} />
      </Routes>
    </div>
  );
}
