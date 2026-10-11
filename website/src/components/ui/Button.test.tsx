import { render, screen } from "@testing-library/react";
import Button from "./Button";

describe("Button", () => {
  it("renders correctly", () => {
    render(<Button href="world">Hello</Button>);
    expect(screen.getByText(/Hello/i)).toBeInTheDocument();
  });
});
