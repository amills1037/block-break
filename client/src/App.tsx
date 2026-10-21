import { useRef, useState, useEffect } from "react";

import Footer from "@/components/layout/Footer";
import Header from "@/components/layout/Header";
import Main from "@/components/layout/Main";
import Stats from "@/components/backend/Stats";
/**
 * Main App component
 *
 * @returns components
 */
function App() {
    const statsRef = useRef<{ breakBlock: () => void }>(null!);

    const [count, setCount] = useState(0);
    const [name, setName] = useState('');
    const [error, setError] = useState<string|null>(null);
    const [connected, setConnected] = useState(false);

    useEffect(() => {
        //interval to mock pressing a button inside the web assembply application
        const interval = setInterval(() => {
            statsRef.current?.breakBlock()
        }, 1000);

        return () => {
            clearInterval(interval);
        };
    });

    return (
        <>
            <Stats ref={statsRef} setCount={setCount} setName={setName} setConnected={setConnected} setError={setError} />

            <Header webSocketName={name} globalCount={count} error={error}  />
            <Main connected={ connected } />
            <Footer />
        </>
    );
}

export default App;
