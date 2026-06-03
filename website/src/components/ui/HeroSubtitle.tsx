import React from "react";

interface HeroSubtitleProps {
    children: React.ReactNode; // Handles any valid React node
}

function HeroSubtitle({ children }: HeroSubtitleProps) {
    return (
        <h3 className="text-xl text-gray-900 dark:text-white">{children}</h3>
    );
}

export default HeroSubtitle;
