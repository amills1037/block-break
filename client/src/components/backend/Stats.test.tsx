import { render } from "@testing-library/react";
import Stats from "./Stats";

describe("Stats", () => {
    it("renders correctly", () => {
        const ref = () => {};

        const setCount = () => {};
        const setName = () => {};

        render(<Stats ref={ ref } setCount={ setCount } setName={ setName } />);
        //Should not error
    });
});
