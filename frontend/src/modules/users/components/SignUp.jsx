import { useState, useRef } from "react";
import { useDispatch } from "react-redux";
import { FormattedMessage, useIntl } from "react-intl";
import { useNavigate } from "react-router-dom";

import { Errors } from "../../common";
import * as actions from "../actions";
import backend from "../../../backend";

import "../css/SignUp.css";

const SignUp = () => {
  const intl = useIntl();
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const [userName, setUserName] = useState("");
  const [password, setPassword] = useState("");
  const [passConfirm, setPassConfirm] = useState("");
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [email, setEmail] = useState("");
  const [role, setRole] = useState("USER");

  const [backendErrors, setBackendErrors] = useState(null);
  const formRef = useRef(null);

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!formRef.current.checkValidity()) {
      setBackendErrors({
        globalError: intl.formatMessage({ id: "project.global.errors.formNotFull" }),
      });
      formRef.current.classList.add("was-validated");
      return;
    }

    if (password !== passConfirm) {
      setBackendErrors({
        globalError: intl.formatMessage({ id: "project.global.validator.passwordsDoNotMatch" }),
      });
      return;
    }

    const user = { userName, password, firstName, lastName, email, role };

    try {
      await backend.userService.signUp(
        user,
        (authenticatedUser) => {
          dispatch(actions.loginCompleted(authenticatedUser));
          if (role === "COACH") {
            navigate("/users/profile");
          } else {
            navigate("/users/profile");
          }
        },
        (errors) => setBackendErrors(errors),
        () => {
          navigate("/users/login");
          dispatch(actions.logout());
        }
      );
    } catch (err) {
      console.error("SignUp error", err);
      setBackendErrors({
        globalError: intl.formatMessage({ id: "project.global.exceptions.NetworkError" }),
      });
    }
  };

  return (
    <div className="athlix-signup-wrapper">
      <div className="athlix-signup-card">
        <h2 className="athlix-signup-title">
          <FormattedMessage id="project.users.signup.title" defaultMessage="Crear Cuenta" />
        </h2>

        <div className="athlix-role-selector">
          <button
            type="button"
            className={`athlix-role-btn ${role === "USER" ? "active" : ""}`}
            onClick={() => setRole("USER")}
          >
            <FormattedMessage id="project.global.users.roles.athlete" defaultMessage="ATLETA" />
          </button>
          <button
            type="button"
            className={`athlix-role-btn ${role === "COACH" ? "active" : ""}`}
            onClick={() => setRole("COACH")}
          >
            <FormattedMessage id="project.global.users.roles.coach" defaultMessage="ENTRENADOR" />
          </button>
        </div>

        <form ref={formRef} noValidate onSubmit={handleSubmit} className="athlix-signup-form">
          
          <div className="athlix-input-row">
            <div className="athlix-input-group">
              <input
                type="text"
                id="firstName"
                value={firstName}
                onChange={(e) => setFirstName(e.target.value)}
                autoFocus
                required
                placeholder={intl.formatMessage({ id: "project.global.fields.firstName" })}
                className="athlix-input"
              />
            </div>
            <div className="athlix-input-group">
              <input
                type="text"
                id="lastName"
                value={lastName}
                onChange={(e) => setLastName(e.target.value)}
                required
                placeholder={intl.formatMessage({ id: "project.global.fields.lastName" })}
                className="athlix-input"
              />
            </div>
          </div>

          <div className="athlix-input-group">
            <input
              type="email"
              id="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              placeholder={intl.formatMessage({ id: "project.global.fields.email" })}
              className="athlix-input"
            />
          </div>

          <div className="athlix-input-group">
            <input
              type="text"
              id="userName"
              value={userName}
              onChange={(e) => setUserName(e.target.value)}
              required
              placeholder={intl.formatMessage({ id: "project.global.fields.userName" })}
              className="athlix-input"
            />
          </div>

          <div className="athlix-input-row">
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
            <div className="athlix-input-group">
              <input
                type="password"
                id="passConfirm"
                value={passConfirm}
                onChange={(e) => setPassConfirm(e.target.value)}
                required
                placeholder={intl.formatMessage({ id: "project.global.fields.passConfirm", defaultMessage: "Repetir contraseña" })}
                className="athlix-input"
              />
            </div>
          </div>

          <div className="athlix-signup-actions">
            <button type="submit" className="athlix-btn athlix-btn-primary athlix-signup-submit">
              <FormattedMessage id="project.users.signup.title" defaultMessage="Comenzar" />
            </button>
            
            <div className="athlix-signup-footer">
              <span className="athlix-footer-text">
                <FormattedMessage id="project.users.signup.hasAccount" defaultMessage="¿Ya tienes una cuenta?" />
                </span>
              <button
                type="button"
                onClick={() => navigate("/users/login")}
                className="athlix-btn-link"
              >
                <FormattedMessage id="project.users.signup.loginHere" defaultMessage="Inicia sesión aquí" />
              </button>
            </div>
          </div>
        </form>

        {backendErrors && (
          <div className="athlix-signup-error-container">
            <Errors errors={backendErrors} onClose={() => setBackendErrors(null)} />
          </div>
        )}
      </div>
    </div>
  );
};

export default SignUp;