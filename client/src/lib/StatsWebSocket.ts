/**
 * Stats WebSocket implmentation.
 *
 */

class StatsWebSockets {
    private url: string;

    private webSocket: WebSocket | null;

    private reconnect: boolean;

    private connectionCount: number;

    private socketUUID : string;

    private connectCallback: () => void;

    private errorCallback: (e: Event) => void;

    private setCountCallback: (count: number) => void;

    /**
     * Constructor from StatsWebSocket
     *
     * @param url WebSocket server end point
     * @param setCountCallback Callback to use when the WebSocket server updates the count
     */

    public constructor(url: string, connectCallback: () => void, errorCallback: (e: Event) => void, setCountCallback: (count: number) => void) {
        this.url = url;
        this.connectCallback = connectCallback;
        this.errorCallback = errorCallback;
        this.setCountCallback = setCountCallback;

        this.webSocket = null;
        this.reconnect = true;

        this.connectionCount = 0;
        this.socketUUID = crypto.randomUUID();

        console.log(this.socketUUID + ' constructor');
    }

    /**
     * Connect to the WebSocket server. Returns before connection established.
     *
     */

    public connect(): void {
        this.connectionCount += 1;
        console.log(this.socketUUID + ' connect', this.connectionCount);
        // close the websocket if it is open
        if (this.webSocket !== null) {
            this.webSocket.close(); //noop if already closed
            this.webSocket = null;
        }

        this.webSocket = new WebSocket(this.url);

        //List for WebSocket open
        this.webSocket.addEventListener("open", (event: Event) => {
            console.log(this.socketUUID + ' open', event);
            //Once the connection is "open", send a connect action to the server so it will send response properly
            //This is mostly for AWS API Gateway.  Connect does not add to the sockets list, only action connect.
            this.webSocket?.send('{"action": "connect"}');

            this.connectCallback();
        });

        //Listen for WebSocket messages
        this.webSocket.addEventListener("message", (event: MessageEvent) => {
            // console.log(this.socketUUID + ' message', event);
            const data: { action: string } = JSON.parse(event.data);
            // global is the only message currently.
            if (data.action === "global") {
                const global = data as {
                    action: "global";
                    data: { count: number };
                };

                //Call the
                this.setCountCallback(global.data.count);
            }
        });

        const reconnect = (from: string): void => {
            this.webSocket = null;

            //recconect unless disconnect has been called
            if (this.reconnect) {
                console.log(this.socketUUID + ' reconnect ' + from);
                //reconnect to the websocket
                this.connect();
            }
        };

        //Listen for WebSocket close
        this.webSocket.addEventListener("close", (event: CloseEvent) => {
            console.log(this.socketUUID + ' close', event);
            //We should always call reconnect.  Reconnect will check if the socket should actually try and recoonect.
            //Reconnect after a random amount, don't use raw 2^count, add jitter to prevent connections from reconnecting at the same time
            const timeout = this.connectionCount < 6 ? Math.pow(2, (this.connectionCount-1)) * Math.random() * 1000 : 32000 * Math.random();
            const format = new Intl.NumberFormat(undefined, { maximumFractionDigits: 2 });
            console.log(this.socketUUID + ' reconnect in ' + format.format(timeout));
            setTimeout(() => {
                reconnect('close');
            }, timeout);
        });

        //Listen for WebSocket close
        this.webSocket.addEventListener("error", (event: Event) => {
            console.log(this.socketUUID + ' error', event);
            this.errorCallback(event);

            //error event is mostly followed by close event
            //it may not call close if the socket did not open
            //this edge case warrents further scrutiny
            // if (!this.didOpen) {
            //     reconnect('error');
            // }
        });
    }

    /**
     * Disconenct from the WebSocket server.  Does not reconnect.
     *
     */

    public disconnect(): void {
        console.log(this.socketUUID + ' disconnect');
        //do not try to reconnect, we called disconnect, disconnect is permanent
        this.reconnect = false;

        this.connectCallback = () => {};
        this.errorCallback = () => {};
        this.setCountCallback = () => {};

        this.webSocket?.close(); //noop if already closed
    }

    /**
     * Send a break block message to the WebSocket server.  Does nothing if not connected.
     *
     */

    public breakBlock(): void {
       // console.log(this.socketUUID + ' breakBlock');

        if (this.webSocket?.readyState === WebSocket.OPEN) {
            //console.log(this.socketUUID + " breakBlock: ", "action=breakblock");
            this.webSocket.send('{"action": "breakblock"}');
        }
    }
}

export default StatsWebSockets;
