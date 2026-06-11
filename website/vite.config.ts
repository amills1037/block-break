/// <reference types="vitest/config" />
import path from "path";

import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import tailwindcss from "@tailwindcss/vite";

// https://vite.dev/config/
export default defineConfig({
    plugins: [react(), tailwindcss()],
    resolve: {
        alias: {
            "@": path.resolve(__dirname, "./src"),
            "@ui": path.resolve(__dirname, "./src/components/ui"),
        },
    },
    build: {
        rolldownOptions: {
            input: {
                main: path.resolve(__dirname, "index.html"),
                error: path.resolve(__dirname, "error.html"),
            },
        },
    },
    test: {
        globals: true, // Allows using describe, expect, etc. without importing
        environment: "jsdom", // Simulates a browser environment
        setupFiles: "./src/test/setup.ts", // Runs before each test
    },
});
