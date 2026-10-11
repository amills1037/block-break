import { render, screen } from "@testing-library/react";
import Header from "./Header";

describe("Header", () => {
    it("renders correctly", () => {
        render(<Header webSocketName="Hello" globalCount={42} />);
        expect(screen.getByText(/Hello/i)).toBeInTheDocument();
        expect(screen.getByText(/Global Count/i)).toBeInTheDocument();
        expect(screen.getByText(/42/i)).toBeInTheDocument();
    });
});
