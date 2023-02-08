import { Routes, Route, useLocation } from "react-router-dom";
import { DataFrames } from "../pages/DataFrames";
import { Ranking } from "../pages/Ranking";
import { AnimatePresence } from "framer-motion";
import { Profil } from "../pages/Profil";

// Routes
export const AnimatedRoutes = () => {
  // Logic
  const location = useLocation();

  // Render
  return (
    <AnimatePresence>
      <Routes location={location} key={location.pathname}>
        <Route path="/dataframes" element={<DataFrames />} />
        <Route path="/ranking" element={<Ranking />} />
        <Route path="/profil" element={<Profil />} />
      </Routes>
    </AnimatePresence>
  );
};
