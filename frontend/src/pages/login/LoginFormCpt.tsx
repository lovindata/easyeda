import { Link, useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import { ButtonSubmitCpt, PwdInputCpt, TextInputCpt, TitleCpt } from "../../components";
import { TokenDtoOut, UserStatusDtoOut } from "../../data";
import { useGet, usePost } from "../../hooks";
import { useEffect } from "react";
import { useToaster, ToastLevelEnum, useUserContext } from "../../context";

/**
 * Login form.
 */
function LoginFormCpt() {
  // Pre-requisites
  const { register, handleSubmit } = useForm();
  const { setAccessToken, setExpireAt, setRefreshToken } = useUserContext();

  // Effect running on log in changes
  const { post, isLoading, data } = usePost<TokenDtoOut>("/user/login", "TokenDtoOut");
  const { addToast } = useToaster();
  useEffect(() => {
    switch (data?.kind) {
      case "TokenDtoOut":
        setAccessToken(data.accessToken);
        setExpireAt(data.expireAt);
        setRefreshToken(data.refreshToken);
        get({ Authorization: `Bearer ${data.accessToken}` });
        break;
      case "AppException":
        addToast(ToastLevelEnum.Error, "😧 Ooh uh, log in issue?", data.message);
        break;
      default:
        break;
    }
  }, [data]);

  // Effect running on get user info changes
  const { get: get, data: dataUserInfo } = useGet<UserStatusDtoOut>("/user/retrieve", "UserStatusDtoOut");
  const navigate = useNavigate();
  useEffect(() => {
    switch (dataUserInfo?.kind) {
      case "UserStatusDtoOut":
        addToast(
          ToastLevelEnum.Success,
          `🤗 Yay, welcome back ${dataUserInfo.username}!`,
          `${dataUserInfo.email} successfully connected.`
        );
        navigate("/app");
        break;
      default:
        break;
    }
  }, [dataUserInfo]);

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
          <Link
            to="/login"
            className="text-sm text-sky-500 hover:underline"
            onClick={() =>
              addToast(
                ToastLevelEnum.Info,
                "😣 Coming soon!",
                "Admin related features will be available in next releases."
              )
            }
          >
            Password forgotten?
          </Link>
        }
        registerKey={register("pwd")}
      />
      <ButtonSubmitCpt
        name="Connexion"
        isLoading={isLoading}
        extra={
          <div className="flex space-x-1 text-sm brightness-75">
            <p>Need an account? </p>
            <Link to="/register" className="text-sm text-sky-500 hover:underline">
              Sign up
            </Link>
          </div>
        }
      />
    </form>
  );
}

export default LoginFormCpt;
