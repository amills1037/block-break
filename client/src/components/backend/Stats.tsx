import { useEffect, useImperativeHandle, useRef, type Ref } from "react";

import StatsWebSocket from "@/lib/StatsWebSocket";

interface StatsInterface {
    ref?: Ref<{ breakBlock: () => void }>;
    setCount: (c: number) => void;
}

function getWebSocketUrl(): string {
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const db = urlParams.get('db');

  const urls = [
      'wss://socket.serverblockbreak:443/mariadb',
      'wss://socket.serverblockbreak:443/mongodb',
      'wss://socket.serverblockbreak:443/postgresql'
  ];

  switch (db) {
      case '0':
          return urls[Math.floor(Math.random()*urls.length)];
      case '1':
          return urls[0];
      case '2':
          return urls[1];
      case '3':
          return urls[2];
      default:
          return 'wss://serverless.blockbreak.ca:443';
      }
}

function Stats({ ref, setCount }: StatsInterface) {
    const sws = useRef<StatsWebSocket>(null!);
    useEffect(() => {
        console.log("Stats useEffect");

        sws.current = new StatsWebSocket(
            getWebSocketUrl(), (c: number) => {
                setCount(c);
            }
        );

        sws.current.connect();

        return () => {
            sws.current?.disconnect();
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
