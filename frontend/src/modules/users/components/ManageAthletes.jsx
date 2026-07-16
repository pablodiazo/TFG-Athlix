import React, { useState, useEffect } from "react";
import { FaPaperPlane, FaUserPlus, FaClock } from "react-icons/fa";
import { FormattedMessage } from "react-intl";
import backend from "../../../backend";
import "../css/ManageAthletes.css"; 

const ManageAthletes = () => {
  const [inviteEmail, setInviteEmail] = useState("");
  const [feedback, setFeedback] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [sentRequests, setSentRequests] = useState([]);
  const [isLoadingRequests, setIsLoadingRequests] = useState(true);

  const fetchSentRequests = () => {
    setIsLoadingRequests(true);
    backend.userService.getSentCoachRequests(
      (data) => {
        setSentRequests(data);
        setIsLoadingRequests(false);
      },
      (error) => {
        console.error("Error cargando invitaciones enviadas:", error);
        setIsLoadingRequests(false);
      }
    );
  };

  useEffect(() => {
    fetchSentRequests();
  }, []);

  const handleSendInvite = (e) => {
    e.preventDefault();
    setIsLoading(true);
    setFeedback(null);
    
    backend.userService.sendCoachRequest(
      inviteEmail,
      () => {
        setFeedback({ type: 'success', msg: `¡Invitación enviada a ${inviteEmail}!` });
        setInviteEmail("");
        setIsLoading(false);
        fetchSentRequests();
      },
      (error) => {
        setFeedback({ type: 'error', msg: 'El usuario no existe, ya es tu atleta, o ya tiene una invitación tuya pendiente.' });
        setIsLoading(false);
      }
    );
  };

  return (
    <div className="athlix-manage-wrapper">
      <div className="manage-header">
        <h2><FormattedMessage id="project.plans.ManageAthletes.title" defaultMessage="Gestión de Atletas" /></h2>
        <p><FormattedMessage id="project.plans.ManageAthletes.subtitle" defaultMessage="Amplía tu equipo enviando invitaciones de entrenamiento." /></p>
      </div>

      <div className="manage-content-grid">
        <div className="invite-panel">
          <div className="panel-icon-header">
            <FaUserPlus className="panel-icon" />
            <h3><FormattedMessage id="project.plans.ManageAthletes.invite" defaultMessage="Invitar Nuevo Atleta" /></h3>
          </div>
          <p className="panel-desc">
            <FormattedMessage id="project.plans.ManageAthletes.description" defaultMessage="Introduce el correo electrónico del atleta. Recibirá una notificación en su panel para aceptar tu solicitud de planificación." />
          </p>
          <form onSubmit={handleSendInvite} className="invite-form">
            <div className="athlix-form-group">
              <input 
                type="email" 
                value={inviteEmail} 
                onChange={(e) => setInviteEmail(e.target.value)} 
                placeholder="correo@delatleta.com"
                required
                disabled={isLoading}
                className="invite-input"
              />
            </div>

            {feedback && (
              <div className={`athlix-feedback-msg ${feedback.type}`}>
                {feedback.msg}
              </div>
            )}

            <button type="submit" className="athlix-btn-primary" disabled={!inviteEmail || isLoading}>
              {isLoading ? <FormattedMessage id="project.plans.ManageAthletes.sending" defaultMessage="Enviando..." /> : <><FaPaperPlane /> <FormattedMessage id="project.plans.ManageAthletes.sendInvite" defaultMessage="Enviar Invitación" /></>}
            </button>
          </form>
        </div>

        <div className="sent-requests-panel">
           <h3><FormattedMessage id="project.plans.ManageAthletes.pendingInvitations" defaultMessage="Invitaciones Pendientes" /></h3>
           <p className="panel-desc"><FormattedMessage id="project.plans.ManageAthletes.pendingDescription" defaultMessage="Atletas que aún no han respondido a tu solicitud." /></p>
           {isLoadingRequests ? (
             <div className="empty-state"><FormattedMessage id="project.global.messages.loading" defaultMessage="Cargando..." /></div>
           ) : sentRequests.length === 0 ? (
             <div className="empty-state">
                <FormattedMessage id="project.plans.ManageAthletes.noRequests" defaultMessage="No tienes ninguna invitación pendiente de respuesta." />
             </div>
           ) : (
             <div className="sent-requests-list" style={{ display: "flex", flexDirection: "column", gap: "1rem" }}>
                {sentRequests.map(req => (
                  <div key={req.id} style={{
                    backgroundColor: "rgba(255, 255, 255, 0.03)",
                    padding: "1rem",
                    borderRadius: "8px",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "space-between",
                    borderLeft: "3px solid #eab308"
                  }}>
                    <div>
                      <strong style={{ display: "block", color: "#f3f4f6", marginBottom: "0.2rem" }}>
                        {req.athleteFirstName} {req.athleteLastName}
                      </strong>
                      <span style={{ fontSize: "0.85rem", color: "#9ca3af" }}>{req.athleteEmail}</span>
                    </div>
                    <div style={{ display: "flex", alignItems: "center", gap: "0.4rem", color: "#eab308", fontSize: "0.85rem", fontWeight: "600" }}>
                      <FaClock /> <FormattedMessage id="project.plans.ManageAthletes.pending" defaultMessage="Pendiente" />
                    </div>
                  </div>
                ))}
             </div>
           )}
        </div>

      </div>
    </div>
  );
};

export default ManageAthletes;