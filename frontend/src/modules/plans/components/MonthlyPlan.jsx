import React, { useState, useEffect } from "react";
import { FormattedMessage } from "react-intl";
import { useNavigate } from "react-router-dom";
import backend from "../../../backend";
import { FaSwimmer, FaBicycle, FaRunning, FaDumbbell, FaSync, FaClock, FaChevronLeft, FaChevronRight } from "react-icons/fa";

import "../css/MonthlyPlan.css";

const SPORT_INFO = {
  SWIM: { name: "Natación", color: "#3b82f6", icon: FaSwimmer },
  BIKE: { name: "Ciclismo", color: "#eab308", icon: FaBicycle },
  RUN: { name: "Carrera", color: "#f97316", icon: FaRunning },
  STRENGTH: { name: "Fuerza", color: "#a855f7", icon: FaDumbbell },
  BRICK: { name: "Transición", color: "#ef4444", icon: FaSync },
  OTHER: { name: "Otro", color: "#9ca3af", icon: FaClock }
};

const MonthlyPlan = ({ athleteId }) => {
  const navigate = useNavigate();
  const [currentMonth, setCurrentMonth] = useState(new Date());
  const [monthlyData, setMonthlyData] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  const getApiDateString = (dateObj) => {
    const year = dateObj.getFullYear();
    const month = String(dateObj.getMonth() + 1).padStart(2, "0");
    const day = String(dateObj.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
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
    }
    return `${Math.round(totalMeters)} m`;
  };

  useEffect(() => {
    const fetchMonthlyPlan = () => {
      setIsLoading(true);
      
      const firstDay = new Date(currentMonth.getFullYear(), currentMonth.getMonth(), 1);
      const lastDay = new Date(currentMonth.getFullYear(), currentMonth.getMonth() + 1, 0);

      const startDateStr = getApiDateString(firstDay);
      const endDateStr = getApiDateString(lastDay);

      const onSuccess = (data) => {
        setMonthlyData(data);
        setIsLoading(false);
      };
      
      const onError = (error) => {
        console.error("Error fetching monthly plan:", error);
        setIsLoading(false);
      };

      if (athleteId) {
        backend.planService.getAthleteMonthlyPlan(athleteId, startDateStr, endDateStr, onSuccess, onError);
      } else {
        backend.planService.getMonthlyPlan(startDateStr, endDateStr, onSuccess, onError);
      }
    };

    fetchMonthlyPlan();
  }, [currentMonth, athleteId]);

  const handlePrevMonth = () => { setCurrentMonth(new Date(currentMonth.getFullYear(), currentMonth.getMonth() - 1, 1)); };
  const handleNextMonth = () => { setCurrentMonth(new Date(currentMonth.getFullYear(), currentMonth.getMonth() + 1, 1)); };
  const handleToday = () => { setCurrentMonth(new Date()); };
  const handleDayClick = (date) => { 
    if (!athleteId)
      navigate(`/plans/daily`, { state: { targetDate: getApiDateString(date) } });
    else{
     navigate(`/plans/athletes`, { state: { athleteId: athleteId, targetDate: getApiDateString(date) } }); 
    }
  };

  const getMonthlyTotals = () => {
    const totals = {};
    monthlyData.forEach(day => {
      if (day.sessions) {
        day.sessions.forEach(session => {
          const sport = session.sport;
          if (!totals[sport]) totals[sport] = { count: 0, ce: 0, totalMins: 0, totalMts: 0 };
          
          totals[sport].count += 1;
          totals[sport].ce += (session.ce || 0);

          const { minutes, meters } = parseSessionString(session.totalDistanceOrDuration);
          totals[sport].totalMins += minutes;
          totals[sport].totalMts += meters;
        });
      }
    });

    Object.keys(totals).forEach(sport => {
      totals[sport].duration = formatDuration(totals[sport].totalMins) || "--";
      totals[sport].distance = formatDistance(totals[sport].totalMts) || "--";
    });

    return totals;
  };

  const renderCalendar = () => {
    // ... (Mantén tu función renderCalendar exactamente igual que la tenías) ...
    const year = currentMonth.getFullYear();
    const month = currentMonth.getMonth();
    const firstDayOfMonth = new Date(year, month, 1);
    const lastDayOfMonth = new Date(year, month + 1, 0);
    
    let firstDayIndex = firstDayOfMonth.getDay() - 1;
    if (firstDayIndex === -1) firstDayIndex = 6;

    const daysInMonth = lastDayOfMonth.getDate();
    const calendarCells = [];

    for (let i = 0; i < firstDayIndex; i++) {
      calendarCells.push(<div key={`empty-prev-${i}`} className="calendar-cell empty"></div>);
    }

    for (let day = 1; day <= daysInMonth; day++) {
      const currentDateLoop = new Date(year, month, day);
      const dateStr = getApiDateString(currentDateLoop);
      const dayPlan = monthlyData.find(d => d.date === dateStr);
      const isToday = getApiDateString(new Date()) === dateStr;

      calendarCells.push(
        <div 
          key={day} 
          className={`calendar-cell ${isToday ? 'today' : ''} ${dayPlan?.sessions?.length > 0 ? 'has-workouts' : ''}`}
          onClick={() => handleDayClick(currentDateLoop)}
        >
          <span className="cell-day-number">{day}</span>
          <div className="cell-indicators">
            {dayPlan && dayPlan.sessions && dayPlan.sessions.map((session, idx) => {
              const sportInfo = SPORT_INFO[session.sport] || SPORT_INFO.OTHER;
              const SportIcon = sportInfo.icon;
              return (
                <div key={idx} className="sport-micro-badge" style={{ backgroundColor: `${sportInfo.color}20`, color: sportInfo.color, borderColor: `${sportInfo.color}40` }} title={`${sportInfo.name} - ${session.objective}`}>
                  <SportIcon className="micro-icon" />
                  <span className="micro-duration">{session.totalDistanceOrDuration}</span>
                </div>
              );
            })}
            <div className="lifestyle-dots">
              {dayPlan?.nutrition && <span className="dot nutrition-dot" title="Plan de Nutrición"></span>}
              {dayPlan?.rest && <span className="dot rest-dot" title="Plan de Descanso"></span>}
            </div>
          </div>
        </div>
      );
    }
    return calendarCells;
  };

  const monthName = new Intl.DateTimeFormat("es-ES", { month: "long" }).format(currentMonth);
  const totals = getMonthlyTotals();

  return (
    <div className="monthly-wrapper">
      
      <div className="monthly-header">
        <button className="athlix-btn-outline" onClick={handlePrevMonth}>
          <FaChevronLeft /> <FormattedMessage id="project.global.buttons.previous" />
        </button>
        <div className="monthly-title">
          <h2>{monthName.charAt(0).toUpperCase() + monthName.slice(1)} {currentMonth.getFullYear()}</h2>
          <button className="athlix-btn-text" onClick={handleToday}><FormattedMessage id="project.plans.MonthlyPlan.returnToCurrentMonth" defaultMessage="VOLVER AL MES ACTUAL" /></button>
        </div>
        <button className="athlix-btn-outline" onClick={handleNextMonth}>
          <FormattedMessage id="project.global.buttons.next" /> <FaChevronRight />
        </button>
      </div>

      <div className="monthly-top-summary">
        <h3 className="monthly-summary-title">
          <FormattedMessage id="project.plans.WeeklyPlan.totalVolumeAcumulated" defaultMessage="Volumen Acumulado" />
        </h3>
        
        {Object.keys(totals).length === 0 && !isLoading ? (
            <p className="monthly-summary-empty"><FormattedMessage id="project.plans.MonthlyPlan.noData" defaultMessage="No hay sesiones planificadas este mes." /></p>
        ) : (
          <div className="monthly-totals-grid">
            {Object.keys(totals).map((sportKey) => {
              const sportInfo = SPORT_INFO[sportKey] || SPORT_INFO.OTHER;
              const SportIcon = sportInfo.icon;
              const data = totals[sportKey];

              return (
                <div key={sportKey} className="monthly-total-card" style={{ borderBottomColor: sportInfo.color }}>
                  <div className="monthly-card-header-sport">
                    <SportIcon className="monthly-sport-icon" style={{ color: sportInfo.color }} />
                    <h4>{sportInfo.name}</h4>
                  </div>
                  <div className="monthly-stats-row" style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "0.8rem" }}>
                    <div className="monthly-stat-box">
                      <span><FormattedMessage id="project.plans.MonthlyPlan.sessionCount" defaultMessage="Sesiones" /></span>
                      <strong>{data.count}</strong>
                    </div>
                    {data.ce > 0 && (
                      <div className="monthly-stat-box">
                        <span><FormattedMessage id="project.plans.MonthlyPlan.ce" defaultMessage="Carga (CE)" /></span>
                        <strong>{Math.round(data.ce)}</strong>
                      </div>
                    )}
                    {data.duration !== "--" && (
                      <div className="monthly-stat-box">
                        <span><FormattedMessage id="project.plans.WeeklyPlan.time" defaultMessage="Tiempo" /></span>
                        <strong>{data.duration}</strong>
                      </div>
                    )}
                    {data.distance !== "--" && (
                      <div className="monthly-stat-box">
                        <span><FormattedMessage id="project.global.fields.distance" defaultMessage="Distancia" /></span>
                        <strong>{data.distance}</strong>
                      </div>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>

      <div className="calendar-container">
        <div className="calendar-weekdays">
          <span><FormattedMessage id="project.plans.MonthlyPlan.monday" /></span>
          <span><FormattedMessage id="project.plans.MonthlyPlan.tuesday" /></span>
          <span><FormattedMessage id="project.plans.MonthlyPlan.wednesday" /></span>
          <span><FormattedMessage id="project.plans.MonthlyPlan.thursday" /></span>
          <span><FormattedMessage id="project.plans.MonthlyPlan.friday" /></span>
          <span><FormattedMessage id="project.plans.MonthlyPlan.saturday" /></span>
          <span><FormattedMessage id="project.plans.MonthlyPlan.sunday" /></span>
        </div>
        
        {isLoading ? (
          <div className="monthly-loading">
            <div className="spinner"></div>
            <p><FormattedMessage id="project.plans.MonthlyPlan.loading" /></p>
          </div>
        ) : (
          <div className="calendar-grid">
            {renderCalendar()}
          </div>
        )}
      </div>

    </div>
  );
};

export default MonthlyPlan;