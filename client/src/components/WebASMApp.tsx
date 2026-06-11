import { useEffect } from "react";

import app, { main } from "../wasm/app";

function WebASMApp() {
    useEffect(() => {
        console.log("WebASM init");

        const initApp = async () => {
            try {
                await app();

                main();
            } catch (error) {
                console.error("Failed to init app", error);
            }
        };

        initApp();

        return () => {
            console.log("WebASM destroy");
        };
    }, []); // Empty array attaches the listener once on mount

    return <canvas id="webasm-canvas"></canvas>;
}

export default WebASMApp;
