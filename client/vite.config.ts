/// <reference types="vitest/config" />
import path from "path";

import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import tailwindcss from "@tailwindcss/vite";

// https://vite.dev/config/
export default defineConfig({
    plugins: [react(), tailwindcss()],
    base: "/client/",
    resolve: {
        alias: {
            "@": path.resolve(__dirname, "./src"),
            "@ui": path.resolve(__dirname, "./src/components/ui"),
        },
    },
    test: {
        globals: true, // Allows using describe, expect, etc. without importing
        environment: "jsdom", // Simulates a browser environment
        setupFiles: "./src/test/setup.ts", // Runs before each test
    },
});
