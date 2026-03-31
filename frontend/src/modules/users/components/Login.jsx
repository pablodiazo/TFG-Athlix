import { useState, useRef } from "react";
import { useDispatch } from "react-redux";
import { FormattedMessage, useIntl } from "react-intl";
import { useNavigate } from "react-router-dom";

import { Errors } from "../../common";
import * as actions from "../actions";
import backend from "../../../backend";
  
import "../css/Login.css";

const Login = () => {
  const intl = useIntl();
  const dispatch = useDispatch();
  const navigate = useNavigate();
  
  const [userName, setUserName] = useState("");
  const [password, setPassword] = useState("");
  const [backendErrors, setBackendErrors] = useState(null);
  const formRef = useRef(null);

  const handleSignUpButtonClick = (e) => {
    e.preventDefault();
    navigate("/users/signup");
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!formRef.current.checkValidity()) {
      setBackendErrors({
        globalError: intl.formatMessage({
          id: "project.global.errors.formNotFull",
        }),
      });
      formRef.current.classList.add("was-validated");
      return;
    }

    try {
      await backend.userService.login(
        userName,
        password,
        (authenticatedUser) => {
          dispatch(actions.loginCompleted(authenticatedUser));
          const userRole = authenticatedUser.user.role;
           if (userRole === "COACH") {
            navigate("/users/profile");
          } else if (userRole === "USER") {
            navigate("/plans/daily");
          } else {
            navigate("/");
          }
        },
        (errors) => {
          if (errors.globalError === "project.exceptions.IncorrectLoginException") {
            setBackendErrors({
              globalError: intl.formatMessage({
                id: "project.users.exceptions.IncorrectLoginException",
              }),
            });
          } else if (errors.globalError === "project.exceptions.IncorrectPasswordException") {
            setBackendErrors({
              globalError: intl.formatMessage({
                id: "project.users.exceptions.IncorrectPasswordException",
              }),
            });
          } else {
            setBackendErrors(errors);
          }
        },
        () => {
          navigate("/users/login");
          dispatch(actions.logout());
        }
      );
    } catch (err) {
      console.error("Login error", err);
      setBackendErrors({
        globalError: intl.formatMessage({
          id: "project.global.exceptions.NetworkError",
        }),
      });
    }
  };

  return (
    <div className="athlix-login-wrapper">
      <div className="athlix-login-card">
        <h2 className="athlix-login-title">
          <FormattedMessage id="project.users.login.title" />
        </h2>
        
        <form ref={formRef} noValidate onSubmit={handleSubmit} className="athlix-login-form">
          <div className="athlix-input-group">
            <input
              type="text"
              id="userName"
              value={userName}
              onChange={(e) => setUserName(e.target.value)}
              autoFocus
              required
              placeholder={intl.formatMessage({ id: "project.global.fields.userName" })}
              className="athlix-input"
            />
          </div>

          <div className="athlix-input-group">
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              placeholder={intl.formatMessage({ id: "project.global.fields.password" })}
              className="athlix-input"
            />
          </div>

          <div className="athlix-login-actions">
            <button
              type="submit"
              className="athlix-btn athlix-btn-primary athlix-login-submit"
            >
              <FormattedMessage id="project.users.login.title" />
            </button>
            
            <div className="athlix-login-divider">o</div>

            <button
              onClick={handleSignUpButtonClick}
              className="athlix-btn athlix-btn-secondary athlix-login-signup"
            >
              <FormattedMessage id="project.users.signup.title" />
            </button>
          </div>
        </form>

        {backendErrors && (
          <div className="athlix-login-error-container">
            <Errors errors={backendErrors} onClose={() => setBackendErrors(null)} />
          </div>
        )}
      </div>
    </div>
  );
};

export default Login;