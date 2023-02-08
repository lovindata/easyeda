import imgProfilPng from "../assets/_old/imgProfil.png";
import { ReactComponent as LinkedInSvg } from "../assets/medias/linkedin.svg";
import { ReactComponent as GithubSvg } from "../assets/medias/github.svg";
import { ReactComponent as KaggleSvg } from "../assets/medias/kaggle.svg";
import { Link } from "react-router-dom";
import { DownloadCount, StarCount } from "./helpers/CountsComp";

// Profil information
export const ProfilInfo = () => {
  // Retrieve profil data
  const profil = {
    id: "3769",
    name: "Elorine",
    img: <img src={imgProfilPng} alt="" className="h-full rounded-l-xl" />,
    downloadCount: 7101366,
    startCount: 21133,
    linkedinHref: "https://www.linkedin.com/in/james-jiang-87306b155/",
    githubHref: "https://github.com/iLoveDataJjia",
    kaggleHref: "https://www.kaggle.com/ilovedatajjia",
  };

  // Render
  return (
    <div className="flex h-24 w-72 rounded-xl bg-gray-900 drop-shadow-md">
      {/* Photo */}
      <Link to="/profil">{profil.img}</Link>

      {/* Infos */}
      <div className="flex flex-1 justify-evenly py-4">
        {/* Profil info */}
        <div className="flex flex-col justify-between">
          <div className="group flex w-fit flex-col">
            <Link to="/profil" className="transition-effect text-xs text-emerald-700 group-hover:text-white">
              {`#${profil.id}`}
            </Link>
            <Link
              to="/profil"
              className="transition-effect -mt-1 text-2xl font-bold text-emerald-500 group-hover:text-white">
              {profil.name}
            </Link>
          </div>

          {/* Downloads & Stars */}
          <div className="flex space-x-3">
            <Link to="/profil">
              <DownloadCount count={profil.downloadCount} />
            </Link>
            <Link to="/profil">
              <StarCount count={profil.startCount} />
            </Link>
          </div>
        </div>

        {/* Social media info */}
        <div className="flex flex-col items-center justify-between">
          <a href={profil.linkedinHref} target="_blank" rel="noreferrer">
            <LinkedInSvg className="transition-effect h-4 w-4 fill-emerald-500 hover:fill-white" />
          </a>
          <a href={profil.githubHref} target="_blank" rel="noreferrer">
            <GithubSvg className="transition-effect h-4 w-4 fill-emerald-500 hover:fill-white" />
          </a>
          <a href={profil.kaggleHref} target="_blank" rel="noreferrer">
            <KaggleSvg className="transition-effect h-4 w-4 fill-emerald-500 hover:fill-white" />
          </a>
        </div>
      </div>
    </div>
  );
};
