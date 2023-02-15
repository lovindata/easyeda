import { BrowserRouter, Routes, Route } from "react-router-dom";
import Login from "./pages/login";
import Register from "./pages/register";
import CtxProvider from "./context";
import { HttpProvider } from "./hooks/HttpHk";

function App() {
  return (
    <HttpProvider>
      <CtxProvider>
        <div className="App bg-slate-900 min-h-screen shadow flex flex-col">
          <BrowserRouter>
            <Routes>
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
            </Routes>
          </BrowserRouter>
        </div>
      </CtxProvider>
    </HttpProvider>
  );
}

export default App;
