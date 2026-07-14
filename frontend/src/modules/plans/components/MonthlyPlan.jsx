import React, { useState, useEffect } from "react";
import { FormattedMessage } from "react-intl";
import { Form, useNavigate } from "react-router-dom";
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

  const handlePrevMonth = () => {
    setCurrentMonth(new Date(currentMonth.getFullYear(), currentMonth.getMonth() - 1, 1));
  };

  const handleNextMonth = () => {
    setCurrentMonth(new Date(currentMonth.getFullYear(), currentMonth.getMonth() + 1, 1));
  };

  const handleToday = () => {
    setCurrentMonth(new Date());
  };

  const handleDayClick = (date) => {
    navigate(`/plans/athletes`, { state: { athleteId: athleteId, targetDate: getApiDateString(date) } });
  };

  const renderCalendar = () => {
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

  return (
    <div className="monthly-wrapper">
      
      <div className="monthly-header">
        <button className="athlix-btn-outline" onClick={handlePrevMonth}>
          <FaChevronLeft /> <FormattedMessage id="project.global.buttons.previous" />
        </button>
        <div className="monthly-title">
          <h2>{monthName.charAt(0).toUpperCase() + monthName.slice(1)} {currentMonth.getFullYear()}</h2>
          <button className="athlix-btn-text" onClick={handleToday}>IR AL MES ACTUAL</button>
        </div>
        <button className="athlix-btn-outline" onClick={handleNextMonth}>
          <FormattedMessage id="project.global.buttons.next" /> <FaChevronRight />
        </button>
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