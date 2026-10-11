import { render, screen } from "@testing-library/react";
import HeroBox from "./HeroBox";

describe("HeroBox", () => {
  it("renders correctly", () => {
    render(<HeroBox>Hello</HeroBox>);
    expect(screen.getByText(/Hello/i)).toBeInTheDocument();
  });
});
