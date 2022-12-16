import { motion } from "framer-motion";

// Ranks
export const Ranking = () => (
  <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}>
    Ranks to build UI...
  </motion.div>
);
