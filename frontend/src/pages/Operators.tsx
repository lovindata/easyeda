import { motion } from "framer-motion";

// Operators
export const Operators = () => (
  <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0, position: "absolute" }}>
    Operators
  </motion.div>
);
