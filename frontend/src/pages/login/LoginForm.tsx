import { Link } from "react-router-dom";
import { useForm } from "react-hook-form";
import { ButtonSubmit, PwdInput, TextInput, Title } from "../../components/form/generic";

function LoginForm() {
  // Pre-requisites
  const { register, handleSubmit } = useForm();

  // Render
  return (
    <form
      className="min-w-max flex flex-col bg-slate-700 p-8 space-y-5 rounded"
      onSubmit={handleSubmit((data) => console.log(data))}>
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
