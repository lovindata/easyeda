import { Routes, Route, useNavigate } from "react-router-dom";
import { HomePg } from "./home/HomePg";
import { ConnsPg } from "./connections/ConnsPg";
import { PipelinesPg } from "./pipelines/PipelinesPg";
import { SideBarCpt } from "./SideBarCpt";
import { useUserContext } from "../../context";
import { useEffect } from "react";

/**
 * Application page.
 */
export function AppPg() {
  // Redirect to login if not connected
  const { accessToken } = useUserContext();
  const navigate = useNavigate();
  useEffect(() => {
    !accessToken && navigate("/login");
  }, []);

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
