import React, { useEffect, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { FaClipboardList } from "react-icons/fa";
import { GiNightSleep } from "react-icons/gi";
import { FormattedMessage } from "react-intl";

import backend from "../../../backend"; 
import "../css/CreateRestPlan.css";

const EditRestPlan = () => {
  const { id } = useParams();
  const location = useLocation();
  const navigate = useNavigate();
  
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  const [planData, setPlanData] = useState({
    planDate: "",
    targetSleepHours: "",
    guidelines: ""
  });

  useEffect(() => {
    if (location.state && location.state.planData) {
      const p = location.state.planData;
      setPlanData({
        planDate: p.planDate || "",
        targetSleepHours: p.targetSleepHours || "",
        guidelines: p.guidelines || ""
      });
    } else {
      setErrorMessage("No se encontraron los datos del plan de descanso.");
    }
  }, [location, id]);

  const handleMainChange = (e) => {
    const { name, value } = e.target;
    setPlanData({ ...planData, [name]: value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    setErrorMessage("");
    setSuccessMessage("");
    setIsSubmitting(true);

    const payload = {
      planDate: planData.planDate,
      targetSleepHours: Number(planData.targetSleepHours),
      guidelines: planData.guidelines
    };

    backend.planService.updateRestPlan(
      id,
      payload,
      (response) => {
        setIsSubmitting(false);
        setSuccessMessage("¡Plan de descanso actualizado correctamente!");
        setTimeout(() => navigate(-1), 1500);
      },
      (errors) => {
        setIsSubmitting(false);
        setErrorMessage("Hubo un error al actualizar el plan. Comprueba los datos.");
        console.error(errors);
      }
    );
  };

  return (
    <div className="athlix-create-wrapper">
      <div className="athlix-create-header">
        <h2><FormattedMessage id="project.plans.EditRestPlan.title" /></h2>
        <p><FormattedMessage id="project.plans.EditRestPlan.disclaimer" /></p>
      </div>

      {successMessage && <div className="athlix-alert success" style={{ padding: '1rem', borderRadius: '8px', marginBottom: '2rem', backgroundColor: 'rgba(34, 197, 94, 0.2)', color: '#4ade80', border: '1px solid #22c55e' }}>{successMessage}</div>}
      {errorMessage && <div className="athlix-alert error">{errorMessage}</div>}

      <form onSubmit={handleSubmit} className="athlix-create-form">
        
        <div className="athlix-form-section">
          <h3 className="athlix-section-title"><FormattedMessage id="project.plans.CreateRestPlan.firstData" /></h3>
          <div className="athlix-form-grid-1">
            <div className="athlix-input-group">
              <label><FormattedMessage id="project.plans.CreateRestPlan.planDate" /></label>
              <input type="date" name="planDate" value={planData.planDate} onChange={handleMainChange} required />
            </div>
          </div>
        </div>

        <div className="athlix-form-section">
          <h3 className="athlix-section-title"><FormattedMessage id="project.plans.CreateRestPlan.principalData" /></h3>
          <div className="athlix-form-grid-1">
            <div className="athlix-input-group">
              <label><GiNightSleep className="icon-blue" /> <FormattedMessage id="project.plans.CreateRestPlan.restHours" /> (Horas)</label>
              <input type="number" min="0" step="0.1" name="targetSleepHours" value={planData.targetSleepHours} onChange={handleMainChange} required />
            </div>
            <div className="athlix-input-group">
              <label><FaClipboardList className="icon-purple" /><FormattedMessage id="project.plans.CreateRestPlan.guidelines" /></label>
              <textarea 
                name="guidelines" 
                value={planData.guidelines} 
                onChange={handleMainChange} 
                rows="4" 
              />
            </div>
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

export default EditRestPlan;