import { BrowserRouter as RouterProvider, Routes, Route } from "react-router-dom";
import { LoginPg, RegisterPg, AppPg } from "./pages";
import { CtxProvider, ToasterCpt } from "./context";
import { BackendProvider } from "./hooks";

/**
 * Application entrypoint.
 */
function App() {
  return (
    <div className="flex min-h-screen flex-col bg-slate-900 text-gray-300">
      <CtxProvider>
        <BackendProvider>
          <RouterProvider>
            <Routes>
              <Route path="/login" element={<LoginPg />} />
              <Route path="/register" element={<RegisterPg />} />
              <Route path="/app/*" element={<AppPg />} />
            </Routes>
            <ToasterCpt />
          </RouterProvider>
        </BackendProvider>
      </CtxProvider>
    </div>
  );
}

export default App;
