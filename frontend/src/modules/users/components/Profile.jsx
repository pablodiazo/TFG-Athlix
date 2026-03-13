import React from "react";
import { useSelector } from "react-redux";
import { FormattedMessage, useIntl } from "react-intl";
import { useNavigate } from "react-router-dom";

import users from "../../users";
import "../css/Profile.css";

const Profile = () => {
  const intl = useIntl();
  const navigate = useNavigate();
  const user = useSelector(users.selectors.getUser);
  
  if (!user) {
    return null; 
  }

  const getInitials = (firstName, lastName) => {
    const first = firstName ? firstName.charAt(0).toUpperCase() : "";
    const last = lastName ? lastName.charAt(0).toUpperCase() : "";
    return `${first}${last}` || "A";
  };

  return (
    <div className="athlix-profile-wrapper">
      <div className="athlix-profile-container">
        
        <div className="athlix-profile-header-card">
          <div className="athlix-profile-avatar-large">
            {getInitials(user.firstName, user.lastName)}
          </div>
          <div className="athlix-profile-header-info">
            <h2 className="athlix-profile-fullname">
              {user.firstName} {user.lastName}
            </h2>
            <p className="athlix-profile-username">@{user.userName}</p>
            <span className={`athlix-role-badge role-${user.role?.toLowerCase()}`}>
              {user.role === "COACH" 
                ? <FormattedMessage id="project.global.users.roles.coach" defaultMessage="Entrenador" />
                : <FormattedMessage id="project.global.users.roles.athlete" defaultMessage="Atleta" />
              }
            </span>
          </div>
        </div>

        <div className="athlix-profile-details-card">
          <h3 className="athlix-section-title">
            <FormattedMessage id="project.users.profile.details" defaultMessage="Detalles de la cuenta" />
          </h3>
          
          <div className="athlix-details-grid">
            <div className="athlix-detail-item">
              <span className="athlix-detail-label">
                <FormattedMessage id="project.global.fields.firstName" defaultMessage="Nombre" />
              </span>
              <span className="athlix-detail-value">{user.firstName}</span>
            </div>
            
            <div className="athlix-detail-item">
              <span className="athlix-detail-label">
                <FormattedMessage id="project.global.fields.lastName" defaultMessage="Apellidos" />
              </span>
              <span className="athlix-detail-value">{user.lastName}</span>
            </div>

            <div className="athlix-detail-item">
              <span className="athlix-detail-label">
                <FormattedMessage id="project.global.fields.email" defaultMessage="Correo electrónico" />
              </span>
              <span className="athlix-detail-value">{user.email}</span>
            </div>

            <div className="athlix-detail-item">
              <span className="athlix-detail-label">
                <FormattedMessage id="project.global.fields.userName" defaultMessage="Nombre de usuario" />
              </span>
              <span className="athlix-detail-value">{user.userName}</span>
            </div>
          </div>
        </div>

        <div className="athlix-profile-actions-card">
          <button 
            className="athlix-btn athlix-btn-primary"
            onClick={() => navigate("/users/updateProfile")}
          >
            <FormattedMessage id="project.users.updateProfile.title" defaultMessage="Editar Perfil" />
          </button>
          
          <button 
            className="athlix-btn athlix-btn-secondary"
            onClick={() => navigate("/users/changePassword")}
          >
            <FormattedMessage id="project.users.ChangePassword.title" defaultMessage="Cambiar Contraseña" />
          </button>
        </div>

      </div>
    </div>
  );
};

export default Profile;