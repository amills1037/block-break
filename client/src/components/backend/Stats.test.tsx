import { render } from "@testing-library/react";
import Stats from "./Stats";

describe("Stats", () => {
    it("renders correctly", () => {
        const ref = () => {};

        const setCount = () => {};
        const setName = () => {};
        const setConnected = () => {};
        const setError = () => {};

        render(<Stats ref={ref} setCount={setCount} setName={setName} setConnected={ setConnected } setError={ setError } />);
        //Should not error
    });
});
