import { Routes, Route, useLocation } from "react-router-dom";
import { DataFrames } from "../pages/DataFrames";
import { Ranks } from "../pages/Ranks";
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
        <Route path="/ranks" element={<Ranks />} />
      </Routes>
    </AnimatePresence>
  );
};
