import { render, screen } from "@testing-library/react";
import Main from "./Main";

describe("Main", () => {
  it("renders correctly", () => {
    render(<Main />);
    expect(screen.getByText(/Handcrafted with care by Anthony Mills./i)).toBeInTheDocument();
  });
});
