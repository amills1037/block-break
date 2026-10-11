import { render, screen } from "@testing-library/react";
import H2 from "./H2";

describe("H2", () => {
  it("renders correctly", () => {
    render(<H2>Hello</H2>);
    expect(screen.getByText(/Hello/i)).toBeInTheDocument();
  });
});
