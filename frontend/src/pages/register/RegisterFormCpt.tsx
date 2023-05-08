import { useUserRtsCreate } from "../../api/routes/UserRtsHk";
import { ButtonSubmitCpt, PwdInputCpt, TextInputCpt, TitleCpt, DateInputCpt } from "../../helpers/FormUtils";
import { useForm } from "react-hook-form";
import { Link } from "react-router-dom";

/**
 * Registration form.
 */
export default function RegisterFormCpt() {
  // Pre-requisites
  const { register, handleSubmit } = useForm();
  const { create, isCreating } = useUserRtsCreate();

  // Render
  return (
    <form
      className="flex min-w-max flex-col space-y-5 rounded bg-neutral p-8 text-neutral-content shadow"
      onSubmit={handleSubmit((data) =>
        create({
          email: data.email,
          pwd: data.pwd,
          username: data.username,
          birthDate: data.birthDate,
          isTermsAccepted: data.isTermsAccepted,
        })
      )}
    >
      <TitleCpt title="Create an account" desc="Just a few steps before joining the community!" />
      <TextInputCpt header="E-MAIL" isRequired={true} registerKey={register("email")} />
      <PwdInputCpt header="PASSWORD" isRequired={true} registerKey={register("pwd")} />
      <TextInputCpt header="USERNAME" isRequired={true} registerKey={register("username")} />
      <DateInputCpt header="BIRTH DATE" isRequired={true} registerKey={register("birthDate")} />
      <div className="flex items-center space-x-2">
        <input
          type="checkbox"
          className="checkbox-primary checkbox bg-neutral-focus"
          {...register("isTermsAccepted")}
        />
        <label className="flex space-x-1 text-xs">
          <p className="text-accent">*</p>
          <p>I have read and accept the</p>
          <a href="https://github.com/iLoveDataJjia/easyeda/blob/main/LICENSE" className="link-primary link">
            terms of service
          </a>
          <p>and</p>
          <a href="https://github.com/iLoveDataJjia/easyeda/blob/main/LICENSE" className="link-primary link">
            privacy policy
          </a>
          <p>of DatapiU.</p>
        </label>
      </div>
      <ButtonSubmitCpt
        name="Continue"
        isLoading={isCreating}
        extra={
          <div className="flex space-x-1 text-sm contrast-50">
            <p>Already have an account?</p>
            <Link to="/login" className="link-primary link text-sm">
              Sign in
            </Link>
          </div>
        }
      />
    </form>
  );
}
