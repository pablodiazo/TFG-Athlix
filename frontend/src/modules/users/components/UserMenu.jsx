import React, { useState, useEffect, useRef } from "react";
import { useSelector, useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { FormattedMessage } from "react-intl";

import users from "../../users";
import * as actions from "../actions";
import backend from "../../../backend";

import "../css/UserMenu.css";

const UserMenu = () => {
  const userName = useSelector(users.selectors.getUserName);
  const isUser = useSelector(users.selectors.isUser);
  const isCoach = useSelector(users.selectors.isCoach);

  const navigate = useNavigate();
  const dispatch = useDispatch();

  const [isOpen, setIsOpen] = useState(false);
  const menuRef = useRef(null);

  const toggleMenu = () => setIsOpen((prev) => !prev);

  const handleLogout = () => {
    backend.userService.logout();
    dispatch(actions.logout());
    navigate("/");
  };

  const handleClickOutside = (event) => {
    if (menuRef.current && !menuRef.current.contains(event.target)) {
      setIsOpen(false);
    }
  };

  useEffect(() => {
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const navAndClose = (path) => {
    navigate(path);
    setIsOpen(false);
  };

  return (
    <div className="athlix-user-menu" ref={menuRef}>
      <button 
        className={`athlix-user-btn ${isOpen ? "active" : ""}`} 
        onClick={toggleMenu}
      >
        <div className="athlix-avatar-circle">
          {userName ? userName.charAt(0).toUpperCase() : "A"}
        </div>
        <span className="athlix-username">{userName}</span>
        <svg 
          className={`athlix-chevron ${isOpen ? "open" : ""}`} 
          width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"
        >
          <path strokeLinecap="round" strokeLinejoin="round" d="M19 9l-7 7-7-7" />
        </svg>
      </button>

      {isOpen && (
        <div className="athlix-dropdown-menu">
          <div className="athlix-dropdown-header">
            <span className="athlix-dropdown-subtitle">Sesión iniciada como</span>
            <span className="athlix-dropdown-name">{userName}</span>
          </div>

          <ul className="athlix-dropdown-list">
            <li className="athlix-dropdown-group">Gestión de Cuenta</li>
            <li>
              <button onClick={() => navAndClose("/users/profile")}>
                <FormattedMessage id="project.users.profile.title" />
              </button>
            </li>
            <li>
              <button onClick={() => navAndClose("/users/changePassword")}>
                <FormattedMessage id="project.users.ChangePassword.title" />
              </button>
            </li>

            <li className="athlix-dropdown-divider"></li>
            <li className="athlix-dropdown-group">Planificacion</li>

            {isUser && (
              <>
                <li>
                  <button onClick={() => navAndClose("/plans/daily")}>
                    <FormattedMessage id="project.plans.DailyPlan.title" defaultMessage="Plan diario" />
                  </button>
                </li>
                <li>
                  <button onClick={() => navAndClose("/users/seeFollowing")}>
                    Opción 2
                  </button>
                </li>
              </>
            )}

            {isCoach && (
              <>
                <li>
                  <button onClick={() => navAndClose("/plans/create-training-session")}>
                    <FormattedMessage id="project.plans.CreateTrainingSession.title" defaultMessage="Crear sesión de entrenamiento" />
                  </button>
                </li>
                <li>
                  <button onClick={() => navAndClose("/plans/create-nutrition-plan")}>
                    <FormattedMessage id="project.plans.CreateNutritionPlan.title" defaultMessage="Crear plan de nutrición" />
                  </button>
                </li>
              </>
            )}

            <li className="athlix-dropdown-divider"></li>
            
            <li>
              <button onClick={handleLogout} className="athlix-logout-btn">
                <FormattedMessage id="project.users.logout.title" />
              </button>
            </li>
          </ul>
        </div>
      )}
    </div>
  );
};

export default UserMenu;