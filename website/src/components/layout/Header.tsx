import Title from "@ui/Title";

function Header() {
    return (
        <header className="px-3 py-4 flex gap-2 bg-teal-700 ring ring-gray-900">
            {/* "logo" from https://fonts.google.com/icons square */}
            <svg
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 -960 960 960"
                fill="currentColor"
                stroke="currentColor"
                className="size-6 text-white"
            >
                <path d="M120-120v-720h720v720H120Zm80-80h560v-560H200v560Zm0 0v-560 560Z" />
            </svg>

            <Title>Block Break</Title>
        </header>
    );
}

export default Header;
