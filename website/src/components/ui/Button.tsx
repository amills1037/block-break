import React from "react";

interface ButtonProps {
    children: React.ReactNode; // Handles any valid React node
    href: string;
}

function Button({ children, href }: ButtonProps) {
    return (
        <a href={href}>
            <button className="w-60 bg-sky-700 text-gray-200 rounded-xl ring ring-gray-900/5">
                {children}
            </button>
        </a>
    );
}

export default Button;
