import init, { main } from "@/wasm/app";

class WebASMApp {
    private canvasId: string;

    public constructor(canvas: HTMLCanvasElement) {
        console.log('canavas.id', canvas.id);

        if (!canvas.hasAttribute('id')) {
            canvas.id = "webasm-canvas";
        }

        this.canvasId = canvas.id;
    }

    public initialized(initCallback: () => void) {
        init().then(() => {
            initCallback();
        }).catch((e) => {
            console.log("e", e);
        });
    }

    public run(): void {
        main(this.canvasId);
    }

    public destroy(): void {
    }
}

export default WebASMApp;
