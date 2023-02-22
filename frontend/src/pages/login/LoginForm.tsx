import { Link, useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import { ButtonSubmitCpt, PwdInputCpt, TextInputCpt, TitleCpt } from "../../components";
import { TokenDtoOut } from "../../data";
import { usePost } from "../../hooks";
import { useEffect } from "react";
import { useToaster, ToastLevelEnum, useUser } from "../../context";

/**
 * Login form.
 */
function LoginForm() {
  // States
  const { register, handleSubmit } = useForm();
  const { setAccessToken, setExpireAt, setRefreshToken } = useUser();
  const navigate = useNavigate();
  const { post, isLoading, data } = usePost<TokenDtoOut>("/user/login", "TokenDtoOut");
  const { toasts, addToast } = useToaster();

  // Effect running on `data` change
  useEffect(() => {
    switch (data?.kind) {
      case "TokenDtoOut":
        console.log(data);
        setAccessToken(data.accessToken);
        setExpireAt(data.expireAt);
        setRefreshToken(data.refreshToken);
        // navigate("/");
        break;
      case "AppException":
        addToast({ level: ToastLevelEnum.Error, header: "Oh no, false credentials!", message: data.message });
        setAccessToken(undefined);
        setExpireAt(undefined);
        setRefreshToken(undefined);
        break;
      case undefined:
        setAccessToken(undefined);
        setExpireAt(undefined);
        setRefreshToken(undefined);
        break;
    }
  }, [data]);

  // Render
  return (
    <form
      className="flex min-w-max flex-col space-y-5 rounded bg-slate-700 p-8"
      onSubmit={handleSubmit((data) => !isLoading && post({ email: data.email, pwd: data.pwd }))}
    >
      <TitleCpt title="Hey, welcome back!" desc="We're so excited to see you again!" />
      <TextInputCpt header="E-MAIL" isRequired={true} registerKey={register("email")} />
      <PwdInputCpt
        header="PASSWORD"
        isRequired={true}
        extra={
          <Link to="/login" className="text-sm text-sky-500 hover:underline">
            Password forgotten?
          </Link>
        }
        registerKey={register("pwd")}
      />
      <ButtonSubmitCpt
        name="Connexion"
        isLoading={isLoading}
        extra={
          <div className="flex space-x-1 text-sm text-white">
            <p className="opacity-50">Need an account? </p>
            <Link to="/register" className="text-sm text-sky-500 hover:underline">
              Sign up
            </Link>
          </div>
        }
      />
    </form>
  );
}

export default LoginForm;
