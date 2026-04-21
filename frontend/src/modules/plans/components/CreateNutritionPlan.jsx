import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { FaFire, FaClipboardList } from "react-icons/fa";
import { GiSteak, GiSlicedBread, GiAvocado, GiWaterBottle } from "react-icons/gi";
import { FormattedMessage } from "react-intl";

import backend from "../../../backend"; 
import "../css/CreateNutritionPlan.css";

const CreateNutritionPlan = () => {
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
    targetCalories: "",
    proteinGrams: "",
    carbsGrams: "",
    fatGrams: "",
    hydrationLiters: "",
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
      targetCalories: Number(planData.targetCalories),
      proteinGrams: Number(planData.proteinGrams),
      carbsGrams: Number(planData.carbsGrams),
      fatGrams: Number(planData.fatGrams),
      hydrationLiters: Number(planData.hydrationLiters),
      guidelines: planData.guidelines
    };

    backend.planService.createNutritionPlan(
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
        <h2><FormattedMessage id="project.plans.CreateNutritionPlan.header"></FormattedMessage></h2>
        <p><FormattedMessage id="project.plans.CreateNutritionPlan.disclaimer"></FormattedMessage></p>
      </div>

      <form onSubmit={handleSubmit} className="athlix-create-form">
        
        <div className="athlix-form-section">
          <h3 className="athlix-section-title"><FormattedMessage id="project.plans.CreateNutritionPlan.principalData"></FormattedMessage></h3>
          <div className="athlix-form-grid-2">
            <div className="athlix-input-group">
              <label><FormattedMessage id="project.plans.CreateNutritionPlan.athleteName"></FormattedMessage></label>
              <select name="athleteId" value={planData.athleteId} onChange={handleMainChange} required>
                <option value=""><FormattedMessage id="project.plans.CreateNutritionPlan.selectAthlete" /></option>
                  {athletes.map((athlete) => (
                    <option key={athlete.id} value={athlete.id}>{athlete.firstName} {athlete.lastName}</option>
                  ))}
              </select>
            </div>
            <div className="athlix-input-group">
              <label><FormattedMessage id="project.plans.CreateNutritionPlan.planDate"></FormattedMessage></label>
              <input type="date" name="planDate" value={planData.planDate} onChange={handleMainChange} required />
            </div>
          </div>
        </div>

        <div className="athlix-form-section">
          <h3 className="athlix-section-title"><FormattedMessage id="project.plans.CreateNutritionPlan.macros"></FormattedMessage></h3>
          
          <div className="athlix-macro-grid">
            <div className="athlix-input-group">
              <label><FaFire className="icon-orange" /><FormattedMessage id="project.plans.CreateNutritionPlan.calories"></FormattedMessage> (Kcal)</label>
              <input type="number" min="0" name="targetCalories" value={planData.targetCalories} onChange={handleMainChange} required placeholder="Ej: 3000" />
            </div>
            <div className="athlix-input-group">
              <label><GiSteak className="icon-red" /><FormattedMessage id="project.plans.CreateNutritionPlan.proteins"></FormattedMessage> (g)</label>
              <input type="number" min="0" name="proteinGrams" value={planData.proteinGrams} onChange={handleMainChange} required placeholder="Ej: 150" />
            </div>
            <div className="athlix-input-group">
              <label><GiSlicedBread className="icon-yellow" /><FormattedMessage id="project.plans.CreateNutritionPlan.carbohydrates"></FormattedMessage> (g)</label>
              <input type="number" min="0" name="carbsGrams" value={planData.carbsGrams} onChange={handleMainChange} required placeholder="Ej: 400" />
            </div>
            <div className="athlix-input-group">
              <label><GiAvocado className="icon-green" /><FormattedMessage id="project.plans.CreateNutritionPlan.fats"></FormattedMessage> (g)</label>
              <input type="number" min="0" name="fatGrams" value={planData.fatGrams} onChange={handleMainChange} required placeholder="Ej: 80" />
            </div>
          </div>
        </div>

        <div className="athlix-form-section">
          <h3 className="athlix-section-title"><FormattedMessage id="project.plans.CreateNutritionPlan.extras"></FormattedMessage></h3>
          <div className="athlix-form-grid-1">
            <div className="athlix-input-group">
              <label><GiWaterBottle className="icon-blue" /> <FormattedMessage id="project.plans.CreateNutritionPlan.hidratation"></FormattedMessage></label>
              <input type="number" min="0" step="0.1" name="hydrationLiters" value={planData.hydrationLiters} onChange={handleMainChange} required placeholder="Ej: 3.5" />
            </div>
            <div className="athlix-input-group">
              <label><FaClipboardList className="icon-purple" /><FormattedMessage id="project.plans.CreateNutritionPlan.guidelines"></FormattedMessage></label>
              <textarea 
                name="guidelines" 
                value={planData.guidelines} 
                onChange={handleMainChange} 
                rows="4" 
                placeholder="Ej: Tomar batido de recuperación 30 min después del entrenamiento de la tarde..."
              />
            </div>
          </div>
        </div>

        {errorMessage && <div className="athlix-alert error">{errorMessage}</div>}

        <div className="athlix-form-actions">
          <button type="submit" className="athlix-btn-primary" disabled={isSubmitting}>
            {isSubmitting ? "Guardando plan..." : "Asignar nutrición"}
          </button>
        </div>

      </form>
      
    </div>
  );
};

export default CreateNutritionPlan;