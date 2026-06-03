import React from "react";

interface AProps {
    children: React.ReactNode; // Handles any valid React node
    href: string;
}

function A({ href, children }: AProps) {
    return (
        <a className="text-sm text-sky-700 underlinded" href={href}>
            {children}
        </a>
    );
}

export default A;
