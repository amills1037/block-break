import { render, screen } from "@testing-library/react";
import ErrorApp from "./ErrorApp";

describe("ErrorApp", () => {
  it("renders correctly", () => {
    render(<ErrorApp />);
    expect(screen.getByText(/Error: 404 Not Found/i)).toBeInTheDocument();
  });
});
