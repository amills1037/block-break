import React from "react";

interface HeroBoxProps {
    children: React.ReactNode; // Handles any valid React node
}

function HeroBox({ children }: HeroBoxProps) {
    return (
        <div className="w-xs flex flex-col rounded-md px-6 py-8 ring shadow-md ring-gray-900/5 bg-white dark:bg-gray-950/50">
            {children}
        </div>
    );
}

export default HeroBox;
