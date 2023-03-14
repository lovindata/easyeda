import { Link, useNavigate } from "react-router-dom";
import { ButtonSubmitCpt, DateInputCpt, PwdInputCpt, TextInputCpt, TitleCpt } from "../../components";
import { useForm } from "react-hook-form";
import { usePost } from "../../hooks";
import { UserStatusDtoOut } from "../../data";
import { useToaster, ToastLevelEnum } from "../../context";
import { useEffect } from "react";

/**
 * Registration form.
 */
function RegisterFormCpt() {
  // Pre-requisites
  const { register, handleSubmit } = useForm();
  const { addToast } = useToaster();

  // Effect on post registration
  const navigate = useNavigate();
  const { post, isLoading, data } = usePost<UserStatusDtoOut>("/user/create", "UserStatusDtoOut");
  useEffect(() => {
    switch (data?.kind) {
      case "UserStatusDtoOut":
        addToast(ToastLevelEnum.Success, `😉 Account created!`, `${data.email} ready to connect.`);
        navigate("/login");
        break;
      case "AppException":
        addToast(ToastLevelEnum.Error, "😧 Oh no, registration issue.", data.message);
        break;
      default:
        break;
    }
  }, [data]);

  // Render
  return (
    <form
      className="flex min-w-max flex-col space-y-5 rounded bg-slate-700 p-8"
      onSubmit={handleSubmit((data) => {
        data.isTermsAccepted
          ? !isLoading && post({ email: data.email, username: data.username, pwd: data.pwd, birthDate: data.birthDate })
          : addToast(
              ToastLevelEnum.Error,
              "🤓 Ooh, you missed something...",
              "Terms must be read and accepted for creating an account."
            );
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
        isLoading={isLoading}
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
