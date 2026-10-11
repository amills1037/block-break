import { render, screen } from "@testing-library/react";
import Server from "./Server";

describe("Server", () => {
  it("renders correctly", () => {
    render(<Server/>);
    expect(screen.getByText(/Choose this version/i)).toBeInTheDocument();
  });
});
