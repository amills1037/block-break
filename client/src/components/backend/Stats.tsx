import { useEffect, useImperativeHandle, useRef, type Ref } from "react";

import StatsWebSocket from "@/lib/StatsWebSocket";

interface StatsInterface {
    ref?: Ref<{ breakBlock: () => void }>;
    setCount: (c: number) => void;
}

function Stats({ ref, setCount }: StatsInterface) {
    const sws = useRef<StatsWebSocket>(null!);
    useEffect(() => {
        console.log("Stats useEffect");

        sws.current = new StatsWebSocket(
            "wss://serverless.blockbreak.ca:443",
            (c: number) => console.log("count", c),
        );

        return () => {
            if (sws.current) sws.current.close();
        };
    }, [setCount]);

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
