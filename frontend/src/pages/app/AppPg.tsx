import { Routes, Route } from "react-router-dom";
import { HomePg } from "./home/HomePg";
import { ConnsPg } from "./connections/ConnsPg";
import { PipelinesPg } from "./pipelines/PipelinesPg";
import { SideBarCpt } from "./SideBarCpt";
import { useUser } from "../../context";

/**
 * Application page.
 */
export function AppPg() {
  // Get user
  const { user, isRetrieving } = useUser();
  // console.log(user);

  // Render
  return (
    <div className="flex">
      <SideBarCpt />
      <Routes>
        <Route path="" element={<HomePg />} />
        <Route path="connections" element={<ConnsPg />} />
        <Route path="pipelines" element={<PipelinesPg />} />
      </Routes>
    </div>
  );
}
