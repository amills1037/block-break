import React from "react";

interface H2Props {
    children: React.ReactNode; // Handles any valid React node
}

function H2({ children }: H2Props) {
    return <h2 className="text-gray-900 dark:text-white">{children}</h2>;
}

export default H2;
