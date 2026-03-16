import { useState } from "react";
import { useSelector } from "react-redux";
import { FormattedMessage, useIntl } from "react-intl";
import { useNavigate } from "react-router-dom";

import { Errors } from "../../common";
import * as selectors from "../selectors";
import backend from "../../../backend";

import "../css/ChangePassword.css";

const ChangePassword = () => {
  const user = useSelector(selectors.getUser);
  const navigate = useNavigate();
  const intl = useIntl();
  const [oldPassword, setOldPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmNewPassword, setConfirmNewPassword] = useState("");
  const [backendErrors, setBackendErrors] = useState(null);
  const [passwordsDoNotMatch, setPasswordsDoNotMatch] = useState(false);
  let form;
  let confirmNewPasswordInput;

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (form.checkValidity() && checkConfirmNewPassword()) {
      backend.userService.changePassword(
        user.id,
        oldPassword,
        newPassword,
        () => {
          navigate("/");
        },
        (errors) => {
          if (
            errors.globalError ===
            "project.exceptions.IncorrectPasswordException"
          ) {
            setBackendErrors({
              globalError: intl.formatMessage({
                id: "project.users.exceptions.IncorrectPasswordException",
              }),
            });
          } else {
            setBackendErrors({
              globalError: intl.formatMessage({
                id: "project.global.errors.formNotFull",
              }),
            });
          }
        },
      );
    } else {
      setBackendErrors(null);
      form.classList.add("was-validated");
    }
  };

  const checkConfirmNewPassword = () => {
    if (newPassword !== confirmNewPassword) {
      confirmNewPasswordInput.setCustomValidity("error");
      setPasswordsDoNotMatch(true);

      return false;
    } else {
      return true;
    }
  };

  const handleConfirmNewPasswordChange = (event) => {
    confirmNewPasswordInput.setCustomValidity("");
    setConfirmNewPassword(event.target.value);
    setPasswordsDoNotMatch(false);
  };

  return (
    <div className="athlix-change-password-wrapper">
      <div className="athlix-change-password-card">
        <Errors errors={backendErrors} onClose={() => setBackendErrors(null)} />
          <h2 className="athlix-change-password-title">
            <FormattedMessage id="project.users.ChangePassword.title" />
          </h2>
          <form
            ref={(node) => (form = node)}
            className="athlix-change-password-form"
            noValidate
            onSubmit={(e) => handleSubmit(e)}
          >
            <div className="athlix-input-group">
              <input
                type="password"
                id="oldPassword"
                className="athlix-input"
                value={oldPassword}
                onChange={(e) => setOldPassword(e.target.value)}
                autoFocus
                required
                placeholder={intl.formatMessage({
                  id: "project.users.ChangePassword.fields.oldPassword",
                })}
              />
              <div className="invalid-feedback">
                <FormattedMessage id="project.global.validator.required" />
              </div>
            </div>
            <div className="athlix-input-group">
              <input
                type="password"
                id="newPassword"
                className="athlix-input"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                required
                placeholder={intl.formatMessage({
                  id: "project.users.ChangePassword.fields.newPassword",
                })}
              />
              <div className="invalid-feedback">
                <FormattedMessage id="project.global.validator.required" />
              </div>
            </div>
            <div className="athlix-input-group">
              <input
                ref={(node) => (confirmNewPasswordInput = node)}
                type="password"
                id="confirmNewPassword"
                className="athlix-input"
                value={confirmNewPassword}
                onChange={(e) => handleConfirmNewPasswordChange(e)}
                required
                placeholder={intl.formatMessage({
                  id: "project.users.ChangePassword.fields.confirmNewPassword",
                })}
              />
              <div className="invalid-feedback">
                {passwordsDoNotMatch ? (
                  <FormattedMessage id="project.global.validator.passwordsDoNotMatch" />
                ) : (
                  <FormattedMessage id="project.global.validator.required" />
                )}
              </div>
            </div>
            <div className="athlix-login-actions">
              <button
                type="submit"
                className="athlix-btn athlix-btn-primary athlix-login-submit"
              >
                <FormattedMessage id="project.global.buttons.save" />
              </button>
            </div>
          </form>
      </div>
    </div>
  );
};

export default ChangePassword;
