import { BrowserRouter as RouterProvider, Routes, Route } from "react-router-dom";
import { LoginPg, RegisterPg, AppPg } from "./pages";
import { CtxProvider, ToasterCpt } from "./context";
import { BackendProvider } from "./services";

/**
 * Application entrypoint.
 */
function App() {
  return (
    <div className="flex min-h-screen flex-col">
      <RouterProvider>
        <CtxProvider>
          <BackendProvider>
            <Routes>
              <Route path="/login" element={<LoginPg />} />
              <Route path="/register" element={<RegisterPg />} />
              <Route path="/app/*" element={<AppPg />} />
            </Routes>
            <ToasterCpt />
          </BackendProvider>
        </CtxProvider>
      </RouterProvider>
    </div>
  );
}

export default App;
