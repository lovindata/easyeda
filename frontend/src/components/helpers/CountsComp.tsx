import { ReactComponent as DownloadIconSvg } from "../../assets/downloadIcon.svg";
import { ReactComponent as StarIconSvg } from "../../assets/starIcon.svg";
import { numberFormatter } from "../../utils/stringFormatter";

/**
 * Download count component.
 */
export const DownloadCount = (props: { count: number }) => (
  <div className="group/downloads flex items-center space-x-1">
    <DownloadIconSvg className="transition-effect h-4 w-4 fill-sky-500 group-hover/downloads:fill-white" />
    <div className="transition-effect text-xs text-sky-500 group-hover/downloads:text-white">
      {numberFormatter(props.count)}
    </div>
  </div>
);

/**
 * Start count component.
 */
export const StarCount = (props: { count: number }) => (
  <div className="group/stars flex items-center space-x-1">
    <StarIconSvg className="transition-effect h-4 w-4 fill-yellow-500 group-hover/stars:fill-white" />
    <div className="transition-effect text-xs text-yellow-500 group-hover/stars:text-white">
      {numberFormatter(props.count)}
    </div>
  </div>
);
