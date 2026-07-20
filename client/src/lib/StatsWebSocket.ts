class StatsWebSockets {
    private url: string;
    private webSocket: WebSocket;

    private setCountCallback: (count: number) => void;

    constructor(url: string, setCountCallback: (count: number) => void) {
        this.url = url;
        this.setCountCallback = setCountCallback;

        this.webSocket = new WebSocket(url);

        // ws.current.onopen = () => {
        //     console.log("onopen");
        //     ws.current.send('{"action": "connect"}');
        // };

        // ws.current.onclose = () => {
        //     console.log("onclose properClose: ", properClose);
        //     if (!properClose) {
        //         console.log("Restart web socket");
        //     }
        // };

        // ws.current.onmessage = (event: { data: string }) => {
        //     const data: { action: string } = JSON.parse(event.data);
        //     if (data.action === "global") {
        //         const global = data as {
        //             action: "global";
        //             data: { count: number };
        //         };

        //         console.log("global.data.count", global.data.count);

        //         setCount(global.data.count);
        //     }
        // };

        // ws.current.onerror = (e) => {
        //     console.error("e", e);
        //     const readyState = ws.current.readyState;
        //     if (
        //         readyState === WebSocket.OPEN ||
        //         readyState === WebSocket.CONNECTING
        //     ) {
        //         //There is an error, close it and let onclose restart
        //         ws.current.close();
        //     }
        // };
    }

    public connect(): void {}

    onStatusChange;

    onCountChnage;

    // onConnect;
    // onMessage;
    // onClose;
    // onError;

    public breakBlock(): void {
        console.log("StatsWebSocket.close");

        if (this.webSocket?.readyState === WebSocket.OPEN) {
            console.log("breakBlock: ", "action=breakblock");
            this.webSocket?.send('{"action": "breakblock"}');
        }
    }

    public close(): void {
        console.log("StatsWebSocket.close");
    }
}

export default StatsWebSockets;
