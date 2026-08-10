import { useRef, useState, useEffect } from "react";

import Footer from "@/components/layout/Footer";
import Header from "@/components/layout/Header";
import Main from "@/components/layout/Main";
import Stats from "@/components/backend/Stats";

function App() {
    const statsRef = useRef<{ breakBlock: () => void }>(null!);
    const [count, setCount] = useState(0);

    useEffect(() => {
        const interval = setInterval(() => {
            statsRef.current?.breakBlock()
        }, 1000);

        return () => {
            clearInterval(interval);
        };
    });

    return (
        <>
            <Stats ref={statsRef} setCount={setCount} />

            <Header globalCount={count} />
            <Main />
            <Footer />
        </>
    );
}

export default App;
