import imgProfilPng from "../assets/_old/imgProfil.png";
import { ReactComponent as DownloadIconSvg } from "../assets/downloadIcon.svg";
import { ReactComponent as StarIconSvg } from "../assets/starIcon.svg";
import { ReactComponent as LinkedInSvg } from "../assets/medias/linkedin.svg";
import { ReactComponent as GithubSvg } from "../assets/medias/github.svg";
import { ReactComponent as KaggleSvg } from "../assets/medias/kaggle.svg";
import { Link } from "react-router-dom";

// Profil
export const ProfilInfo = () => {
  // Retrieve profil data
  const imgProfil = <img src={imgProfilPng} alt="" className="h-24 w-24 rounded-l-xl" />;
  const idProfil = "3769";
  const nameProfil = "Elorine";
  const downloadsCountProfil = 7366;
  const starsCountProfil = 211;
  const linkedInHref = "https://www.linkedin.com/in/james-jiang-87306b155/";
  const githubHref = "https://github.com/iLoveDataJjia";
  const kaggleHref = "https://www.kaggle.com/ilovedatajjia";

  // Formatter
  const numberFormatter = Intl.NumberFormat("en-US", {
    notation: "compact",
    maximumFractionDigits: 1,
  }).format;

  // Render
  return (
    <div
      className="group flex h-24 w-72 select-none items-center rounded-xl
     bg-gray-900 drop-shadow-md transition-all duration-200 ease-linear hover:bg-green-500">
      {/* Photo */}
      <Link to="/profil" className="h-24">
        {imgProfil}
      </Link>

      {/* Infos */}
      <div className="flex flex-1 justify-evenly">
        {/* Profil info */}
        <div className="flex flex-col">
          {/* Name */}
          <div className="flex">
            <Link to="/profil" className="text-xs text-green-500 group-hover:text-white">{`#${idProfil}`}</Link>
          </div>
          <div className="flex">
            <Link to="/profil" className="text-2xl font-bold text-green-500 group-hover:text-white">
              {nameProfil}
            </Link>
          </div>

          {/* Downloads & Stars */}
          <div className="flex space-x-3">
            <Link to="/profil" className="flex items-center space-x-1">
              <DownloadIconSvg className="h-4 w-4 fill-green-500 group-hover:fill-white" />
              <div className="text-xs text-green-500 group-hover:text-white">
                {numberFormatter(downloadsCountProfil)}
              </div>
            </Link>
            <Link to="/profil" className="flex items-center space-x-1">
              <StarIconSvg className="h-4 w-4 fill-green-500 group-hover:fill-white" />
              <div className="text-xs text-green-500 group-hover:text-white">{numberFormatter(starsCountProfil)}</div>
            </Link>
          </div>
        </div>

        {/* Social media info */}
        <div className="flex flex-col justify-between">
          <a href={linkedInHref} target="_blank" rel="noreferrer">
            <LinkedInSvg className="h-4 w-4 fill-green-500 group-hover:fill-white" />
          </a>
          <a href={githubHref} target="_blank" rel="noreferrer">
            <GithubSvg className="h-4 w-4 fill-green-500 group-hover:fill-white" />
          </a>
          <a href={kaggleHref} target="_blank" rel="noreferrer">
            <KaggleSvg className="h-4 w-4 fill-green-500 group-hover:fill-white" />
          </a>
        </div>
      </div>
    </div>
  );
};
