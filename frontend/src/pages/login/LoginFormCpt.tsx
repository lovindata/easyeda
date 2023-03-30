import { Link } from "react-router-dom";
import { useForm } from "react-hook-form";
import { ButtonSubmitCpt, PwdInputCpt, TextInputCpt, TitleCpt } from "../../components";
import { useToaster, ToastLevelEnum, useUserConnectM } from "../../context";

/**
 * Login form.
 */
function LoginFormCpt() {
  // Pre-requisites
  const { addToast } = useToaster();
  const { register, handleSubmit } = useForm();
  const { connectM, isConnecting } = useUserConnectM();

  // Render
  return (
    <form
      className="flex min-w-max flex-col space-y-5 rounded bg-neutral p-8"
      onSubmit={handleSubmit((data) => !isConnecting && connectM(data.email, data.pwd))}
    >
      <TitleCpt title="Hey, welcome back!" desc="We're so excited to see you again!" />
      <TextInputCpt header="E-MAIL" isRequired={true} registerKey={register("email")} />
      <PwdInputCpt
        header="PASSWORD"
        isRequired={true}
        extra={
          <Link
            to="/login"
            className="link-primary link text-sm"
            onClick={() =>
              addToast({
                level: ToastLevelEnum.Info,
                header: "Comming soon",
                message: "Admin related features will be available in next releases.",
              })
            }
          >
            Password forgotten?
          </Link>
        }
        registerKey={register("pwd")}
      />
      <ButtonSubmitCpt
        name="Connexion"
        isLoading={isConnecting}
        extra={
          <div className="flex space-x-1 text-sm brightness-75">
            <p>Need an account? </p>
            <Link to="/register" className="link-primary link text-sm">
              Sign up
            </Link>
          </div>
        }
      />
    </form>
  );
}

export default LoginFormCpt;
