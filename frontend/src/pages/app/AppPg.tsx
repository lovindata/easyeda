import { Routes, Route, useNavigate } from "react-router-dom";
import { HomePg } from "./home/HomePg";
import { ConnsPg } from "./connections/ConnsPg";
import { PipelinesPg } from "./pipelines/PipelinesPg";
import { SideBarCpt } from "./SideBarCpt";
import { useUser, useToaster, ToastLevelEnum } from "../../context";
import { useEffect } from "react";

/**
 * Application page.
 */
export function AppPg() {
  // Redirect to login if not connected
  const { user, isDoomed } = useUser();
  const navigate = useNavigate();
  const { addToast } = useToaster();
  useEffect(() => {
    if (isDoomed) {
      addToast(ToastLevelEnum.Warning, "Not connected", "Connection lost or account not provided.");
      navigate("/login");
    }
  }, [user]);

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
