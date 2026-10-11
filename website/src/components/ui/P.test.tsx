import { render, screen } from "@testing-library/react";
import P from "./P";

describe("P", () => {
  it("renders correctly", () => {
    render(<P>Hello</P>);
    expect(screen.getByText(/Hello/i)).toBeInTheDocument();
  });
});
