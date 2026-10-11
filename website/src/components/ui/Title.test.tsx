import { render, screen } from "@testing-library/react";
import Title from "./Title";

describe("Title", () => {
  it("renders correctly", () => {
    render(<Title>Hello</Title>);
    expect(screen.getByText(/Hello/i)).toBeInTheDocument();
  });
});
