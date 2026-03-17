import { useState, useRef } from "react";
import { useSelector, useDispatch } from "react-redux";
import { FormattedMessage, useIntl } from "react-intl";
import { useNavigate } from "react-router-dom";

import { Errors } from "../../common";
import * as actions from "../actions";
import users from "../../users";
import backend from "../../../backend";

import "../css/SignUp.css";

const UpdateProfile = () => {
  const intl = useIntl();
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const userId = useSelector(users.selectors.getUserId);

  const [userName, setUserName] = useState(useSelector(users.selectors.getUserName));
  const [firstName, setFirstName] = useState(useSelector(users.selectors.getFirstName));
  const [lastName, setLastName] = useState(useSelector(users.selectors.getLastName));
  const [email, setEmail] = useState(useSelector(users.selectors.getEmail));
  const [role, setRole] = useState(useSelector(users.selectors.getRole));

  const [backendErrors, setBackendErrors] = useState(null);
  const formRef = useRef(null);

  const handleSubmit = (event) => {
    event.preventDefault();
    setBackendErrors(null);

    const updatedUser = {
      id: userId,
      password: null,
      userName,
      firstName,
      lastName,
      email,
      role,
    };

    backend.userService.updateProfile(
      updatedUser,
      (result) => {
        dispatch(users.actions.updateProfileCompleted(result));
        navigate("/users/profile");
      },
      (errors) => {
        setBackendErrors(errors);
        if (formRef.current) {
          formRef.current.classList.add("was-validated");
        }
      }
    );
  };

  return (
    <div className="athlix-signup-wrapper">
      <div className="athlix-signup-card">
        <h2 className="athlix-signup-title">
          <FormattedMessage id="project.users.updateProfile.title" defaultMessage="Actualizar perfil" />
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

          <div className="athlix-signup-actions">
            <button type="submit" className="athlix-btn athlix-btn-primary athlix-signup-submit">
              <FormattedMessage id="project.global.buttons.update" defaultMessage="Actualizar" />
            </button>
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

export default UpdateProfile;