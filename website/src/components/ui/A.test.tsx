import { render, screen } from "@testing-library/react";
import A from "./A";

describe("A", () => {
  it("renders correctly", () => {
    render(<A href="world">Hello</A>);
    expect(screen.getByText(/Hello/i)).toBeInTheDocument();
  });
});
