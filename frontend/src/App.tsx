import GenericRtsPvd from "./api/routes/GenericRtsPvd";
import AuthPvd from "./context/auth/AuthPvd";
import ToasterCpt from "./context/toaster/ToasterCpt";
import ToasterPvd from "./context/toaster/ToasterPvd";
import AppPg from "./pages/app/AppPg";
import LoginPg from "./pages/login/LoginPg";
import RegisterPg from "./pages/register/RegisterPg";
import { BrowserRouter as RouterPvd, Route, Routes } from "react-router-dom";

/**
 * Application entrypoint.
 */
export default function App() {
  return (
    <div className="flex min-h-screen flex-col">
      <ToasterPvd>
        <AuthPvd>
          <GenericRtsPvd>
            <RouterPvd>
              <Routes>
                <Route path="/login" element={<LoginPg />} />
                <Route path="/register" element={<RegisterPg />} />
                <Route path="/app/*" element={<AppPg />} />
              </Routes>
              <ToasterCpt />
            </RouterPvd>
          </GenericRtsPvd>
        </AuthPvd>
      </ToasterPvd>
    </div>
  );
}
