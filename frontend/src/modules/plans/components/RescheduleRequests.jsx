import React, { useState, useEffect } from "react";
import backend from "../../../backend";
import { FaCalendarAlt, FaCheck, FaTimes, FaArrowRight, FaClock } from "react-icons/fa";
import { FormattedMessage } from "react-intl";
import "../css/RescheduleRequests.css";

const RescheduleRequests = () => {
  const [requests, setRequests] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    fetchRequests();
  }, []);

  const fetchRequests = () => {
    setIsLoading(true);
    backend.planService.getNotifications(
      (data) => {
        const pendingRequests = data.filter(n => n.type === "RESCHEDULE" && !n.isReviewed);
        setRequests(pendingRequests);
        setIsLoading(false);
      },
      (error) => {
        console.error("Error al cargar las solicitudes:", error);
        setIsLoading(false);
      }
    );
  };

  const acceptRequest = (request, isAccepted) => {
    const payload = {
      athleteId: request.athleteId,
      sessionId: request.sessionId, 
      newDate: request.newDate,     
      newStartTime: request.newStartTime, 
      reschedule: isAccepted
    };

    backend.planService.acceptReadjustment(
      request.id,
      payload,
      () => {
        setRequests(prev => prev.filter(r => r.id !== request.id));
      },
      (error) => {
        console.error("Error al procesar la solicitud:", error);
        alert("No se pudo procesar la solicitud. Inténtalo de nuevo.");
      }
    );
  };

  const denyRequest = (request, isDenied) => {
    const payload = {
      athleteId: request.athleteId,
      sessionId: request.sessionId, 
      newDate: request.newDate,     
      newStartTime: request.newStartTime, 
      reschedule: isDenied
    };

    backend.planService.denyReadjustment(
      request.id,
      payload,
      () => {
        setRequests(prev => prev.filter(r => r.id !== request.id));
      },
      (error) => {
        console.error("Error al procesar la solicitud:", error);
        alert("No se pudo procesar la solicitud. Inténtalo de nuevo.");
      }
    );
  };

  const formatNiceDate = (dateStr) => {
    if (!dateStr) return "";
    const [year, month, day] = dateStr.split('-');
    const dateObj = new Date(year, month - 1, day);
    const options = { weekday: 'short', day: 'numeric', month: 'short' };
    return dateObj.toLocaleDateString('es-ES', options).replace(',', ''); 
  };

  const formatTime = (timeStr) => timeStr ? timeStr.substring(0, 5) : "";

  if (isLoading) {
    return (
      <div className="requests-wrapper loading">
        <div className="spinner"></div>
        <p><FormattedMessage id="project.plans.RescheduleRequests.loading"/></p>
      </div>
    );
  }

  return (
    <div className="requests-wrapper">
      <div className="requests-header">
        <h2><FormattedMessage id="project.plans.RescheduleRequests.title"/></h2>
        <p><FormattedMessage id="project.plans.RescheduleRequests.description"/></p>
      </div>

      {requests.length === 0 ? (
        <div className="empty-requests">
          <FaCalendarAlt style={{ fontSize: "3rem", color: "#3b82f6", marginBottom: "1rem" }} />
          <h3><FormattedMessage id="project.plans.RescheduleRequests.noRequests"/></h3>
          <p><FormattedMessage id="project.plans.RescheduleRequests.noRequestsDescription"/></p>
        </div>
      ) : (
        <div className="requests-grid">
          {requests.map((req) => (
            <div key={req.id} className="request-card">
              
              <div className="request-card-header">
                <FaClock className="request-icon" />
                <span className="request-athlete-id"><FormattedMessage id="project.plans.RescheduleRequests.athleteId"/><span>{req.athleteId}</span></span>
              </div>

              <div className="request-card-body">
                <p className="request-message">{req.message}</p>
                
                {req.newDate && (
                  <div className="time-change-visual">
                    <div className="time-box original">
                      <small><FormattedMessage id="project.plans.RescheduleRequests.original"/></small>
                      <strong>{formatNiceDate(req.planDate)}</strong>
                    </div>
                    <FaArrowRight className="arrow-icon" />
                    <div className="time-box proposed">
                      <small><FormattedMessage id="project.plans.RescheduleRequests.proposed"/></small>
                      <strong>{formatNiceDate(req.newDate)}</strong>
                      <span>{formatTime(req.newStartTime)}</span>
                    </div>
                  </div>
                )}
              </div>

              <div className="request-card-actions">
                <button 
                  className="req-btn reject" 
                  onClick={() => denyRequest(req, true)}
                >
                  <FaTimes /> <FormattedMessage id="project.plans.RescheduleRequests.deny"/>
                </button>
                <button 
                  className="req-btn accept" 
                  onClick={() => acceptRequest(req, true)}
                >
                  <FaCheck /> <FormattedMessage id="project.plans.RescheduleRequests.accept"/>
                </button>
              </div>

            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default RescheduleRequests;