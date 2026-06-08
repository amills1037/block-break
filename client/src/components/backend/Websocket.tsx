// import { useEffect, useRef, useState } from "react";

export function ChatComponent() {
    // const [messages, setMessages] = useState([]);
    // const [input, setInput] = useState("");
    // const ws = useRef(Websocket);
    // useEffect(() => {
    //     // 1. Establish connection
    //     ws.current = new WebSocket("ws://localhost:8080");
    //     // 2. Listeners
    //     ws.current.onopen = () => console.log("Connected");
    //     ws.current.onclose = () => console.log("Disconnected");
    //     ws.current.onmessage = (event) => {
    //         const data = JSON.parse(event.data);
    //         setMessages((prev) => [...prev, data]);
    //     };
    //     // 3. Clean up on unmount
    //     return () => {
    //         if (ws.current) ws.current.close();
    //     };
    // }, []);
    // const sendMessage = () => {
    //     if (input.trim() && ws.current?.readyState === WebSocket.OPEN) {
    //         ws.current.send(JSON.stringify({ text: input }));
    //         setInput("");
    //     }
    // };
    // return (
    //     <div>
    //         <input value={input} onChange={(e) => setInput(e.target.value)} />
    //         <button onClick={sendMessage}>Send</button>
    //         <ul>
    //             {messages.map((m, i) => (
    //                 <li key={i}>{m.text}</li>
    //             ))}
    //         </ul>
    //     </div>
    // );
}
