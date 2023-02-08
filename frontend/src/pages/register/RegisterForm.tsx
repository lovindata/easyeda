import { Link } from "react-router-dom";
import { ButtonSubmit, DateInput, PwdInput, TextInput, Title } from "../../components/form/generic";
import { useForm } from "react-hook-form";

function RegisterForm() {
  // Pre-requisites
  const { register, handleSubmit } = useForm();

  // Render
  return (
    <form
      className="min-w-max flex flex-col bg-slate-700 p-8 space-y-5 rounded"
      onSubmit={handleSubmit((data) => console.log(data))}>
      <Title title="Create an account" desc="Just a few steps before joining the community!" />
      <TextInput header="E-MAIL" isRequired={true} registerKey={register("email")} />
      <TextInput header="USERNAME" isRequired={false} registerKey={register("username")} />
      <PwdInput header="PASSWORD" isRequired={true} registerKey={register("pwd")} />
      <DateInput header="BIRTH DATE" isRequired={true} registerKey={register("birthDate")} />
      <div className="flex space-x-2 items-center">
        <input
          type="checkbox"
          className="h-5 w-5 bg-slate-800 rounded border-none focus:ring-offset-0 focus:ring-0 text-emerald-500"
          {...register("isTermsAccepted")}
        />
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
      <ButtonSubmit
        name="Continue"
        extra={
          <div className="text-sm text-white flex space-x-1">
            <p className="opacity-50">Already have an account?</p>
            <Link to="/login" className="text-sm text-sky-500 hover:underline">
              Sign in
            </Link>
          </div>
        }
      />
    </form>
  );
}

export default RegisterForm;
