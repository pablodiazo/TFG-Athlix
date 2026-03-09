import React from "react";

import { HashRouter as Router } from "react-router-dom";

import Header from "./Header";
import Body from "./Body";

const App = () => {
  return (
    <Router>
      <Header />
      <Body />
    </Router>
  );
};

export default App;
