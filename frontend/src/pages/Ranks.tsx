import { motion } from "framer-motion";

// Ranks
export const Ranks = () => (
  <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}>
    Ranks
  </motion.div>
);
