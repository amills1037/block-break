import { render, screen } from "@testing-library/react";
import Error from "./Error";

describe("Error", () => {
  it("renders correctly", () => {
    render(<Error />);
    expect(screen.getByText(/Error: 404 Not Found/i)).toBeInTheDocument();
  });
});
