import { Link } from "react-router-dom";

function LoginForm() {
  return (
    <div className="container flex flex-col bg-slate-700 p-8 space-y-5 rounded">
      <div className="flex flex-col items-center space-y-2">
        <h1 className=" text-white font-bold text-2xl">Hey, welcome back!</h1>
        <p className="text-white opacity-50">We're so excited to see you again!</p>
      </div>
      <div className="flex flex-col space-y-2">
        <label className="text-sm font-semibold opacity-75 flex space-x-1">
          <p className=" text-white">E-MAIL</p>
          <p className="text-red-500">*</p>
        </label>
        <input
          type="text"
          size={48}
          className="bg-slate-800 rounded text-white p-2 focus text-opacity-80 focus:ring-0 border-none"></input>
      </div>
      <div className="flex flex-col space-y-2">
        <label className="text-sm font-semibold opacity-75 flex space-x-1">
          <p className=" text-white">PASSWORD</p>
          <p className="text-red-500">*</p>
        </label>
        <input
          type="password"
          size={48}
          className="bg-slate-800 shadow-inner rounded text-white p-2 focus text-opacity-80 focus:ring-0 border-none"></input>
        <div className="flex">
          <Link to="/login" className="text-sm text-sky-500 hover:underline">
            Password forgotten?
          </Link>
        </div>
      </div>
      <div className="flex flex-col space-y-2">
        <button className=" bg-emerald-500 p-2.5 rounded font-semibold text-white hover:bg-emerald-600 transition-all">
          Connexion
        </button>
        <div className="text-sm text-white flex space-x-1">
          <p className="opacity-50">Need an account? </p>
          <Link to="/register" className="text-sm text-sky-500 hover:underline">
            Sign up
          </Link>
        </div>
      </div>
    </div>
  );
}

export default LoginForm;
