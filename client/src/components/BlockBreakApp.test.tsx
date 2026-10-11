import { render } from "@testing-library/react";
import BlockBreakApp from "./BlockBreakApp";

describe("BlockBreakApp", () => {
  it("renders correctly", () => {
      render(<BlockBreakApp />);
      //Should not error
  });
});
