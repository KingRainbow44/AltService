import { defineConfig } from "vite";
import preact from "@preact/preset-vite";
import tsconfigPaths from "vite-tsconfig-paths";

import tailwind from "tailwindcss";
import autoprefixer from "autoprefixer";

import tailwindConfig from "./cfg/tailwind.config.js";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [preact(), tsconfigPaths()],
  css: {
    postcss: {
      plugins: [
        tailwind(tailwindConfig),
        autoprefixer()
      ]
    }
  }
});
