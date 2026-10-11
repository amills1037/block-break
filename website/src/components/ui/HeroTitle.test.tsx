import { render, screen } from "@testing-library/react";
import HeroTitle from "./HeroTitle";

describe("HeroTitle", () => {
  it("renders correctly", () => {
    render(<HeroTitle>Hello</HeroTitle>);
    expect(screen.getByText(/Hello/i)).toBeInTheDocument();
  });
});
