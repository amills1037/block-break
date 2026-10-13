/**
 * Initializes the WebAssembly application and shows the canvas.
 */

import { useEffect, useRef } from "react";

import WebASMApp from '@/lib/WebASMApp';

function BlockBreakApp() {
    const webASMApp = useRef<WebASMApp>(null);

        //Use reference to prevent redraw
        const canvasRef = useRef<HTMLCanvasElement | null>(null);

        useEffect(() => {
            console.log("BlockBreakApp new");
            webASMApp.current = new WebASMApp(canvasRef.current!);

            webASMApp.current.initialized(() => {
                console.log("BlockBreakApp initialized");

                console.log("BlockBreakApp start run");
                webASMApp.current!.run();
                console.log("BlockBreakApp end run");
            });

            console.log("BlockBreakApp init");

            return () => {
              console.log("BlockBreakApp destroy");
              webASMApp.current!.destroy();
            };
        }, []); // Empty array attaches the listener once on mount

    return <canvas ref={canvasRef}></canvas>;
}

export default BlockBreakApp;
