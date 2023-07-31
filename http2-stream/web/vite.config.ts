import { defineConfig } from "vite";

export default defineConfig({
  build: {
    lib: {
      name: "web",
      entry: "src/app.ts",
      formats: ["umd"],
      fileName: () => "bundle.js",
    },
  },
  server: {
    proxy: {
      "/api": {
        target: "http://localhost:5000",
        rewrite: (path) => path.replace(/^\/api/, ""),
      },
    },
  },
});
