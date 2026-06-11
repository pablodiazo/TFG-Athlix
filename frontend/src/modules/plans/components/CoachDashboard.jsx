import React, { useState, useEffect } from "react";
import { useLocation } from "react-router-dom";
import { FaUserCircle, FaCalendarDay, FaCalendarWeek } from "react-icons/fa";
import DailyPlan from "./DailyPlan";
import WeeklyPlan from "./WeeklyPlan";
import backend from "../../../backend";
import "../css/CoachDashboard.css"; 
import { FormattedMessage } from "react-intl";

const CoachDashboard = () => {
  const location = useLocation();
  const [dashboardDate, setDashboardDate] = useState(null);

  const [selectedAthleteId, setSelectedAthleteId] = useState(null);
  const [athletes, setAthletes] = useState([]);
  const [isLoadingAthletes, setIsLoadingAthletes] = useState(true);

  const [viewMode, setViewMode] = useState('daily');

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

  useEffect(() => {
    if (location.state && location.state.athleteId) {
      setSelectedAthleteId(location.state.athleteId);
      
      setViewMode('daily');

      if (location.state.targetDate) {
        const [year, month, day] = location.state.targetDate.split('-');
        setDashboardDate(new Date(year, month - 1, day));
      }

      window.history.replaceState({}, document.title);
    }
  }, [location.state]);

  return (
    <div className="coach-dashboard-wrapper">
      
      {/* BARRA LATERAL (SIDEBAR) */}
      <aside className="coach-sidebar">
        <h3 className="sidebar-title"><FormattedMessage id="project.plans.CoachDashboard.myAthletes" /></h3>
        
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
          <>
            <div className="coach-main-toolbar">
              <div className="view-toggle-group">
                <button 
                  className={`view-toggle-btn ${viewMode === 'daily' ? 'active' : ''}`}
                  onClick={() => setViewMode('daily')}
                >
                  <FaCalendarDay /> <FormattedMessage id="project.global.fields.day" />
                </button>
                <button 
                  className={`view-toggle-btn ${viewMode === 'weekly' ? 'active' : ''}`}
                  onClick={() => setViewMode('weekly')}
                >
                  <FaCalendarWeek /> <FormattedMessage id="project.global.fields.week" />
                </button>
              </div>
            </div>

            {viewMode === 'daily' ? (
              <DailyPlan athleteId={selectedAthleteId} forcedDate={dashboardDate}/>
            ) : (
              <WeeklyPlan athleteId={selectedAthleteId} />
            )}
          </>
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