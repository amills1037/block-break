import { render, screen } from "@testing-library/react";
import HeroSubtitle from "./HeroSubtitle";

describe("HeroSubtitle", () => {
  it("renders correctly", () => {
    render(<HeroSubtitle>Hello</HeroSubtitle>);
    expect(screen.getByText(/Hello/i)).toBeInTheDocument();
  });
});
