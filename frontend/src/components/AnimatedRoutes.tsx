import { Routes, Route, useLocation } from "react-router-dom";
import { DataFrames } from "../pages/DataFrames";
import { Operators } from "../pages/Operators";
import { AnimatePresence } from "framer-motion";

// Routes
export const AnimatedRoutes = () => {
  // Logic
  const location = useLocation();

  // Render
  return (
    <AnimatePresence>
      <Routes location={location} key={location.pathname}>
        <Route path="/dataframes" element={<DataFrames />} />
        <Route path="/operators" element={<Operators />} />
      </Routes>
    </AnimatePresence>
  );
};
