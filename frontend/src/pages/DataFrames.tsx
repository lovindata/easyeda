import { motion } from "framer-motion";

// DataFrames
export const DataFrames = () => {
  // Render
  return (
    <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}>
      DataFrames
    </motion.div>
  );
};
