/**
 * Backend component to connect to the WebSocketServer and send and receive messages.
 *
 */

import { useEffect, useImperativeHandle, useRef, type Ref } from "react";

import StatsWebSocket from "@/lib/StatsWebSocket";
import {getWebSocketUrl} from "@/lib/websocketurl";

/**
 * Type to store data to display.
 *
 */

interface StatsInterface {
    ref?: Ref<{ breakBlock: () => void }>;
    setCount: (c: number) => void;
    setName: (s: string) => void;
    setConnected: (connected: boolean) => void;
    setError: (s: string | null) => void;
}

function Stats({ ref, setCount, setName, setConnected, setError }: StatsInterface) {
    //Use refence to prevent redraw when changed
    const sws = useRef<StatsWebSocket>(null!);
    useEffect(() => {
        console.log("Stats useEffect");

        const queryString = window.location.search;
        const urlParams = new URLSearchParams(queryString);
        const db = urlParams.get('db');

        const [url, name] = getWebSocketUrl(db);
        setName(name);

        const connectCallback = () => {
            setError(null);
            setConnected(true);
        };

        const errorCallback = () => {
            setError("Error");
            setConnected(false);
        };

        sws.current = new StatsWebSocket(
            url,
            connectCallback,
            errorCallback,
            (c: number) => {
                setCount(c);
            }
        );

        sws.current.connect();

        return () => {
            sws.current?.disconnect();
        };
    }, [setCount, setName, setConnected, setError]);

    //Code to allow a breakBlock function to be called outside the component.
    useImperativeHandle(ref, () => {
        return {
            breakBlock: () => {
                sws.current?.breakBlock();
            },
        };
    }, []);
    return null;
}

export default Stats;
