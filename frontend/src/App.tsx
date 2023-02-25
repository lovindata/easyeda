import { BrowserRouter as RouterProvider, Routes, Route } from "react-router-dom";
import { LoginPg, RegisterPg } from "./pages";
import { CtxProvider, ToasterCpt } from "./context";
import { HttpProvider } from "./hooks";

/**
 * Application entrypoint.
 */
function App() {
  return (
    <div className="flex min-h-screen flex-col bg-slate-900 shadow">
      <HttpProvider>
        <CtxProvider>
          <RouterProvider>
            <Routes>
              <Route path="/login" element={<LoginPg />} />
              <Route path="/register" element={<RegisterPg />} />
            </Routes>
            <ToasterCpt />
          </RouterProvider>
        </CtxProvider>
      </HttpProvider>
    </div>
  );
}

export default App;
