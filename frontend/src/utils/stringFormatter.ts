import dayjs from "dayjs";
import relativeTime from "dayjs/plugin/relativeTime";
dayjs.extend(relativeTime);

// Base number
export const numberFormatter = (x: number) =>
  Intl.NumberFormat("en-US", {
    notation: "compact",
    maximumFractionDigits: 1,
  }).format(x);

// Unix timestamp number
export const unixTimestampSecFormatter = (x: number) => dayjs(Date.now() - x).fromNow();
