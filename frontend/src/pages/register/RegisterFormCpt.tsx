import { Link } from "react-router-dom";
import { ButtonSubmitCpt, DateInputCpt, PwdInputCpt, TextInputCpt, TitleCpt } from "../../components";
import { useForm } from "react-hook-form";
import { useToaster, ToastLevelEnum, useUserRegisterM } from "../../context";

/**
 * Registration form.
 */
function RegisterFormCpt() {
  // Pre-requisites
  const { register, handleSubmit } = useForm();
  const { addToast } = useToaster();
  const { registerM, isRegisting } = useUserRegisterM();

  // Render
  return (
    <form
      className="flex min-w-max flex-col space-y-5 rounded bg-slate-700 p-8"
      onSubmit={handleSubmit((data) => {
        data.isTermsAccepted
          ? !isRegisting && registerM(data.email, data.username, data.pwd, data.birthDate)
          : addToast(ToastLevelEnum.Error, "Missing field", "Terms must be read and accepted for creating an account.");
      })}
    >
      <TitleCpt title="Create an account" desc="Just a few steps before joining the community!" />
      <TextInputCpt header="E-MAIL" isRequired={true} registerKey={register("email")} />
      <TextInputCpt header="USERNAME" isRequired={true} registerKey={register("username")} />
      <PwdInputCpt header="PASSWORD" isRequired={true} registerKey={register("pwd")} />
      <DateInputCpt header="BIRTH DATE" isRequired={true} registerKey={register("birthDate")} />
      <div className="flex items-center space-x-2">
        <input
          type="checkbox"
          className="h-5 w-5 rounded border-none bg-slate-800 text-emerald-500 focus:ring-0 focus:ring-offset-0"
          {...register("isTermsAccepted")}
        />
        <label className="flex space-x-1 text-xs">
          <p className="text-red-500">*</p>
          <p>I have read and accept the</p>
          <a href="https://github.com/iLoveDataJjia/easyeda/blob/main/LICENSE" className="text-sky-500 hover:underline">
            terms of service
          </a>
          <p>and</p>
          <a href="https://github.com/iLoveDataJjia/easyeda/blob/main/LICENSE" className="text-sky-500 hover:underline">
            privacy policy
          </a>
          <p>of DatapiU.</p>
        </label>
      </div>
      <ButtonSubmitCpt
        name="Continue"
        isLoading={isRegisting}
        extra={
          <div className="flex space-x-1 text-sm brightness-75">
            <p>Already have an account?</p>
            <Link to="/login" className="text-sm text-sky-500 hover:underline">
              Sign in
            </Link>
          </div>
        }
      />
    </form>
  );
}

export default RegisterFormCpt;
