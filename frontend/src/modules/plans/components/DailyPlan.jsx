import React, { useState, useEffect } from "react";
import { FormattedMessage } from "react-intl";
import backend from "../../../backend";
import { useNavigate } from "react-router-dom";

import { FaSwimmer, FaBicycle, FaRunning, FaDumbbell, FaClock, FaSync, FaCheck, FaTimes, FaCalendarDay, FaEdit, FaTrash, FaRobot } from "react-icons/fa";
import "../css/DailyPlan.css";

const SPORT_INFO = {
  SWIM: { name: "Natación", color: "#3b82f6" , icon: FaSwimmer},
  BIKE: { name: "Ciclismo", color: "#eab308", icon: FaBicycle},
  RUN: { name: "Carrera", color: "#f97316" , icon: FaRunning},
  STRENGTH: { name: "Fuerza", color: "#a855f7", icon: FaDumbbell },
  BRICK: { name: "Transición", color: "#ef4444", icon: FaSync },
  OTHER: { name: "Otro", color: "#9ca3af", icon: FaClock }
};

const DailyPlan = ({ athleteId, forcedDate }) => {
  const [currentDate, setCurrentDate] = useState(forcedDate || new Date());
  const [planData, setPlanData] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const navigate = useNavigate();
  const [isReplanningId, setIsReplanningId] = useState(null);
  const isCoach = !!athleteId;

  useEffect(() => {
    if (forcedDate) {
      setCurrentDate(forcedDate);
    }
  }, [forcedDate]);

  const [popoverState, setPopoverState] = useState({
    activeId: null,
    type: null,
    entityId: null,
    currentValue: 0,
    isSaving: false
  });

  const [reschedulePopover, setReschedulePopover] = useState({
    activeId: null,
    newDate: "",
    newStartTime: "",
    isSaving: false
  });

  const openReschedulePopover = (e, sessionId, currentStartTime) => {
    e.stopPropagation();
    const tomorrow = new Date(currentDate);
    tomorrow.setDate(tomorrow.getDate() + 1);
    
    setReschedulePopover({
      activeId: sessionId,
      newDate: getApiDateString(tomorrow),
      newStartTime: currentStartTime ? currentStartTime.substring(0, 5) : "12:00",
      isSaving: false
    });
  };

  const closeReschedulePopover = (e) => {
    if (e) e.stopPropagation();
    setReschedulePopover({ activeId: null, newDate: "", newStartTime: "", isSaving: false });
  };

  const handleSaveReschedule = (e) => {
    if (e) e.stopPropagation();
    setReschedulePopover(prev => ({ ...prev, isSaving: true }));

    const payload = {
      sessionId: reschedulePopover.activeId,
      newDate: reschedulePopover.newDate,
      newStartTime: reschedulePopover.newStartTime.length === 5 ? `${reschedulePopover.newStartTime}:00` : reschedulePopover.newStartTime
    };

    backend.planService.rescheduleTrainingSession(
      payload,
      () => {
        setPlanData(prevData => ({
          ...prevData,
          sessions: prevData.sessions.filter(s => s.id !== reschedulePopover.activeId)
        }));
        closeReschedulePopover();
      },
      (error) => {
        console.error("Error al reprogramar:", error);
        setReschedulePopover(prev => ({ ...prev, isSaving: false }));
        alert("No se pudo mover la sesión.");
      }
    );
  };

  const openPopover = (e, id, type, entityId, currentDecimalValue) => {
    e.stopPropagation();
    setPopoverState({
      activeId: id,
      type: type,
      entityId: entityId,
      currentValue: Math.round((currentDecimalValue || 0) * 100),
      isSaving: false
    });
  };

  const closePopover = (e) => {
    if (e) e.stopPropagation();
    setPopoverState({ activeId: null, type: null, entityId: null, currentValue: 0, isSaving: false });
  };

  const handleSavePopover = (e) => {
    if (e) e.stopPropagation();
    setPopoverState(prev => ({ ...prev, isSaving: true }));
    
    const decimalDone = popoverState.currentValue / 100;
    
    const payload = { planId: popoverState.entityId, done: decimalDone };

    const onSuccess = () => {
      setPlanData(prevData => {
        const newData = { ...prevData };
        if (popoverState.type === 'BLOCK') {
          newData.sessions = newData.sessions.map(session => ({
            ...session,
            blocks: session.blocks ? session.blocks.map(b => 
              b.id === popoverState.entityId ? { ...b, done: decimalDone } : b
            ) : []
          }));
        } else if (popoverState.type === 'NUTRITION') {
          newData.nutrition = { ...newData.nutrition, done: decimalDone };
        } else if (popoverState.type === 'REST') {
          newData.rest = { ...newData.rest, done: decimalDone };
        }
        return newData;
      });
      closePopover();
    };

    const onErrors = (error) => {
      console.error("Error al actualizar progreso:", error);
      setPopoverState(prev => ({ ...prev, isSaving: false }));
      alert("No se pudo actualizar el progreso. Inténtalo de nuevo.");
    };

    if (popoverState.type === 'BLOCK') {
      backend.planService.updateTrainingBlockDone(payload, onSuccess, onErrors);
    } else if (popoverState.type === 'NUTRITION') {
      backend.planService.updateNutritionPlanDone(payload, onSuccess, onErrors);
    } else if (popoverState.type === 'REST') {
      backend.planService.updateRestPlanDone(payload, onSuccess, onErrors);
    }
  };

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

  const getDynamicBadgeStyle = (decimalValue) => {
    const percentage = Math.round((decimalValue || 0) * 100);
    const hue = Math.round(percentage * 1.2);
    return {
      color: `hsl(${hue}, 80%, 50%)`,
      backgroundColor: `hsla(${hue}, 80%, 50%, 0.15)`
    };
  };

  useEffect(() => {
    const fetchPlan = () => {
      setIsLoading(true);
      const apiDate = getApiDateString(currentDate);
      
      const onSuccess = (data) => {
          setPlanData(data);
          setIsLoading(false);
      };
      const onError = (error) => {
          console.error("Error fetching plan:", error);
          setIsLoading(false);
      };

      if (athleteId) {
          backend.planService.getAthleteDailyPlan(athleteId, apiDate, onSuccess, onError);
      } else {
          backend.planService.getDailyPlan(apiDate, onSuccess, onError);
      }
    };

    fetchPlan();
  }, [currentDate, athleteId]);

  useEffect(() => {
    const closeAllPopovers = () => {
      setReschedulePopover({ activeId: null, newDate: "", newTime: "", isSaving: false });
      
      setPopoverState({ activeId: null, type: null, entityId: null, currentValue: 0, isSaving: false });
    };

    document.addEventListener("click", closeAllPopovers);
    
    return () => document.removeEventListener("click", closeAllPopovers);
  }, []);

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

  const handleToday = () => { setCurrentDate(new Date()); };
  const renderPopover = (id) => {
    if (popoverState.activeId !== id) return null;

    const hue = Math.round(popoverState.currentValue * 1.2);
    const dynamicColor = `hsl(${hue}, 80%, 50%)`;

    return (
      <div className="slider-popover" onClick={e => e.stopPropagation()}>
        <input type="range" min="0" max="100"value={popoverState.currentValue}
          onChange={(e) => setPopoverState({...popoverState, currentValue: Number(e.target.value)})}
          style={{ background: `linear-gradient(to right, ${dynamicColor} ${popoverState.currentValue}%, #0f1115 ${popoverState.currentValue}%)`,'--thumb-color': dynamicColor }}
        />
        <span className="slider-val" style={{ color: dynamicColor }}>
          {popoverState.currentValue}%
        </span>
        <div className="slider-actions">
          <button className="slider-btn save" onClick={handleSavePopover} disabled={popoverState.isSaving}>
            <FaCheck />
          </button>
          <button className="slider-btn cancel" onClick={closePopover} disabled={popoverState.isSaving}>
            <FaTimes />
          </button>
        </div>
      </div>
    );
  };

  const [deleteModal, setDeleteModal] = useState({
    isOpen: false,
    entityId: null,
    entityType: null
  });

  const openDeleteModal = (id, type) => {
    setDeleteModal({ isOpen: true, entityId: id, entityType: type });
  };

  const closeDeleteModal = () => {
    setDeleteModal({ isOpen: false, entityId: null, entityType: null });
  };

  const confirmDelete = () => {
    const { entityId, entityType } = deleteModal;
    
    if (entityType === 'SESSION') {
      backend.planService.deleteTrainingSession(
        entityId,
        () => {
          setPlanData((prevData) => ({
            ...prevData,
            sessions: prevData.sessions.filter((session) => session.id !== entityId)
          }));
          closeDeleteModal();
        },
        (error) => { console.error("Error al eliminar sesión", error); closeDeleteModal(); }
      );
    } 
    else if (entityType === 'NUTRITION') {
      backend.planService.deleteNutritionPlan(
        entityId,
        () => {
          setPlanData((prevData) => ({
            ...prevData,
            nutrition: null
          }));
          closeDeleteModal();
        },
        (error) => { console.error("Error al eliminar nutrición", error); closeDeleteModal(); }
      );
    }
    else if (entityType === 'REST') {
      backend.planService.deleteRestPlan(
        entityId,
        () => {
          setPlanData((prevData) => ({
            ...prevData,
            rest: null
          }));
          closeDeleteModal();
        },
        (error) => { console.error("Error al eliminar descanso", error); closeDeleteModal(); }
      );
    }
  };

  const handleEditSession = (session) => {
    navigate(`/plans/edit-session/${session.id}`, { state: { sessionData: session } });
  };

  const handleEditNutritionPlan = (plan) => {
    navigate(`/plans/edit-nutrition-plan/${plan.id}`, { state: { planData: plan } });
  };

  const handleEditRestPlan = (plan) => {
    navigate(`/plans/edit-rest-plan/${plan.id}`, { state: { planData: plan } });
  };

  const [replanModal, setReplanModal] = useState({
    isOpen: false,
    sessionId: null
  });

  const openReplanModal = (e, sessionId) => {
    e.stopPropagation();
    setReplanModal({ isOpen: true, sessionId: sessionId });
  };

  const closeReplanModal = () => {
    setReplanModal({ isOpen: false, sessionId: null });
  };

  const confirmReplan = () => {
    const sessionId = replanModal.sessionId;
    setIsReplanningId(sessionId);
    closeReplanModal();

    backend.planService.markSessionAsFailedAndReplan(
      sessionId,
      () => {
        setIsReplanningId(null);
        navigate(`/plans/review-proposal/${sessionId}`);
      },
      (error) => {
        console.error("Error al pedir reajuste a la IA:", error);
        setIsReplanningId(null);
        alert("Hubo un error al solicitar el reajuste. Inténtalo de nuevo más tarde.");
      }
    );
  };

  if (isLoading) {
    return (
      <div className="daily-wrapper loading">
        <div className="spinner"></div>
        <p><FormattedMessage id="project.global.messages.loading" /></p>
      </div>
    );
  }

  const isRestDay = !planData?.sessions?.length && !planData?.nutrition && !planData?.rest;

  return (
    <div className="daily-wrapper">
      
      <div className="date-navigator">
        <button className="nav-btn" onClick={handlePrevDay}>
          <FormattedMessage id="project.global.buttons.previous" />
        </button>
        <div className="current-date-display">
          <h2 className="date-text">{getDisplayDate(currentDate)}</h2>
          <button className="today-btn" onClick={handleToday}>Hoy</button>
        </div>
        <button className="nav-btn" onClick={handleNextDay}>
          <FormattedMessage id="project.global.buttons.next" />
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
                              <span className="ce-badge" style={{ backgroundColor: `${sportInfo.color}15`, color: sportInfo.color }} title="Training Stress Score">
                                CE {Math.round(session.ce)}
                              </span>
                              <span className="session-time">{formatTime(session.startTime)}</span>
                              
                              <div className="badge-wrapper" style={{ marginLeft: "auto" }}>
                                {!isCoach && (
                                  <>
                                    <button className="reschedule-icon-btn" onClick={(e) => openReplanModal(e, session.id)} 
                                      title="Sesión fallida (Pedir reajuste automático)" disabled={isReplanningId === session.id}
                                      style={{ color: isReplanningId === session.id ? 'gray' : '#ef4444' }}>
                                      <FaRobot />
                                    </button>
                                    <button className="reschedule-icon-btn" onClick={(e) => openReschedulePopover(e, session.id, session.startTime)}title="Mover a otro día u hora">
                                      <FaCalendarDay />
                                    </button>
                                  </>
                                )}
                                {reschedulePopover.activeId === session.id && (
                                  <div className="slider-popover reschedule-popover" onClick={e => e.stopPropagation()}>
                                    
                                    <div className="reschedule-inputs-column">
                                      <input type="date" value={reschedulePopover.newDate} 
                                        onChange={(e) => setReschedulePopover({...reschedulePopover, newDate: e.target.value})}
                                        className="reschedule-input"
                                      />
                                      <input type="time" value={reschedulePopover.newStartTime} 
                                        onChange={(e) => setReschedulePopover({...reschedulePopover, newStartTime: e.target.value})}
                                        className="reschedule-input"
                                      />
                                    </div>

                                    <div className="slider-actions">
                                      <button className="slider-btn save" onClick={handleSaveReschedule} disabled={reschedulePopover.isSaving}>
                                        <FaCheck />
                                      </button>
                                      <button className="slider-btn cancel" onClick={closeReschedulePopover} disabled={reschedulePopover.isSaving}>
                                        <FaTimes />
                                      </button>
                                    </div>
                                  </div>
                                )}
                              </div>
                          </div>
                          <p className="session-objective">{session.totalDistanceOrDuration} - {session.objective}</p>
                        </div>

                        <div className="blocks-container">
                        {session.blocks && session.blocks.map((block) => (
                            <div key={block.id} className="block-row">
                              <div className="block-left">
                                <div className="badge-wrapper">
                                  <span className={`badge done ${!isCoach ? "clickable" : ""}`} style={getDynamicBadgeStyle(block.done)} onClick={(e) => !isCoach && openPopover(e, `BLOCK-${block.id}`, 'BLOCK', block.id, block.done)} title={!isCoach ? "Actualizar cumplimiento" : "Cumplimiento del atleta"}>
                                    {Math.round((block.done || 0) * 100)}%
                                  </span>
                                  {renderPopover(`BLOCK-${block.id}`)}
                                </div>
                                <div className="block-main">
                                  <span className="block-sets">
                                    {block.sets > 1 ? `${block.sets} x` : ''} {block.reps > 1 ? `${block.reps}` : ''} {block.reps > 1 && block.distanceOrDuration !== '-' ? 'x' : ''} {block.distanceOrDuration !== '-' ? `${block.distanceOrDuration}` : ''}
                                  </span>
                                  <span className="block-name">{block.name}</span>
                                </div>
                              </div>
                              <div className="block-details">
                                {block.pace && block.pace !== "0" && block.pace !== "-" && <span className="badge pace">{block.pace}</span>}
                                {block.rest && block.rest !== "0" && <span className="badge rest"> <FormattedMessage id="project.plans.CreateTrainingSession.rest"/>: {block.rest}</span>}
                              </div>
                            </div>
                        ))}
                        </div>
                        {isCoach && 
                        <div className="session-card-actions">
                          <button 
                            className="btn-icon-edit" 
                            onClick={() => handleEditSession(session)}
                            title="Editar sesión"
                          >
                            <FaEdit />
                          </button>
                          <button 
                            className="btn-icon-danger" 
                            onClick={() => openDeleteModal(session.id, 'SESSION')}
                            title="Eliminar sesión"
                          >
                            <FaTrash />
                          </button>
                        </div>
                        }
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
                    <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "1.5rem" }}>
                        <h3 className="card-title" style={{ margin: 0 }}><FormattedMessage id="project.plans.DailyPlan.nutrition" /></h3>
                        {planData.nutrition && (
                          <div className="badge-wrapper">
                            <span className={`badge done ${!isCoach ? "clickable" : ""}`} style={getDynamicBadgeStyle(planData.nutrition.done)} onClick={(e) => !isCoach && openPopover(e, `NUTRITION-${planData.nutrition.id}`, 'NUTRITION', planData.nutrition.id, planData.nutrition.done)}>
                              {Math.round((planData.nutrition.done || 0) * 100)}%
                            </span>
                            {renderPopover(`NUTRITION-${planData.nutrition.id}`)}
                          </div>
                        )}
                    </div>
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
                        {planData.nutrition.guidelines && planData.nutrition.guidelines !== "-" && (
                            <div className="guidelines">
                            <p>{planData.nutrition.guidelines}</p>
                            </div>
                        )}
                        {isCoach && 
                        <div className="session-card-actions">
                          <button 
                            className="btn-icon-edit" 
                            onClick={() => handleEditNutritionPlan(planData.nutrition)}
                            title="Editar plan nutricional"
                          >
                            <FaEdit />
                          </button>
                          <button 
                            className="btn-icon-danger" 
                            onClick={() => openDeleteModal(planData.nutrition.id, 'NUTRITION')}
                            title="Eliminar plan nutricional"
                          >
                            <FaTrash />
                          </button>
                        </div>
                        }
                        </div>
                    ) : (
                        <p className="not-planned"><FormattedMessage id="project.plans.DailyPlan.noNutritionPlanned" /></p>
                    )}
                </div>

                {/* DESCANSO */}
                <div className="lifestyle-card">
                    <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "1.5rem" }}>
                        <h3 className="card-title" style={{ margin: 0 }}><FormattedMessage id="project.plans.DailyPlan.rest" /></h3>
                        {planData.rest && (
                           <div className="badge-wrapper">
                             <span className={`badge done ${!isCoach ? "clickable" : ""}`} style={getDynamicBadgeStyle(planData.rest.done)} onClick={(e) => !isCoach && openPopover(e, `REST-${planData.rest.id}`, 'REST', planData.rest.id, planData.rest.done)}>
                               {Math.round((planData.rest.done || 0) * 100)}%
                             </span>
                             {renderPopover(`REST-${planData.rest.id}`)}
                           </div>
                        )}
                    </div>
                    {planData.rest ? (
                        <div className="rest-content">
                        <div className="sleep-huge">
                            {planData.rest.targetSleepHours} <span>h</span>                            
                        </div>
                        <p className="sleep-label"><FormattedMessage id="project.plans.DailyPlan.sleepTarget" /></p>
                        
                        {planData.rest.guidelines && planData.rest.guidelines !== "-" && (
                            <div className="guidelines">
                              <p>{planData.rest.guidelines}</p>
                            </div>
                        )}
                        {isCoach && 
                        <div className="session-card-actions">
                          <button 
                            className="btn-icon-edit" 
                            onClick={() => handleEditRestPlan(planData.rest)}
                            title="Editar plan de descanso"
                          >
                            <FaEdit />
                          </button>
                          <button 
                            className="btn-icon-danger" 
                            onClick={() => openDeleteModal(planData.rest.id, 'REST')}
                            title="Eliminar plan de descanso"
                          >
                            <FaTrash />
                          </button>
                        </div>
                        }
                        </div>
                    ) : (
                        <p className="not-planned"><FormattedMessage id="project.plans.DailyPlan.noRestPlanned" /></p>
                    )}
                </div>
            </div>
          </div>
        </div>
      )}
      {deleteModal.isOpen && (
        <div className="athlix-modal-overlay" onClick={closeDeleteModal}>
          <div className="athlix-modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="athlix-modal-header">
              <h3>
                {deleteModal.entityType === 'SESSION' && (
                  <FormattedMessage id="project.plans.DeleteSession.delete" />
                )}
                {deleteModal.entityType === 'NUTRITION' && (
                  <FormattedMessage id="project.plans.DeleteNutritionPlan.delete" />
                )}
                {deleteModal.entityType === 'REST' && (
                  <FormattedMessage id="project.plans.DeleteRestPlan.delete" />
                )}
              </h3>
            </div>
            <div className="athlix-modal-body">
              <p>
                <FormattedMessage id="project.plans.DeleteSession.description" />
                {deleteModal.entityType === 'SESSION' && (
                  <FormattedMessage id="project.plans.DeleteSession.descriptionSession" />
                )}
                {deleteModal.entityType === 'NUTRITION' && (
                  <FormattedMessage id="project.plans.DeleteSession.descriptionPlan" />
                )}
                {deleteModal.entityType === 'REST' && (
                  <FormattedMessage id="project.plans.DeleteSession.descriptionRestPlan" />
                )}
                <FormattedMessage id="project.plans.DeleteSession.descriptionContinued" />
              </p>
            </div>
            <div className="athlix-modal-footer">
              <button className="athlix-btn-cancel" onClick={closeDeleteModal}>
                <FormattedMessage id="project.global.buttons.cancel" />
              </button>
              <button className="athlix-btn-confirm-danger" onClick={confirmDelete}>
                <FormattedMessage id="project.plans.DeleteSession.confirm" />
              </button>
            </div>
          </div>
        </div>
      )}
      {replanModal.isOpen && (
        <div className="athlix-modal-overlay" onClick={closeReplanModal}>
          <div className="athlix-modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="athlix-modal-header">
              <h3><FormattedMessage id="project.plans.DailyPlan.autoAdjustment" /></h3>
            </div>
            <div className="athlix-modal-body">
              <p>
                <FormattedMessage id="project.plans.DailyPlan.adjustmentDescription" />
              </p>
              <p style={{ marginTop: '10px', fontWeight: 'bold' }}>
                <FormattedMessage id="project.plans.DailyPlan.continue" />
              </p>
            </div>
            <div className="athlix-modal-footer">
              <button className="athlix-btn-cancel" onClick={closeReplanModal}>
                <FormattedMessage id="project.global.buttons.cancel" />
              </button>
              <button className="athlix-btn-confirm-danger" onClick={confirmReplan} style={{ backgroundColor: '#22c55e' }}>
                <FormattedMessage id="project.plans.DailyPlan.adjust" />
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default DailyPlan;