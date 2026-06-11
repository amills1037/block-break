import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import ErrorApp from "./ErrorApp.tsx";

createRoot(document.getElementById("root")!).render(
    <StrictMode>
        <ErrorApp />
    </StrictMode>,
);
