interface GlobalCount {
    globalCount: number;
}

function Header({ globalCount }: GlobalCount) {
    return (
        <header className="px-3 py-4 flex gap-2 bg-teal-700 ring ring-gray-900">
            {/* "arrow" from https://fonts.google.com/icons arrow back */}
            <a href="/">
                <svg
                    xmlns="http://www.w3.org/2000/svg"
                    viewBox="0 -960 960 960"
                    fill="currentColor"
                    stroke="currentColor"
                    className="size-6 text-white"
                >
                    <path d="m313-440 224 224-57 56-320-320 320-320 57 56-224 224h487v80H313Z" />
                </svg>
            </a>

            <h1 className="text-white">Global Count {globalCount}</h1>
        </header>
    );
}

export default Header;
