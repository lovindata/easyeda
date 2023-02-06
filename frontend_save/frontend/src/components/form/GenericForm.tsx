type IField

interface IField {
  dataKey: string;
  header: string;
  required: boolean;
  link?: JSX.Element;
}

interface PForm<A extends object> {
  title: string;
  description: string;
  data: A;
  fields: IField[];
  submitName: string;
  submitDescription: JSX.Element;
  submitHandler: (_: A) => void;
}

function GenericForm<A extends object>(props: PForm<A>) {
  const test = Object.keys(props.data);

  return <div></div>;
}

export default GenericForm;
