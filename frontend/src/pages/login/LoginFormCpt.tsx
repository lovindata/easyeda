import { useUserRtsLogin } from "../../api/routes/UserRtsHk";
import ButtonSubmitCpt from "../../components/form/ButtonSubmitCpt";
import PwdInputCpt from "../../components/form/PwdInputCpt";
import TextInputCpt from "../../components/form/TextInputCpt";
import TitleCpt from "../../components/form/TitleCpt";
import { ToastLevelEnum } from "../../context/toaster/ToasterCtx";
import useToaster from "../../context/toaster/ToasterHk";
import { useForm } from "react-hook-form";
import { Link } from "react-router-dom";

/**
 * Login form.
 */
export default function LoginFormCpt() {
  // Pre-requisites
  const { addToast } = useToaster();
  const { register, handleSubmit } = useForm();
  const { logIn, isLogingIn } = useUserRtsLogin();

  // Render
  return (
    <form
      className="flex min-w-max flex-col space-y-5 rounded bg-neutral p-8 text-neutral-content shadow"
      onSubmit={handleSubmit((data) => !isLogingIn && logIn({ email: data.email, pwd: data.pwd }))}
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
        isLoading={isLogingIn}
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
