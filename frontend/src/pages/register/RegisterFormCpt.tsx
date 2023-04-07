import { Link } from "react-router-dom";
import {
  ButtonSubmitCpt,
  DateInputCpt,
  PwdInputCpt,
  TextInputCpt,
  TitleCpt,
} from "../../components";
import { useForm } from "react-hook-form";
import { useUserRtsCreate } from "../../services";

/**
 * Registration form.
 */
function RegisterFormCpt() {
  // Pre-requisites
  const { register, handleSubmit } = useForm();
  const { create, isCreating } = useUserRtsCreate();

  // Render
  return (
    <form
      className="flex min-w-max flex-col space-y-5 rounded bg-neutral p-8"
      onSubmit={handleSubmit((data) =>
        create({
          email: data.email,
          username: data.username,
          pwd: data.pwd,
          birthDate: data.birthDate,
          isTermsAccepted: data.isTermsAccepted,
        })
      )}
    >
      <TitleCpt
        title="Create an account"
        desc="Just a few steps before joining the community!"
      />
      <TextInputCpt
        header="E-MAIL"
        isRequired={true}
        registerKey={register("email")}
      />
      <TextInputCpt
        header="USERNAME"
        isRequired={true}
        registerKey={register("username")}
      />
      <PwdInputCpt
        header="PASSWORD"
        isRequired={true}
        registerKey={register("pwd")}
      />
      <DateInputCpt
        header="BIRTH DATE"
        isRequired={true}
        registerKey={register("birthDate")}
      />
      <div className="flex items-center space-x-2">
        <input
          type="checkbox"
          className="checkbox-primary checkbox bg-base-100"
          {...register("isTermsAccepted")}
        />
        <label className="flex space-x-1 text-xs">
          <p className="text-accent">*</p>
          <p>I have read and accept the</p>
          <a
            href="https://github.com/iLoveDataJjia/easyeda/blob/main/LICENSE"
            className="link-primary link"
          >
            terms of service
          </a>
          <p>and</p>
          <a
            href="https://github.com/iLoveDataJjia/easyeda/blob/main/LICENSE"
            className="link-primary link"
          >
            privacy policy
          </a>
          <p>of DatapiU.</p>
        </label>
      </div>
      <ButtonSubmitCpt
        name="Continue"
        isLoading={isCreating}
        extra={
          <div className="flex space-x-1 text-sm brightness-75">
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

export default RegisterFormCpt;
