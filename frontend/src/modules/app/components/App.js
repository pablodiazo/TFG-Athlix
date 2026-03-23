import React, { useEffect } from "react";
import { HashRouter as Router } from "react-router-dom";
import { useDispatch } from "react-redux";
import users from "../../users";
import backend from "../../../backend";

import Header from "./Header";
import Body from "./Body";

const App = () => {

  const dispatch = useDispatch();

  useEffect(() => {
    // Intentar login automático desde el token guardado
    backend.userService.tryLoginFromServiceToken(
      (authenticatedUser) => {
        if (authenticatedUser) {
          dispatch(users.actions.loginCompleted(authenticatedUser));
        }
      },
      () => {
        // Callback si el token falla: hacer logout
        dispatch(users.actions.logout());
      }
    );
  }, [dispatch]);

  return (
    <Router>
      <Header />
      <Body />
    </Router>
  );
};

export default App;
