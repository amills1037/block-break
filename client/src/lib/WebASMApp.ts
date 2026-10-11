/**
 * WebASMApp implmentation
 * Wrapper class to init and call the main function on the WebAssembly application.
 *
 */

import init, { main } from "@/wasm/app";

class WebASMApp {
    private canvasId: string;

    /**
     * Constructor from StatsWebSocket
     *
     * @param canvas HTML canvas element to show the web assemply in
     *
     */

    public constructor(canvas: HTMLCanvasElement) {
        console.log('canavas.id', canvas.id);

        //Add an id if one does not exist
        if (!canvas.hasAttribute('id')) {
            canvas.id = "webasm-canvas";
        }

        this.canvasId = canvas.id;
    }

    /**
     * Initialize the WebAssembly application.  Call initCallback with initialized.
     *
     * @param initCallback Callback to call when initialized
     *
     */

    public initialized(initCallback: () => void) {
        init().then(() => {
            initCallback();
        }).catch((e) => {
            console.log("e", e);
        });
    }

    /**
     * Run the web application's main function
     *
     */

    public run(): void {
        main(this.canvasId);
    }

    /**
     * Destroy the web application.  Currently does nothing.
     *
     */

    public destroy(): void {
        //noop
    }
}

export default WebASMApp;
