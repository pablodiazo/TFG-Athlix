import React, { useState, useEffect } from "react";
import { FormattedMessage } from "react-intl";
import backend from "../../../backend";
import { FaCheck, FaTimes, FaUserTie } from "react-icons/fa";
import "../css/PendingRequests.css";

const PendingRequests = () => {
  const [requests, setRequests] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  const fetchRequests = () => {
    setIsLoading(true);
    backend.userService.getPendingCoachRequests(
      (data) => {
        setRequests(data);
        setIsLoading(false);
      },
      (error) => {
        console.error("Error cargando peticiones:", error);
        setIsLoading(false);
      }
    );
  };

  useEffect(() => {
    fetchRequests();
  }, []);

  const handleAccept = (id) => {
    backend.userService.acceptCoachRequest(id, () => {
      setRequests(requests.filter(req => req.id !== id));
    }, console.error);
  };

  const handleReject = (id) => {
    backend.userService.rejectCoachRequest(id, () => {
      setRequests(requests.filter(req => req.id !== id));
    }, console.error);
  };

  if (isLoading) return <div className="spinner"></div>;

  return (
    <div className="athlix-pending-requests-wrapper">
      <h2><FormattedMessage id="project.plans.PendingRequests.title" defaultMessage="Peticiones de Entrenadores" /></h2>
      <p><FormattedMessage id="project.plans.PendingRequests.description" defaultMessage="Estos entrenadores desean planificar tus sesiones. Selecciona con quién quieres entrenar." /></p>
      {requests.length === 0 ? (
        <div className="empty-state"><FormattedMessage id="project.plans.PendingRequests.noRequests" defaultMessage="No tienes ninguna petición pendiente." /></div>
      ) : (
        <div className="requests-grid">
          {requests.map((req) => (
            <div key={req.id} className="request-card">
              <div className="request-info">
                <div className="avatar-circle">
                  <FaUserTie />
                </div>
                <div>
                  <h4>{req.coachFirstName} {req.coachLastName}</h4>
                  <span><FormattedMessage id="project.plans.PendingRequests.coachRequest" defaultMessage="Quiere ser tu entrenador"/></span>
                </div>
              </div>
              
              <div className="request-actions">
                <button className="btn-reject" onClick={() => handleReject(req.id)}>
                  <FaTimes /> <FormattedMessage id="project.plans.PendingRequests.deny" defaultMessage="Rechazar" />
                </button>
                <button className="btn-accept" onClick={() => handleAccept(req.id)}>
                  <FaCheck /> <FormattedMessage id="project.plans.PendingRequests.accept" defaultMessage="Aceptar" />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default PendingRequests;