import { useEffect, useImperativeHandle, useRef } from "react";

function Stats({ ref, setCount }) {
    const ws = useRef(null);
    useEffect(() => {
        ws.current = new WebSocket("wss://serverless.blockbreak.ca:443");
        ws.current.onopen = () => {
            console.log("onopen");
            ws.current.send('{"action": "connect"}');
        };

        ws.current.onclose = () => console.log("onclose");

        ws.current.onmessage = (event: { data: string }) => {
            const data: { action: string } = JSON.parse(event.data);
            if (data.action === "global") {
                const global = data as {
                    action: "global";
                    data: { count: number };
                };

                console.log("global.data.count", global.data.count);

                setCount(global.data.count);
            }
        };

        return () => {
            if (ws.current) ws.current.close();
        };
    }, [setCount]);

    useImperativeHandle(ref, () => {
        return {
            breakBlock: () => {
                if (ws.current?.readyState === WebSocket.OPEN) {
                    console.log("breakBlock: ", "action=breakblock");
                    ws.current?.send('{"action": "breakblock"}');
                }
            },
        };
    }, []);
    return null;
}

export default Stats;
