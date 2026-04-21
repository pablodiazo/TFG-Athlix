import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { FaClipboardList } from "react-icons/fa";
import { GiNightSleep} from "react-icons/gi";
import { FormattedMessage } from "react-intl";

import backend from "../../../backend"; 
import "../css/CreateRestPlan.css";

const CreateRestPlan = () => {
  const navigate = useNavigate();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [athletes, setAthletes] = useState([]);

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

  const [planData, setPlanData] = useState({
    athleteId: "",
    planDate: "",
    targetSleepHours: "",
    guidelines: ""
  });

  const handleMainChange = (e) => {
    const { name, value } = e.target;
    setPlanData({ ...planData, [name]: value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    setErrorMessage("");
    setIsSubmitting(true);

    const payload = {
      athleteId: Number(planData.athleteId),
      planDate: planData.planDate,
      targetSleepHours: Number(planData.targetSleepHours),
      guidelines: planData.guidelines
    };

    backend.planService.createRestPlan(
      payload,
      (response) => {
        setIsSubmitting(false);
        navigate("/plans/create-session-success"); 
      },
      (errors) => {
        setIsSubmitting(false);
        setErrorMessage("Ya existe un plan para esta fecha o hubo un error. Comprueba los datos.");
        console.error(errors);
      }
    );
  };

  return (
    <div className="athlix-create-wrapper">
      <div className="athlix-create-header">
        <h2><FormattedMessage id="project.plans.CreateRestPlan.header"></FormattedMessage></h2>
        <p><FormattedMessage id="project.plans.CreateRestPlan.disclaimer"></FormattedMessage></p>
      </div>

      <form onSubmit={handleSubmit} className="athlix-create-form">
        
        <div className="athlix-form-section">
          <h3 className="athlix-section-title"><FormattedMessage id="project.plans.CreateRestPlan.firstData"></FormattedMessage></h3>
          <div className="athlix-form-grid-2">
            <div className="athlix-input-group">
              <label><FormattedMessage id="project.plans.CreateRestPlan.athleteName"></FormattedMessage></label>
              <select name="athleteId" value={planData.athleteId} onChange={handleMainChange} required>
                <option value=""><FormattedMessage id="project.plans.CreateRestPlan.selectAthlete" /></option>
                  {athletes.map((athlete) => (
                    <option key={athlete.id} value={athlete.id}>{athlete.firstName} {athlete.lastName}</option>
                  ))}
              </select>
            </div>
            <div className="athlix-input-group">
              <label><FormattedMessage id="project.plans.CreateRestPlan.planDate"></FormattedMessage></label>
              <input type="date" name="planDate" value={planData.planDate} onChange={handleMainChange} required />
            </div>
          </div>
        </div>

        <div className="athlix-form-section">
          <h3 className="athlix-section-title"><FormattedMessage id="project.plans.CreateRestPlan.principalData"></FormattedMessage></h3>
          <div className="athlix-form-grid-1">
            <div className="athlix-input-group">
              <label><GiNightSleep className="icon-blue" /> <FormattedMessage id="project.plans.CreateRestPlan.restHours"></FormattedMessage> (Horas)</label>
              <input type="number" min="0" step="0.1" name="targetSleepHours" value={planData.targetSleepHours} onChange={handleMainChange} required placeholder="Ej: 8.5" />
            </div>
            <div className="athlix-input-group">
              <label><FaClipboardList className="icon-purple" /><FormattedMessage id="project.plans.CreateRestPlan.guidelines"></FormattedMessage></label>
              <textarea 
                name="guidelines" 
                value={planData.guidelines} 
                onChange={handleMainChange} 
                rows="4" 
                placeholder="Ej: Evitar actividades intensas, mantener una buena hidratación, etc."
              />
            </div>
          </div>
        </div>

        {errorMessage && <div className="athlix-alert error">{errorMessage}</div>}

        <div className="athlix-form-actions">
          <button type="submit" className="athlix-btn-primary" disabled={isSubmitting}>
            {isSubmitting ? "Guardando plan..." : "Asignar descanso"}
          </button>
        </div>

      </form>
      
    </div>
  );
};

export default CreateRestPlan;