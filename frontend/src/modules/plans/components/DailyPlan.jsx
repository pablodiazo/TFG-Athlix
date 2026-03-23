import React, { useState, useEffect } from "react";
import { FormattedMessage } from "react-intl";
import backend from "../../../backend"; 

import { FaSwimmer, FaBicycle, FaRunning, FaDumbbell, FaClock, FaSync } from "react-icons/fa";
import "../css/DailyPlan.css";

const SPORT_INFO = {
  SWIM: { name: "Natación", color: "#3b82f6" , icon: FaSwimmer},
  BIKE: { name: "Ciclismo", color: "#eab308", icon: FaBicycle},
  RUN: { name: "Carrera", color: "#f97316" , icon: FaRunning},
  STRENGTH: { name: "Fuerza", color: "#a855f7", icon: FaDumbbell },
  BRICK: { name: "Transición", color: "#ef4444", icon: FaSync },
  OTHER: { name: "Otro", color: "#9ca3af", icon: FaClock }
};

const DailyPlan = () => {
  const [currentDate, setCurrentDate] = useState(new Date());
  const [planData, setPlanData] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  const getApiDateString = (dateObj) => {
    const year = dateObj.getFullYear();
    const month = String(dateObj.getMonth() + 1).padStart(2, "0");
    const day = String(dateObj.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
  };

  const getDisplayDate = (dateObj) => {
    return new Intl.DateTimeFormat("es-ES", {
      weekday: "long",
      day: "numeric",
      month: "long",
      year: "numeric"
    }).format(dateObj);
  };

  const formatTime = (timeString) => {
    if (!timeString) return "";
    return timeString.substring(0, 5);
  };

  useEffect(() => {
    const fetchPlan = () => {
      setIsLoading(true);
      const apiDate = getApiDateString(currentDate);
      
      backend.planService.getDailyPlan(
        apiDate,
        (data) => {
          setPlanData(data);
          setIsLoading(false);
        },
        (error) => {
          console.error("Error fetching plan:", error);
          setIsLoading(false);
        }
      );
    };

    fetchPlan();
  }, [currentDate]);

  const handlePrevDay = () => {
    setCurrentDate((prev) => {
      const newDate = new Date(prev);
      newDate.setDate(newDate.getDate() - 1);
      return newDate;
    });
  };

  const handleNextDay = () => {
    setCurrentDate((prev) => {
      const newDate = new Date(prev);
      newDate.setDate(newDate.getDate() + 1);
      return newDate;
    });
  };

  const handleToday = () => {
    setCurrentDate(new Date());
  };

  if (isLoading) {
    return (
      <div className="daily-wrapper loading">
        <div className="spinner"></div>
        <p>Cargando plan...</p>
      </div>
    );
  }

  const isRestDay = !planData?.sessions?.length && !planData?.nutrition && !planData?.rest;

  return (
    <div className="daily-wrapper">
      
      <div className="date-navigator">
        <button className="nav-btn" onClick={handlePrevDay}>
          Anterior
        </button>
        <div className="current-date-display">
          <h2 className="date-text">{getDisplayDate(currentDate)}</h2>
          <button className="today-btn" onClick={handleToday}>Hoy</button>
        </div>
        <button className="nav-btn" onClick={handleNextDay}>
          Siguiente
        </button>
      </div>

      {isRestDay ? (
        <div className="empty-day">
          <h3><FormattedMessage id="project.plans.DailyPlan.freeDay" /></h3>
          <p><FormattedMessage id="project.plans.DailyPlan.nothingPlanned" /></p>
        </div>
      ) : (
        <div className="daily-layout">
          
          {/* ENTRENAMIENTOS */}
          <div className="section-block">
            <h3 className="section-title"><FormattedMessage id="project.plans.DailyPlan.workouts" /></h3>
            
            <div className="sessions-row">
                {!planData.sessions || planData.sessions.length === 0 ? (
                <div className="empty-card"><FormattedMessage id="project.plans.DailyPlan.restDay" /></div>
                ) : (
                planData.sessions.map((session) => {
                    const sportInfo = SPORT_INFO[session.sport] || SPORT_INFO.OTHER;
                    const SportIcon = sportInfo.icon;
                    
                    return (
                    <div key={session.id} className="session-card">
                        <div className="session-header">
                        <div className="session-title" style={{ color: sportInfo.color, display: "flex", alignItems: "center", gap: "0.5rem" }}>
                            <SportIcon style={{ fontSize: "1.4rem" }} />
                            <h4>{sportInfo.name}</h4>
                            <span className="session-time">{formatTime(session.startTime)}</span>
                        </div>
                        <p className="session-objective">{session.totalDistanceOrDuration} - {session.objective}</p>
                        </div>

                        <div className="blocks-container">
                        {session.blocks && session.blocks.map((block) => (
                            <div key={block.id} className="block-row">
                            <div className="block-main">
                                <span className="block-sets">
                                {block.sets > 1 ? `${block.sets} x` : ''} {block.reps > 1 ? `${block.reps} x` : ''} {block.distanceOrDuration}
                                </span>
                                <span className="block-name">{block.name}</span>
                            </div>
                            <div className="block-details">
                                {block.pace && block.pace !== "0" && <span className="badge pace">{block.pace}</span>}
                                {block.rest && block.rest !== "0" && <span className="badge rest">Recuperación: {block.rest}</span>}
                            </div>
                            </div>
                        ))}
                        </div>
                    </div>
                    );
                })
                )}
            </div>
          </div>

          {/* NUTRICIÓN Y DESCANSO */}
          <div className="section-block">
            <h3 className="section-title"><FormattedMessage id="project.plans.DailyPlan.lifestyle" /></h3>
            
            <div className="lifestyle-row">
                {/* NUTRICIÓN */}
                <div className="lifestyle-card">
                    <h3 className="card-title"><FormattedMessage id="project.plans.DailyPlan.nutrition" /></h3>
                    {planData.nutrition ? (
                        <div className="nutrition-content">
                        <div className="calories-huge">
                            {planData.nutrition.targetCalories} <span>kcal</span>
                        </div>
                        <div className="macros-grid">
                            <div className="macro-item protein">
                            <span><FormattedMessage id="project.plans.DailyPlan.proteins" /></span>
                            <strong>{planData.nutrition.proteinGrams}g</strong>
                            </div>
                            <div className="macro-item carbs">
                            <span><FormattedMessage id="project.plans.DailyPlan.carbs" /></span>
                            <strong>{planData.nutrition.carbsGrams}g</strong>
                            </div>
                            <div className="macro-item fat">
                            <span><FormattedMessage id="project.plans.DailyPlan.fats" /></span>
                            <strong>{planData.nutrition.fatGrams}g</strong>
                            </div>
                            <div className="macro-item water">
                            <span><FormattedMessage id="project.plans.DailyPlan.water" /></span>
                            <strong>{planData.nutrition.hydrationLiters}L</strong>
                            </div>
                        </div>
                        {planData.nutrition.guidelines && (
                            <div className="guidelines">
                            <p>{planData.nutrition.guidelines}</p>
                            </div>
                        )}
                        </div>
                    ) : (
                        <p className="not-planned"><FormattedMessage id="project.plans.DailyPlan.noNutritionPlanned" /></p>
                    )}
                </div>

                {/* DESCANSO */}
                <div className="lifestyle-card">
                    <h3 className="card-title"><FormattedMessage id="project.plans.DailyPlan.rest" /></h3>
                    {planData.rest ? (
                        <div className="rest-content">
                        <div className="sleep-huge">
                            {planData.rest.targetSleepHours} <span>h</span>
                        </div>
                        <p className="sleep-label"><FormattedMessage id="project.plans.DailyPlan.sleepTarget" /></p>
                        
                        {planData.rest.guidelines && (
                            <div className="guidelines">
                            <p>{planData.rest.guidelines}</p>
                            </div>
                        )}
                        </div>
                    ) : (
                        <p className="not-planned"><FormattedMessage id="project.plans.DailyPlan.noRestPlanned" /></p>
                    )}
                </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default DailyPlan;