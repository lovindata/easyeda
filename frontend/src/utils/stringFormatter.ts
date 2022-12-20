import dayjs from "dayjs";
import relativeTime from "dayjs/plugin/relativeTime";
dayjs.extend(relativeTime);

/**
 * Compute the string representation for a number.
 * @param x Number
 * @returns String representation with suffixes "K", "M", "B" and "T" (ex: `12.3K` for `12311`)
 */
export const numberFormatter = (x: number) =>
  Intl.NumberFormat("en-US", {
    notation: "compact",
    maximumFractionDigits: 1,
  }).format(x);

/**
 * Compute the string representation for a unix timestamp in seconds.
 * @param x Unix timestamp in seconds
 * @returns An example of value is "3 days ago" (for all please check https://day.js.org/docs/en/display/from-now)
 */
export const unixTimestampFormatter = (x: number) => dayjs(x * 1000).fromNow();

/**
 * Compute the string representation for bytes.
 * @param bytes Bytes
 * @returns Example of values are "1.7KB", "7.4TB"
 */
export const bytesToSizeFormatter = (bytes: number) => {
  const sizes = ["Bytes", "KB", "MB", "GB", "TB"];
  if (bytes === 0) return "n/a";
  const i = Math.min(Math.floor(Math.log(bytes) / Math.log(1024)), sizes.length - 1);
  if (i === 0) return `${bytes} ${sizes[i]}`;
  return `${(bytes / 1024 ** i).toFixed(1)} ${sizes[i]}`;
};
