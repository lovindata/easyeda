/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./index.html", "./src/**/*.tsx"],
  theme: {
    extend: {},
  },
  daisyui: {
    themes: ["night"],
  },
  plugins: [require("daisyui")],
};
