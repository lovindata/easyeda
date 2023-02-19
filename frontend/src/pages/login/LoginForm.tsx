import { Link, useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import { ButtonSubmit, PwdInput, TextInput, Title } from "../../components";
import { TokenDtoOut } from "../../data";
import { usePost } from "../../hooks";
import { useEffect } from "react";
import { useUser } from "../../context";

/**
 * Login form.
 */
function LoginForm() {
  // States
  const { register, handleSubmit } = useForm();
  const { setAccessToken, setExpireAt, setRefreshToken } = useUser();
  const navigate = useNavigate();
  const { post, isLoading, data } = usePost<TokenDtoOut>("/user/login", "TokenDtoOut");

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
      case undefined:
        console.log(data);
        setAccessToken(undefined);
        setExpireAt(undefined);
        setRefreshToken(undefined);
        break;
    }
  }, [data]);

  // Render
  return (
    <form
      className="min-w-max flex flex-col bg-slate-700 p-8 space-y-5 rounded"
      onSubmit={handleSubmit((data) => post({ email: data.email, pwd: data.pwd }))}>
      <Title title="Hey, welcome back!" desc="We're so excited to see you again!" />
      <TextInput header="E-MAIL" isRequired={true} registerKey={register("email")} />
      <PwdInput
        header="PASSWORD"
        isRequired={true}
        extra={
          <Link to="/login" className="text-sm text-sky-500 hover:underline">
            Password forgotten?
          </Link>
        }
        registerKey={register("pwd")}
      />
      <ButtonSubmit
        name="Connexion"
        isLoading={isLoading}
        extra={
          <div className="text-sm text-white flex space-x-1">
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
