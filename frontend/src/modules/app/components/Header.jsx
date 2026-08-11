import { useState, useEffect } from "react";
import { useSelector } from "react-redux";
import { Link } from "react-router-dom";
import users, { UserMenu } from "../../users";
import { FaBell } from "react-icons/fa";
import backend from "../../../backend";
import { useNavigate } from "react-router-dom";

import "../css/Header.css";
import { FormattedMessage } from "react-intl";

const Header = () => {
  const userName = useSelector(users.selectors.getUserName);
  const isLoggedIn = useSelector(users.selectors.isLoggedIn);
  const userRole = useSelector(users.selectors.getRole);
  
  const navigate = useNavigate();

  const [notifications, setNotifications] = useState([]);
  const [showNotifications, setShowNotifications] = useState(false);

  useEffect(() => {
    backend.planService.getNotifications(
      (data) => setNotifications(data),
      (error) => console.error("Error cargando notificaciones", error),
    );
  }, []);

  const handleNotificationClick = (notification) => {
    if (!notification.isRead) {
      backend.planService.markNotificationAsRead(
        notification.id,
        () => {
          setNotifications((prev) =>
            prev.map((n) =>
              n.id === notification.id ? { ...n, isRead: true } : n,
            ),
          );
        },
        (error) => console.error("Error al marcar como leída", error),
      );
    }
    setShowNotifications(false);
    if(notification.type === "RESCHEDULE") {
      
      navigate("/plans/reschedule-requests");
    }
    else if (notification.type === "ACCEPTED_READJUSTMENT"){
      navigate("/plans/daily");
      /*
      navigate("/plans/daily", { 
      state: { 
        athleteId: notification.athleteId, 
        targetDate: notification.planDate
      } 
    });
      */
    }
    else if(notification.type === "COACH_ACCEPTED") {
      navigate("/plans/athletes");
    }
    else if(notification.type === "COACH_REJECTED") {
      navigate("/users/manage-athletes");
    }
    else if(notification.type === "COACH_REQUEST") {
      navigate("/users/coach-requests");
    }
    else if(notification.type === "AI_PROPOSAL") {
      navigate(`/plans/review-proposal/${notification.sessionId}`);
    }
  };

  const unreadCount = notifications.filter((n) => !n.isRead).length;

  const formatNiceDate = (dateStr) => {
    if (!dateStr) return "";
    
    const [year, month, day] = dateStr.split('-');
    const dateObj = new Date(year, month - 1, day);
    
    const options = { weekday: 'long', day: 'numeric', month: 'long' };
    let formatted = dateObj.toLocaleDateString('es-ES', options);
    
    return formatted.replace(',', ''); 
  };

  return (
    <header className="athlix-header">
      <div className="athlix-header-container">
        <div className="athlix-header-left">
          <div className="athlix-logo-wrapper">
          {isLoggedIn ? (
            <>
              {userRole === "USER" && (
                <Link className="athlix-logo-link" to="/plans/daily">
                  <h1 className="athlix-logo-text">
                    ATHLIX<span className="athlix-accent">.</span>
                  </h1>
                </Link>
              )}
              {userRole === "COACH" && (
                <Link className="athlix-logo-link" to="/plans/athletes">
                  <h1 className="athlix-logo-text">
                    ATHLIX<span className="athlix-accent">.</span>
                  </h1>
                </Link>
              )}
            </>
          ) : (
            <Link className="athlix-logo-link" to="/">
              <h1 className="athlix-logo-text">
                ATHLIX<span className="athlix-accent">.</span>
              </h1>
            </Link>
          )}
          </div>
          
          {isLoggedIn && (
            <div className="athlix-header-notifications">
              <div
                className="notifications-wrapper"
                style={{ position: "relative", marginRight: "auto" }}
              >
                <button
                  className="notification-bell-btn"
                  onClick={() => setShowNotifications(!showNotifications)}
                >
                  <FaBell />
                  {unreadCount > 0 && (
                    <span className="notification-badge">{unreadCount}</span>
                  )}
                </button>

                {showNotifications && (
                  <div className="notifications-dropdown">
                    <h4><FormattedMessage id="project.global.messages.notifications" /></h4>
                    {notifications.length === 0 ? (
                      <p className="no-notifications">
                        <FormattedMessage id="project.global.messages.noNotifications" />
                      </p>
                    ) : (
                      <div className="notifications-list">
                        {notifications.map((notif) => (
                          notif.type === "RESCHEDULE" ? (
                            <div
                              key={notif.id}
                              className={`notification-item ${!notif.isRead ? "unread" : ""}`}
                              onClick={() => {
                                handleNotificationClick(notif);
                              }}
                            >
                              <p>{notif.message}</p>
                            </div>
                          ) : (
                            <div
                              key={notif.id}
                              className={`notification-item ${!notif.isRead ? "unread" : ""}`}
                              onClick={() => {
                                handleNotificationClick(notif);
                              }}
                            >
                              <p>{notif.message}</p>
                              <small style={{ textTransform: 'capitalize' }}>
                                  {formatNiceDate(notif.planDate)}
                              </small>
                            </div>
                          )
                          
                        ))}
                      </div>
                    )}
                  </div>
                )}
              </div>
            </div>
          )}
          
        </div>
        <div className="athlix-header-actions">
          {userName && <UserMenu />}
        </div>
      </div>
    </header>
  );
};

export default Header;
