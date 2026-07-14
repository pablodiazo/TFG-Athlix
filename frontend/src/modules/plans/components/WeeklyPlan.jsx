import React, { useState, useEffect } from "react";
import { FormattedMessage } from "react-intl";
import backend from "../../../backend";

import { FaSwimmer, FaBicycle, FaRunning, FaDumbbell, FaClock, FaSync, FaChevronLeft, FaChevronRight } from "react-icons/fa";
import "../css/WeeklyPlan.css";

const SPORT_INFO = {
  SWIM: { name: "Natación", color: "#3b82f6", icon: FaSwimmer },
  BIKE: { name: "Ciclismo", color: "#eab308", icon: FaBicycle },
  RUN: { name: "Carrera", color: "#f97316", icon: FaRunning },
  STRENGTH: { name: "Fuerza", color: "#a855f7", icon: FaDumbbell },
  BRICK: { name: "Transición", color: "#ef4444", icon: FaSync },
  OTHER: { name: "Otro", color: "#9ca3af", icon: FaClock }
};

const WeeklyPlan = ({athleteId}) => {
  const [currentMonday, setCurrentMonday] = useState(() => {
    const today = new Date();
    const day = today.getDay();
    const diff = today.getDate() - day + (day === 0 ? -6 : 1);
    return new Date(today.setDate(diff));
  });

  const [weeklyData, setWeeklyData] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  const getApiDateString = (dateObj) => {
    const year = dateObj.getFullYear();
    const month = String(dateObj.getMonth() + 1).padStart(2, "0");
    const day = String(dateObj.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
  };

  const getWeekRangeDisplay = () => {
    const sunday = new Date(currentMonday);
    sunday.setDate(sunday.getDate() + 6);
    
    const options = { day: "numeric", month: "short" };
    const startStr = currentMonday.toLocaleDateString("es-ES", options);
    const endStr = sunday.toLocaleDateString("es-ES", { ...options, year: "numeric" });
    
    return `Semana: ${startStr} - ${endStr}`;
  };

  const parseSessionString = (input) => {
    if (!input) return { minutes: 0, meters: 0 };
    const str = input.toLowerCase();
    let minutes = 0;
    let meters = 0;

    const hMatch = str.match(/(\d+(?:[.,]\d+)?)\s*(?:h|hr|hrs|hora|horas)\b/);
    if (hMatch) minutes += parseFloat(hMatch[1].replace(',', '.')) * 60;

    const minMatch = str.match(/(\d+(?:[.,]\d+)?)\s*(?:min|mins|minuto|minutos)\b/);
    if (minMatch) minutes += parseFloat(minMatch[1].replace(',', '.'));

    const kmMatch = str.match(/(\d+(?:[.,]\d+)?)\s*(?:km|kms)\b/);
    if (kmMatch) meters += parseFloat(kmMatch[1].replace(',', '.')) * 1000;

    const mtsMatch = str.match(/(\d+(?:[.,]\d+)?)\s*(?:mts|metros)\b/);
    if (mtsMatch) meters += parseFloat(mtsMatch[1].replace(',', '.'));

    const mMatch = str.match(/(\d+(?:[.,]\d+)?)\s*m\b/);
    if (mMatch && !minMatch && !mtsMatch) { 
      const val = parseFloat(mMatch[1].replace(',', '.'));
      if (hMatch) {
        minutes += val;
      } else if (val >= 100) {
        meters += val;
      } else {
        minutes += val;
      }
    }

    return { minutes, meters };
  };

  const formatDuration = (totalMinutes) => {
    if (totalMinutes <= 0) return null;
    const h = Math.floor(totalMinutes / 60);
    const m = Math.round(totalMinutes % 60);
    if (h > 0 && m > 0) return `${h}h ${m}m`;
    if (h > 0) return `${h}h`;
    return `${m} min`;
  };

  const formatDistance = (totalMeters) => {
    if (totalMeters <= 0) return null;
    if (totalMeters >= 1000) {
        return `${+(totalMeters / 1000).toFixed(2)} km`;
    return `${Math.round(totalMeters)} m`;
    };
  };
  
  const processBackendData = (data) => {
    const processedData = {
      totals: {},
      days: []
    };

    ['RUN', 'BIKE', 'SWIM', 'STRENGTH'].forEach(sport => {
        processedData.totals[sport] = { totalMins: 0, totalMts: 0, sessionCount: 0 };
    });

    data.forEach((dayData) => {
        const dateObj = new Date(dayData.date);
        
        const sessions = dayData.sessions.map(session => {
            if (!processedData.totals[session.sport]) {
                processedData.totals[session.sport] = { totalMins: 0, totalMts: 0, sessionCount: 0 };
            }

            const { minutes, meters } = parseSessionString(session.totalDistanceOrDuration);
            processedData.totals[session.sport].totalMins += minutes;
            processedData.totals[session.sport].totalMts += meters;
            processedData.totals[session.sport].sessionCount += 1;
            
            return {
                sport: session.sport,
                title: session.objective || session.sport
            };
        });

        processedData.days.push({
            date: dateObj.toLocaleDateString("es-ES", { weekday: "long" }),
            dateNum: dateObj.getDate().toString(),
            sessions: sessions
        });
    });

    Object.keys(processedData.totals).forEach(sport => {
        const sportData = processedData.totals[sport];
        
        if (sportData.sessionCount > 0) {
            const timeStr = formatDuration(sportData.totalMins);
            const distStr = formatDistance(sportData.totalMts);

            if (timeStr || distStr) {
                processedData.totals[sport] = {
                    duration: timeStr || "--",
                    distance: distStr || "--"
                };
            } else {
                processedData.totals[sport] = {
                    duration: sportData.sessionCount === 1 ? "1 sesión" : `${sportData.sessionCount} sesiones`,
                    distance: "--" 
                };
            }
        } else {
             processedData.totals[sport] = { duration: "0", distance: "0" };
        }
    });

    return processedData;
  };

  useEffect(() => {
    const fetchWeeklyPlan = () => {
      setIsLoading(true);
      const startDateStr = getApiDateString(currentMonday);

      const onSuccess = (data) => {
        const processed = processBackendData(data);
        setWeeklyData(processed);
        setIsLoading(false);
      };

      const onError = (error) => {
        console.error("Error fetching weekly plan:", error);
        setIsLoading(false); 
      };

      if (athleteId) {
        backend.planService.getAthleteWeeklyPlan(athleteId, startDateStr, onSuccess, onError);
      } else {
        backend.planService.getWeeklyPlan(startDateStr, onSuccess, onError);
      }
    };

    fetchWeeklyPlan();
  }, [currentMonday, athleteId]);

  const handlePrevWeek = () => {
    setCurrentMonday((prev) => {
      const nd = new Date(prev);
      nd.setDate(nd.getDate() - 7);
      return nd;
    });
  };

  const handleNextWeek = () => {
    setCurrentMonday((prev) => {
      const nd = new Date(prev);
      nd.setDate(nd.getDate() + 7);
      return nd;
    });
  };

  const handleCurrentWeek = () => {
    const today = new Date();
    const day = today.getDay();
    const diff = today.getDate() - day + (day === 0 ? -6 : 1);
    setCurrentMonday(new Date(today.setDate(diff)));
  };

  if (isLoading) {
    return (
      <div className="athlix-weekly-wrapper loading">
        <div className="spinner"></div>
        <p><FormattedMessage id="project.plans.WeeklyPlan.loading" /></p>
      </div>
    );
  }

  if (!weeklyData) {
      return (
         <div className="athlix-weekly-wrapper">
             <div className="empty-day">
                <p><FormattedMessage id="project.plans.WeeklyPlan.noData" /></p>
             </div>
         </div>
      );
  }

  return (
    <div className="athlix-weekly-wrapper">
      
      {/* NAVEGADOR DE SEMANAS */}
      <div className="weekly-navigator">
        <button className="weekly-nav-btn" onClick={handlePrevWeek}>
          <FaChevronLeft /> <FormattedMessage id="project.plans.WeeklyPlan.previousWeek" defaultMessage="Semana anterior" />
        </button>
        <div className="weekly-current-display">
          <h2 className="weekly-range-text">{getWeekRangeDisplay()}</h2>
          <button className="weekly-today-btn" onClick={handleCurrentWeek}>
            <FormattedMessage id="project.plans.WeeklyPlan.returnToCurrentWeek" defaultMessage="Volver a esta semana" />
          </button>
        </div>
        <button className="weekly-nav-btn" onClick={handleNextWeek}>
          <FormattedMessage id="project.plans.WeeklyPlan.nextWeek" defaultMessage="Semana siguiente" /> <FaChevronRight />
        </button>
      </div>

      <div className="weekly-layout-grid">
        
        {/* BLOQUE GENERAL: CUADRO ACUMULADO */}
        <div className="weekly-dashboard-section">
          <h3 className="weekly-section-title"><FormattedMessage id="project.plans.WeeklyPlan.totalVolumeAcumulated" defaultMessage="Volumen Total Acumulado" /></h3>
          <div className="weekly-totals-grid">
            {Object.keys(weeklyData.totals).map((sportKey) => {
              const sportInfo = SPORT_INFO[sportKey] || SPORT_INFO.OTHER;
              const SportIcon = sportInfo.icon;
              const data = weeklyData.totals[sportKey];

              if(data.duration === "0") return null;

              return (
                <div key={sportKey} className="weekly-total-card" style={{ borderLeftColor: sportInfo.color }}>
                  <div className="weekly-card-header-sport">
                    <SportIcon className="weekly-sport-icon" style={{ color: sportInfo.color }} />
                    <h4>{sportInfo.name}</h4>
                  </div>
                  <div className="weekly-stats-row">
                    <div className="weekly-stat-box">
                      <span><FormattedMessage id="project.plans.WeeklyPlan.time" defaultMessage="Tiempo" /></span>
                      <strong>{data.duration}</strong>
                    </div>
                    {data.distance !== "--" && (
                      <div className="weekly-stat-box">
                        <span><FormattedMessage id="project.global.fields.distance" defaultMessage="Distancia" /></span>
                        <strong>{data.distance}</strong>
                      </div>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* BLOQUE DETALLE: RESUMEN DÍA A DÍA */}
        <div className="weekly-days-section">
          <h3 className="weekly-section-title"><FormattedMessage id="project.plans.WeeklyPlan.dailyDistribution" defaultMessage="Distribución Diaria" /></h3>
          <div className="weekly-days-grid">
            {weeklyData.days.map((day, idx) => {
              const isToday = new Date().getDay() === (idx === 6 ? 0 : idx + 1);
              const isRestDay = day.sessions.length === 0;

              return (
                <div key={idx} className={`weekly-day-column-card ${isToday ? "is-today" : ""}`}>
                  <div className="weekly-day-header">
                    <span className="weekly-day-name">{day.date}</span>
                    <span className="weekly-day-number">{day.dateNum}</span>
                  </div>
                  
                  <div className="weekly-day-sessions-list">
                    {isRestDay ? (
                      <div className="weekly-mini-rest-card">
                        <span><FormattedMessage id="project.global.fields.rest" defaultMessage="Descanso" /></span>
                      </div>
                    ) : (
                      day.sessions.map((session, sIdx) => {
                        const sportInfo = SPORT_INFO[session.sport] || SPORT_INFO.OTHER;
                        const SportIcon = sportInfo.icon;
                        
                        return (
                          <div key={sIdx} className="weekly-mini-session-card" style={{ backgroundColor: `${sportInfo.color}15`, borderLeft: `3px solid ${sportInfo.color}` }}>
                            <div className="weekly-mini-title-row">
                              <SportIcon style={{ color: sportInfo.color, fontSize: "0.9rem" }} />
                              <span className="weekly-mini-sport-name" style={{ color: sportInfo.color }}>{sportInfo.name}</span>
                            </div>
                            <p className="weekly-mini-session-desc">{session.title}</p>
                          </div>
                        );
                      })
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        </div>

      </div>
    </div>
  );
};

export default WeeklyPlan;