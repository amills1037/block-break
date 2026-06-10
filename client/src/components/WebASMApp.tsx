import { useEffect } from "react";

function WebASMApp() {
    useEffect(() => {
        console.log("WebASM init");

        return () => {
            console.log("WebASM destroy");
        };
    }, []); // Empty array attaches the listener once on mount

    return <canvas id="webasm-canvas"></canvas>;
}

export default WebASMApp;
