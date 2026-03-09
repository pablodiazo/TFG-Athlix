import "@testing-library/jest-dom";
import { render, screen, fireEvent } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { IntlProvider } from "react-intl";

import Home from "../../modules/app/components/Home";
import messagesEs from "../../i18n/messages/messages_es";

// Mock de useNavigate
const mockedNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => mockedNavigate,
}));

describe("Home", () => {
  const renderComponent = () => {
    return render(
      <IntlProvider locale="es" messages={messagesEs}>
        <MemoryRouter>
          <Home />
        </MemoryRouter>
      </IntlProvider>
    );
  };

  it("Navega al formulario de login al hacer click en el botón", () => {
    renderComponent();

    const button = screen.getByTestId("NewHome_3");
    fireEvent.click(button);

    expect(mockedNavigate).toHaveBeenCalledWith("/users/login");
  });
});
