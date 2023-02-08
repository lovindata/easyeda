import { motion } from "framer-motion";

// Ranks
export const Ranking = () => (
  <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} className="m-auto">
    Ranks to build UI...
  </motion.div>
);
