import { BrowserRouter, Routes, Route } from "react-router-dom";
import { LoginPg, RegisterPg } from "./pages";
import { CtxProvider } from "./context";
import { HttpProvider } from "./hooks";

function App() {
  return (
    <HttpProvider>
      <CtxProvider>
        <div className="App bg-slate-900 min-h-screen shadow flex flex-col">
          <BrowserRouter>
            <Routes>
              <Route path="/login" element={<LoginPg />} />
              <Route path="/register" element={<RegisterPg />} />
            </Routes>
          </BrowserRouter>
        </div>
      </CtxProvider>
    </HttpProvider>
  );
}

export default App;
