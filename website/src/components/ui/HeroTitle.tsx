import React from "react";

interface HeroTitleProps {
    children: React.ReactNode; // Handles any valid React node
}

function HeroTitle({ children }: HeroTitleProps) {
    return (
        <h2 className="text-3xl text-gray-900 dark:text-white">{children}</h2>
    );
}

export default HeroTitle;
