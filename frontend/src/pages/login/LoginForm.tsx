import { Link, useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import { ButtonSubmit, PwdInput, TextInput, Title } from "../../components/form";
import { AppException, TokenDtoOut } from "../../data/dto";
import { useUser } from "../../hooks";
import { useMutation } from "react-query";
import axios from "axios";

/**
 * Login form.
 */
function LoginForm() {
  // Pre-requisites
  const { register, handleSubmit } = useForm();
  const { setAccessToken, setExpireAt, setRefreshToken } = useUser();
  const navigate = useNavigate();
  const {
    mutate: doLogin,
    isLoading,
    data,
  } = useMutation((body: { email: string; pwd: string }) =>
    axios
      .post(`http://${window.location.hostname}:8081/user/login`, body)
      .then((res) => res.data as TokenDtoOut)
      .catch((err) => err.data as AppException)
  );

  switch (data?.kind) {
    case "TokenDtoOut":
      setAccessToken(data.accessToken);
      setExpireAt(data.expireAt);
      setRefreshToken(data.refreshToken);
      navigate("/register");
      break;
    case "AppException":
      alert(data);
      break;
    case undefined:
      break;
  }

  // Handler
  // if (data) {
  //   setAccessToken((data as TokenDtoOut).accessToken);
  //   setExpireAt((data as TokenDtoOut).expireAt);
  //   setRefreshToken((data as TokenDtoOut).refreshToken);
  //   navigate("/register");
  // }

  // Render
  return (
    <form
      className="min-w-max flex flex-col bg-slate-700 p-8 space-y-5 rounded"
      onSubmit={handleSubmit((data) => doLogin({ email: data.email, pwd: data.pwd }))}>
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
        isSubmitting={isLoading}
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
