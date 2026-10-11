import { render, screen } from "@testing-library/react";
import ServerButton from "./ServerButton";

describe("ServerButton", () => {
    it("renders correctly: server", () => {
        render(<ServerButton db="random"/>);
        expect(screen.getByText(/Server/i)).toBeInTheDocument();
    });

    it("renders correctly: MySQL", () => {
        render(<ServerButton db="mysql"/>);
        expect(screen.getByText(/MySQL/i)).toBeInTheDocument();
    });

    it("renders correctly: MongoDB", () => {
        render(<ServerButton db="mongodb"/>);
        expect(screen.getByText(/MongoDB/i)).toBeInTheDocument();
    });

    it("renders correctly: PostgreSQL", () => {
        render(<ServerButton db="postgresql"/>);
        expect(screen.getByText(/PostgreSQL/i)).toBeInTheDocument();
    });
});
