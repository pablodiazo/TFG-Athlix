import React, { useEffect, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { FaSwimmer, FaBicycle, FaRunning, FaDumbbell, FaExchangeAlt, FaClock, FaPlus, FaTrash } from "react-icons/fa";
import { FormattedMessage } from "react-intl";
import backend from "../../../backend";

import "../css/CreateTrainingSession.css";

const SPORT_OPTIONS = [
  { id: "SWIM", name: "Natación", icon: FaSwimmer, color: "#3b82f6" },
  { id: "BIKE", name: "Ciclismo", icon: FaBicycle, color: "#eab308" },
  { id: "RUN", name: "Carrera", icon: FaRunning, color: "#f97316" },
  { id: "STRENGTH", name: "Fuerza", icon: FaDumbbell, color: "#a855f7" },
  { id: "BRICK", name: "Transición", icon: FaExchangeAlt, color: "#ef4444" },
  { id: "OTHER", name: "Otro", icon: FaClock, color: "#9ca3af" }
];

const PACE_OPTIONS = {
  SWIM: ["Suave", "AER1", "AER2", "AER3", "Fuerte"],
  RUN: ["R0", "R1", "R1+", "R2", "R3", "R3+", "R4", "R5", "R6"],
  BIKE: ["Z1", "Z2", "Z3", "Z4", "Z5", "Z6", "Z7"]
};

const EditTrainingSession = () => {
  const { id } = useParams();
  const location = useLocation();
  const navigate = useNavigate();
  
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [successMessage, setSuccessMessage] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  
  const [sessionData, setSessionData] = useState({
    sessionDate: "",
    startTime: "07:00",
    sport: "RUN",
    objective: "",
    totalDistanceOrDuration: ""
  });

  const [blocks, setBlocks] = useState([]);

  useEffect(() => {
    if (location.state && location.state.sessionData) {
      const s = location.state.sessionData;
      setSessionData({
        sessionDate: s.sessionDate,
        startTime: s.startTime.substring(0, 5), 
        sport: s.sport,
        objective: s.objective || "",
        totalDistanceOrDuration: s.totalDistanceOrDuration || ""
      });
      
      if (s.blocks && s.blocks.length > 0) {
        setBlocks(s.blocks);
      } else {
        setBlocks([{ blockOrder: 1, name: "", sets: 1, reps: 1, distanceOrDuration: "", pace: "", rest: "" }]);
      }
    } else {
      setErrorMessage("No se encontraron los datos de la sesión.");
    }
  }, [location, id]);

  const handleMainChange = (e) => {
    const { name, value } = e.target;
    setSessionData({ ...sessionData, [name]: value });
  };

  const handleSportSelect = (sportId) => {
    setSessionData({ ...sessionData, sport: sportId });

    const resetBlocks = blocks.map(block => ({ 
      ...block, 
      pace: PACE_OPTIONS[sportId] ? "" : "-" 
    }));
    setBlocks(resetBlocks);
  };

  const addBlock = () => {
    setBlocks([
      ...blocks, 
      { blockOrder: blocks.length + 1, name: "", sets: 1, reps: 1, distanceOrDuration: "", pace: "", rest: "" }
    ]);
  };

  const removeBlock = (index) => {
    const newBlocks = [...blocks];
    newBlocks.splice(index, 1);
    const reorderedBlocks = newBlocks.map((b, i) => ({ ...b, blockOrder: i + 1 }));
    setBlocks(reorderedBlocks);
  };

  const handleBlockChange = (index, e) => {
    const { name, value } = e.target;
    const newBlocks = [...blocks];
    newBlocks[index][name] = value;
    setBlocks(newBlocks);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    setErrorMessage("");
    setSuccessMessage("");
    setIsSubmitting(true);

    const data = {
      sessionDate: sessionData.sessionDate,
      startTime: sessionData.startTime + (sessionData.startTime.length === 5 ? ":00" : ""),
      sport: sessionData.sport,
      objective: sessionData.objective,
      totalDistanceOrDuration: sessionData.totalDistanceOrDuration,
      blocks: blocks
    };

    backend.planService.updateTrainingSession(
      id,
      data,
      (response) => {
        setIsSubmitting(false);
        setSuccessMessage("¡Sesión actualizada correctamente!");
        setTimeout(() => navigate(-1), 1500);
      },
      (errors) => {
        setIsSubmitting(false);
        setErrorMessage("Error al actualizar la sesión. Revisa los datos.");
        console.error(errors);
      }
    );
  };

  return (
    <div className="athlix-create-wrapper">
      <div className="athlix-create-header">
        <h2><FormattedMessage id="project.plans.EditSession.title" /></h2>
        <p><FormattedMessage id="project.plans.EditSession.disclaimer" /></p>
      </div>

      {successMessage && <div className="athlix-alert success">{successMessage}</div>}
      {errorMessage && <div className="athlix-alert error">{errorMessage}</div>}

      <form onSubmit={handleSubmit} className="athlix-create-form">
        
        <div className="athlix-form-section">
          <h3 className="athlix-section-title"><FormattedMessage id="project.plans.EditSession.principalData" /></h3>
          <div className="athlix-form-grid">
            <div className="athlix-input-group">
              <label><FormattedMessage id="project.plans.EditSession.sessionDate" /></label>
              <input type="date" name="sessionDate" value={sessionData.sessionDate} onChange={handleMainChange} required />
            </div>
            <div className="athlix-input-group">
              <label><FormattedMessage id="project.plans.EditSession.startTime" /></label>
              <input type="time" name="startTime" value={sessionData.startTime} onChange={handleMainChange} required />
            </div>
          </div>
        </div>

        <div className="athlix-form-section">
          <h3 className="athlix-section-title"><FormattedMessage id="project.plans.EditSession.sport" /></h3>
          <div className="athlix-sport-selector">
            {SPORT_OPTIONS.map((sport) => {
              const Icon = sport.icon;
              const isSelected = sessionData.sport === sport.id;
              return (
                <div 
                  key={sport.id} 
                  className={`athlix-sport-card ${isSelected ? 'active' : ''}`}
                  onClick={() => handleSportSelect(sport.id)}
                  style={{ borderColor: isSelected ? sport.color : 'transparent' }}
                >
                  <Icon className="athlix-sport-card-icon" style={{ color: isSelected ? sport.color : '#9ca3af' }} />
                  <span>{sport.name}</span>
                </div>
              );
            })}
          </div>
        </div>

        <div className="athlix-form-section">
          <h3 className="athlix-section-title"><FormattedMessage id="project.plans.EditSession.objective" /></h3>
          <div className="athlix-form-grid-2">
            <div className="athlix-input-group">
              <label><FormattedMessage id="project.plans.EditSession.objectiveDescription" /></label>
              <input type="text" name="objective" value={sessionData.objective} onChange={handleMainChange} required />
            </div>
            <div className="athlix-input-group">
              <label><FormattedMessage id="project.plans.EditSession.totalDistanceOrDuration" /></label>
              <input type="text" name="totalDistanceOrDuration" value={sessionData.totalDistanceOrDuration} onChange={handleMainChange} required />
            </div>
          </div>
        </div>

        <div className="athlix-form-section">
          <div className="athlix-blocks-header">
            <h3 className="athlix-section-title"><FormattedMessage id="project.plans.EditSession.blocks" /></h3>
            <button type="button" className="athlix-btn-outline" onClick={addBlock}>
              <FaPlus /> <FormattedMessage id="project.plans.EditSession.addBlock" />
            </button>
          </div>

          <div className="athlix-blocks-list">
            {blocks.map((block, index) => (
              <div key={index} className="athlix-block-card">
                <div className="athlix-block-card-header">
                  <h4><FormattedMessage id="project.plans.CreateTrainingSession.block" /> {index + 1}</h4>
                  <button type="button" className="athlix-btn-icon-danger" onClick={() => removeBlock(index)}>
                    <FaTrash />
                  </button>
                </div>
                
                <div className="athlix-block-grid">
                  <div className="athlix-input-group full-width">
                    <label><FormattedMessage id="project.plans.EditSession.name" /></label>
                    <input type="text" name="name" value={block.name} onChange={(e) => handleBlockChange(index, e)} required />
                  </div>
                  <div className="athlix-input-group">
                    <label><FormattedMessage id="project.plans.EditSession.sets" /></label>
                    <input type="number" min="1" name="sets" value={block.sets} onChange={(e) => handleBlockChange(index, e)} required />
                  </div>
                  <div className="athlix-input-group">
                    <label><FormattedMessage id="project.plans.EditSession.repetitions" /></label>
                    <input type="number" min="1" name="reps" value={block.reps} onChange={(e) => handleBlockChange(index, e)} required />
                  </div>
                  <div className="athlix-input-group">
                    <label><FormattedMessage id="project.plans.EditSession.distanceOrDuration" /></label>
                    <input type="text" name="distanceOrDuration" value={block.distanceOrDuration} onChange={(e) => handleBlockChange(index, e)} required />
                  </div>
                  <div className="athlix-input-group">
                    <label><FormattedMessage id="project.plans.CreateTrainingSession.pace" /></label>
                      {PACE_OPTIONS[sessionData.sport] ? (
                        <select 
                          name="pace" 
                          value={block.pace || ""} 
                          onChange={(e) => handleBlockChange(index, e)}
                          required
                        >
                          <option value=""><FormattedMessage id="project.plans.CreateTrainingSession.selectZone" /></option>
                          {PACE_OPTIONS[sessionData.sport].map(zone => (<option key={zone} value={zone}>{zone}</option>))}
                        </select>
                      ) : (
                        <input type="text" name="pace" value="-" disabled title="No se aplican zonas de intensidad a este deporte" style={{ opacity: 0.5, cursor: "not-allowed" }}/>
                      )}                                        
                  </div>                                      
                  <div className="athlix-input-group">
                    <label><FormattedMessage id="project.plans.EditSession.rest" /></label>
                    <input type="text" name="rest" value={block.rest || ""} onChange={(e) => handleBlockChange(index, e)} />
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="athlix-form-actions">
          <button type="submit" className="athlix-btn-primary" disabled={isSubmitting}>
            {isSubmitting ? <FormattedMessage id="project.global.buttons.savingChanges" /> : <FormattedMessage id="project.global.buttons.saveChanges" />}
          </button>
        </div>

      </form>
    </div>
  );
};

export default EditTrainingSession;