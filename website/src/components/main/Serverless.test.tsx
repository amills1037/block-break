import { render, screen } from "@testing-library/react";
import Serverless from "./Serverless";

describe("Serverless", () => {
  it("renders correctly", () => {
    render(<Serverless/>);
    expect(screen.getByText(/Choose this version/i)).toBeInTheDocument();
  });
});
