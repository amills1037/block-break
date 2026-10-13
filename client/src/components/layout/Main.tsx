/**
 * Shows the WebAssembly application.
 *
 */

import BlockBreakApp from "@/components/BlockBreakApp";

function Main() {
    return (
        <main className="flex-1 relative bg-white dark:bg-gray-800 shadow-md ring ring-gray-900/5">
            <div className="px-3 py-4 absolute inset-0 size-full">
                <BlockBreakApp />
            </div>
        </main>
    );
}

export default Main;
