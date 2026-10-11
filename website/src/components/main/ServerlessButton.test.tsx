import { render, screen } from "@testing-library/react";
import ServerlessButton from "./ServerlessButton";

describe("ServerlessButton", () => {
    it("renders correctly", () => {
        render(<ServerlessButton/>);
        expect(screen.getByText(/Serverless/i)).toBeInTheDocument();
    });
});
