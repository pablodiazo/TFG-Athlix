import React, { useEffect, useState } from "react";
import { FaSwimmer, FaBicycle, FaRunning, FaDumbbell, FaExchangeAlt, FaClock, FaPlus, FaTrash } from "react-icons/fa";
import { FormattedMessage } from "react-intl";
import backend from "../../../backend";
import { useNavigate } from "react-router-dom";

import "../css/CreateTrainingSession.css";

const SPORT_OPTIONS = [
  { id: "SWIM", name: "Natación", icon: FaSwimmer, color: "#3b82f6" },
  { id: "BIKE", name: "Ciclismo", icon: FaBicycle, color: "#eab308" },
  { id: "RUN", name: "Carrera", icon: FaRunning, color: "#f97316" },
  { id: "STRENGTH", name: "Fuerza", icon: FaDumbbell, color: "#a855f7" },
  { id: "BRICK", name: "Transición", icon: FaExchangeAlt, color: "#ef4444" },
  { id: "OTHER", name: "Otro", icon: FaClock, color: "#9ca3af" }
];

const CreateTrainingSession = () => {
  const navigate = useNavigate();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [successMessage, setSuccessMessage] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [athletes, setAthletes] = useState([]);

  const [sessionData, setSessionData] = useState({
    athleteId: "",
    sessionDate: "",
    startTime: "07:00",
    sport: "RUN",
    objective: "",
    totalDistanceOrDuration: ""
  });

  const [blocks, setBlocks] = useState([
    { blockOrder: 1, name: "Calentamiento", sets: 1, reps: 1, distanceOrDuration: "", pace: "", rest: "" }
  ]);

  useEffect(() => {
    backend.userService.getAthletesByCoach(
      (data) => {
        setAthletes(data);
      },
      (errors) => {
        console.error(errors);
      }
    );
  }, []);

  const handleMainChange = (e) => {
    const { name, value } = e.target;
    setSessionData({ ...sessionData, [name]: value });
  };

  const handleSportSelect = (sportId) => {
    setSessionData({ ...sessionData, sport: sportId });
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
    setBlocks(newBlocks);
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
      athleteId: Number(sessionData.athleteId),
      sessionDate: sessionData.sessionDate,
      startTime: sessionData.startTime + ":00",
      sport: sessionData.sport,
      objective: sessionData.objective,
      totalDistanceOrDuration: sessionData.totalDistanceOrDuration,
      blocks: blocks
    };

    backend.planService.createTrainingSession(
      data,
      (response) => {
        setIsSubmitting(false);
        navigate("/plans/create-session-success");
      },
      (errors) => {
        setIsSubmitting(false);
        setErrorMessage("Error al crear la sesión. Revisa los datos.");
        console.error(errors);
      }
    );
  };

  return (
    <div className="athlix-create-wrapper">
      <div className="athlix-create-header">
        <h2><FormattedMessage id="project.plans.CreateTrainingSession.header" /></h2>
        <p><FormattedMessage id="project.plans.CreateTrainingSession.disclaimer" /></p>
      </div>

      {successMessage && <div className="athlix-alert success">{successMessage}</div>}
      {errorMessage && <div className="athlix-alert error">{errorMessage}</div>}

      <form onSubmit={handleSubmit} className="athlix-create-form">
        
        <div className="athlix-form-section">
          <h3 className="athlix-section-title"><FormattedMessage id="project.plans.CreateTrainingSession.principalData" /></h3>
          <div className="athlix-form-grid">
            <div className="athlix-input-group">
              <label><FormattedMessage id="project.plans.CreateTrainingSession.athleteName" /></label>
              <select name="athleteId" value={sessionData.athleteId} onChange={handleMainChange} required>
                <option value=""><FormattedMessage id="project.plans.CreateTrainingSession.selectAthlete" /></option>
                {athletes.map((athlete) => (
                  <option key={athlete.id} value={athlete.id}>{athlete.firstName} {athlete.lastName}</option>
                ))}
              </select>
            </div>
            <div className="athlix-input-group">
              <label><FormattedMessage id="project.plans.CreateTrainingSession.sessionDate" /></label>
              <input type="date" name="sessionDate" value={sessionData.sessionDate} onChange={handleMainChange} required />
            </div>
            <div className="athlix-input-group">
              <label><FormattedMessage id="project.plans.CreateTrainingSession.startTime" /></label>
              <input type="time" name="startTime" value={sessionData.startTime} onChange={handleMainChange} required />
            </div>
          </div>
        </div>

        <div className="athlix-form-section">
          <h3 className="athlix-section-title"><FormattedMessage id="project.plans.CreateTrainingSession.sport" /></h3>
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
          <h3 className="athlix-section-title"><FormattedMessage id="project.plans.CreateTrainingSession.aim" /></h3>
          <div className="athlix-form-grid-2">
            <div className="athlix-input-group">
              <label><FormattedMessage id="project.plans.CreateTrainingSession.objective" /></label>
              <input type="text" name="objective" value={sessionData.objective} onChange={handleMainChange} required placeholder="Ej: Trabajo de umbral anaeróbico" />
            </div>
            <div className="athlix-input-group">
              <label><FormattedMessage id="project.plans.CreateTrainingSession.totalDistanceOrDuration" /></label>
              <input type="text" name="totalDistanceOrDuration" value={sessionData.totalDistanceOrDuration} onChange={handleMainChange} required placeholder="Ej: 2500m o 1h 30m" />
            </div>
          </div>
        </div>

        <div className="athlix-form-section">
          <div className="athlix-blocks-header">
            <h3 className="athlix-section-title"><FormattedMessage id="project.plans.CreateTrainingSession.blocks" /></h3>
            <button type="button" className="athlix-btn-outline" onClick={addBlock}>
              <FaPlus /> <FormattedMessage id="project.plans.CreateTrainingSession.addBlock" />
            </button>
          </div>

          <div className="athlix-blocks-list">
            {blocks.map((block, index) => (
              <div key={index} className="athlix-block-card">
                <div className="athlix-block-card-header">
                  <h4><FormattedMessage id="project.plans.CreateTrainingSession.block" /> {index + 1}</h4>
                  {blocks.length > 1 && (
                    <button type="button" className="athlix-btn-icon-danger" onClick={() => removeBlock(index)}>
                      <FaTrash />
                    </button>
                  )}
                </div>
                
                <div className="athlix-block-grid">
                  <div className="athlix-input-group full-width">
                    <label><FormattedMessage id="project.plans.CreateTrainingSession.name" /></label>
                    <input type="text" name="name" value={block.name} onChange={(e) => handleBlockChange(index, e)} required placeholder="Ej: Series principales" />
                  </div>
                  <div className="athlix-input-group">
                    <label><FormattedMessage id="project.plans.CreateTrainingSession.sets" /></label>
                    <input type="number" min="1" name="sets" value={block.sets} onChange={(e) => handleBlockChange(index, e)} required />
                  </div>
                  <div className="athlix-input-group">
                    <label><FormattedMessage id="project.plans.CreateTrainingSession.repetitions" /></label>
                    <input type="number" min="1" name="reps" value={block.reps} onChange={(e) => handleBlockChange(index, e)} required />
                  </div>
                  <div className="athlix-input-group">
                    <label><FormattedMessage id="project.plans.CreateTrainingSession.distanceOrDuration" /></label>
                    <input type="text" name="distanceOrDuration" value={block.distanceOrDuration} onChange={(e) => handleBlockChange(index, e)} required placeholder="Ej: 400m" />
                  </div>
                  <div className="athlix-input-group">
                    <label><FormattedMessage id="project.plans.CreateTrainingSession.pace" /></label>
                    <input type="text" name="pace" value={block.pace} onChange={(e) => handleBlockChange(index, e)} placeholder="Ej: Z4 o 4:30 min/km" />
                  </div>
                  <div className="athlix-input-group">
                    <label><FormattedMessage id="project.plans.CreateTrainingSession.rest" /></label>
                    <input type="text" name="rest" value={block.rest} onChange={(e) => handleBlockChange(index, e)} placeholder="Ej: 15'' o 1 min" />
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="athlix-form-actions">
          <button type="submit" className="athlix-btn-primary" disabled={isSubmitting}>
            {isSubmitting ? "Guardando..." : "Crear Sesión para Atleta"}
          </button>
        </div>

      </form>
    </div>
  );
};

export default CreateTrainingSession;