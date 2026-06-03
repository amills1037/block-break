import React from "react";

interface PProps {
    children: React.ReactNode; // Handles any valid React node
}

function P({ children }: PProps) {
    return <p className="text-gray-500 dark:text-gray-400">{children}</p>;
}

export default P;
