import { motion } from "framer-motion";
import { DataFrameInfo } from "../components/DataFrameInfo";

// DataFrames
export const DataFrames = () => {
  // Render
  return (
    <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} className="m-auto">
      <DataFrameInfo />
    </motion.div>
  );
};
