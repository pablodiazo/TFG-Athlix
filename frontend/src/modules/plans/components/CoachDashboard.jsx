import React, { useState, useEffect } from "react";
import { FaUserCircle } from "react-icons/fa";
import DailyPlan from "./DailyPlan"; 
import backend from "../../../backend";
import "../css/CoachDashboard.css"; 
import { FormattedMessage } from "react-intl";

const CoachDashboard = () => {
  const [selectedAthleteId, setSelectedAthleteId] = useState(null);
  const [athletes, setAthletes] = useState([]);
  const [isLoadingAthletes, setIsLoadingAthletes] = useState(true);

  useEffect(() => {
    setIsLoadingAthletes(true);

    backend.userService.getAthletesByCoach(
      (data) => {
        setAthletes(data);
        setIsLoadingAthletes(false);
      },
      (error) => {
        console.error("Error al cargar los atletas:", error);
        setIsLoadingAthletes(false);
      }
    );
  }, []);

  return (
    <div className="coach-dashboard-wrapper">
      
      {/* BARRA LATERAL (SIDEBAR) */}
      <aside className="coach-sidebar">
        <h3 className="sidebar-title">Mis Atletas</h3>
        
        <div className="athlete-list">
          {isLoadingAthletes ? (
            <p style={{ color: "#9ca3af", fontSize: "0.9rem" }}><FormattedMessage id="project.plans.CoachDashboard.loading" /></p>
          ) : athletes.length === 0 ? (
            <p style={{ color: "#9ca3af", fontSize: "0.9rem", fontStyle: "italic" }}>
              <FormattedMessage id="project.plans.CoachDashboard.noAthletes" />
            </p>
          ) : (
            athletes.map((athlete) => (
              <button 
                key={athlete.id}
                className={`athlete-item-btn ${selectedAthleteId === athlete.id ? "active" : ""}`}
                onClick={() => setSelectedAthleteId(athlete.id)}
              >
                <FaUserCircle className="athlete-avatar" />
                <span>{athlete.firstName} {athlete.lastName}</span>
              </button>
            ))
          )}
        </div>
      </aside>

      {/* ÁREA PRINCIPAL */}
      <main className="coach-main-content">
        {selectedAthleteId ? (
          <DailyPlan athleteId={selectedAthleteId} />
        ) : (
          <div className="empty-dashboard">
            <FaUserCircle style={{ fontSize: "4rem", color: "#3b82f6", marginBottom: "1rem" }} />
            <h2><FormattedMessage id="project.plans.CoachDashboard.selectAthlete" /></h2>
            <p><FormattedMessage id="project.plans.CoachDashboard.chooseAthlete" /></p>
          </div>
        )}
      </main>

    </div>
  );
};

export default CoachDashboard;