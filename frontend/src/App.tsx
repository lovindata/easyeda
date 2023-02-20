import { BrowserRouter, Routes, Route } from "react-router-dom";
import { LoginPg, RegisterPg } from "./pages";
import { CtxProvider } from "./context";
import { HttpProvider } from "./hooks";

/**
 * Application entrypoint.
 */
function App() {
  return (
    <div className="bg-slate-900 min-h-screen shadow flex flex-col">
      <HttpProvider>
        <CtxProvider>
          <BrowserRouter>
            <Routes>
              <Route path="/login" element={<LoginPg />} />
              <Route path="/register" element={<RegisterPg />} />
            </Routes>
          </BrowserRouter>
        </CtxProvider>
      </HttpProvider>
    </div>
  );
}

export default App;
