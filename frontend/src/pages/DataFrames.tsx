import { motion } from "framer-motion";

// DataFrames
export const DataFrames = () => (
  <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0, position: "absolute" }}>
    DataFrames
  </motion.div>
);
