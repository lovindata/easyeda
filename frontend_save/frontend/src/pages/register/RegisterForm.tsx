import { Link } from "react-router-dom";

function RegisterForm() {
  return (
    <div className="container flex flex-col bg-slate-700 p-8 space-y-5 rounded">
      <div className="flex flex-col items-center space-y-2">
        <h1 className=" text-white font-bold text-2xl">Create an account</h1>
        <p className="text-white opacity-50">Just a few steps before joining the community!</p>
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
        <label className="text-sm font-semibold opacity-75">
          <p className=" text-white">USERNAME</p>
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
      </div>
      <div className="flex flex-col space-y-2">
        <label className="text-sm font-semibold opacity-75 flex space-x-1">
          <p className=" text-white">BIRTH DATE</p>
          <p className="text-red-500">*</p>
        </label>
        <input
          type="date"
          size={48}
          className="bg-slate-800 shadow-inner rounded text-white p-2 focus text-opacity-80 focus:ring-0 border-none"></input>
      </div>
      <div className="flex space-x-2 items-center pb-2 pt-4">
        <input
          type="checkbox"
          className="h-5 w-5 bg-slate-800 rounded border-none focus:ring-offset-0 focus:ring-0 text-emerald-500"></input>
        <label className="text-xs opacity-75 flex space-x-1">
          <p className="text-red-500">*</p>
          <p className=" text-white">I have read and accept the</p>
          <Link to="/register" className="text-sky-500 hover:underline">
            terms of service
          </Link>
          <p className=" text-white">and</p>
          <Link to="/register" className="text-sky-500 hover:underline">
            privacy policy
          </Link>
          <p className=" text-white">of DatapiU.</p>
        </label>
      </div>
      <div className="flex flex-col space-y-2">
        <button className=" bg-emerald-500 p-2.5 rounded font-semibold text-white hover:bg-emerald-600 transition-all">
          Continue
        </button>
        <div className="text-sm text-white flex space-x-1">
          <p className="opacity-50">Already have an account?</p>
          <Link to="/login" className="text-sm text-sky-500 hover:underline">
            Sign in
          </Link>
        </div>
      </div>
    </div>
  );
}

export default RegisterForm;
