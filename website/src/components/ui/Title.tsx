import React from "react";

interface TitleProps {
    children: React.ReactNode; // Handles any valid React node
}

function Title({ children }: TitleProps) {
    return <h1 className="text-white">{children}</h1>;
}

export default Title;
