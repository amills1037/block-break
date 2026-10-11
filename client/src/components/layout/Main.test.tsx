import { render } from "@testing-library/react";
import Main from "./Main";

describe("Main", () => {
    it("renders correctly", () => {
        render(<Main />);

        const canvas = document.querySelector('canvas');
        expect(canvas).toBeInTheDocument();
    });
});
