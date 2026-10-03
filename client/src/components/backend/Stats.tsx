import { useEffect, useImperativeHandle, useRef, type Ref } from "react";

import StatsWebSocket from "@/lib/StatsWebSocket";

interface StatsInterface {
    ref?: Ref<{ breakBlock: () => void }>;
    setCount: (c: number) => void;
    setName: (s: string) => void;
}

function getWebSocketUrl(): [string, string] {
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const db = urlParams.get('db');

  const urls: [string, string][] = [
      ['wss://socket.serverblockbreak.ca:443/mariadb', 'Server/MariaDB'],
      ['wss://socket.serverblockbreak.ca:443/mongodb', 'Server/MongoDB'],
      ['wss://socket.serverblockbreak.ca:443/postgresql', 'Server/PostgreSQL']
  ];

  const r = Math.floor(Math.random() * urls.length);

  switch (db) {
      case '0':
          return urls[r];
      case '1':
          return urls[0];
      case '2':
          return urls[1];
      case '3':
          return urls[2];
      default:
          return ['wss://serverless.blockbreak.ca:443', "Serverless"];
      }
}

function Stats({ ref, setCount, setName }: StatsInterface) {
    const sws = useRef<StatsWebSocket>(null!);
    useEffect(() => {
        console.log("Stats useEffect");

        const [url, name] = getWebSocketUrl();
        setName(name);
        sws.current = new StatsWebSocket(
            url, (c: number) => {
                setCount(c);
            }
        );

        sws.current.connect();

        return () => {
            sws.current?.disconnect();
        };
    }, [setCount, setName]);

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
