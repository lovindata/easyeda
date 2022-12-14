import imgProfilPng from "../assets/_old/imgProfil.jpg";
import { ReactComponent as DownloadIconSvg } from "../assets/downloadIcon.svg";

// Profil
export const Profil = () => {
  // Retrieve profil data
  const imgProfil = <img src={imgProfilPng} alt="" className="h-auto w-auto rounded-l-xl" />;
  const idProfil = "3769";
  const nameProfil = "Elorine";
  const downloadsCountProfil = 7366;
  const starsCountProfil = 211;

  // Render
  console.log(imgProfilPng);
  return (
    <div className="flex h-24 w-60 items-center space-x-3 rounded-xl bg-gray-900 drop-shadow">
      {/* Photo */}
      <div className="flex">{imgProfil}</div>

      {/* Infos */}
      <div className="flex flex-col">
        <div>{`@${nameProfil}#${idProfil}`}</div>
        <div className="flex space-x-5">
          <div className="flex">
            {/*<DownloadIconSvg className="h-auto w-auto" />*/}
            {downloadsCountProfil}
          </div>
          <div>{starsCountProfil}</div>
        </div>
      </div>
    </div>
  );
};
