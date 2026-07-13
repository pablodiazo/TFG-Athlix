import React, { useEffect, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { FaFire, FaClipboardList } from "react-icons/fa";
import { GiSteak, GiSlicedBread, GiAvocado, GiWaterBottle } from "react-icons/gi";
import { FormattedMessage } from "react-intl";

import backend from "../../../backend"; 
import "../css/CreateNutritionPlan.css";

const EditNutritionPlan = () => {
  const { id } = useParams();
  const location = useLocation();
  const navigate = useNavigate();
  
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  const [planData, setPlanData] = useState({
    planDate: "",
    targetCalories: "",
    proteinGrams: "",
    carbsGrams: "",
    fatGrams: "",
    hydrationLiters: "",
    guidelines: ""
  });

  useEffect(() => {
    if (location.state && location.state.planData) {
      const p = location.state.planData;
      setPlanData({
        planDate: p.planDate || "",
        targetCalories: p.targetCalories || "",
        proteinGrams: p.proteinGrams || "",
        carbsGrams: p.carbsGrams || "",
        fatGrams: p.fatGrams || "",
        hydrationLiters: p.hydrationLiters || "",
        guidelines: p.guidelines || ""
      });
    } else {
      setErrorMessage("No se encontraron los datos del plan nutricional.");
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
      targetCalories: Number(planData.targetCalories),
      proteinGrams: Number(planData.proteinGrams),
      carbsGrams: Number(planData.carbsGrams),
      fatGrams: Number(planData.fatGrams),
      hydrationLiters: Number(planData.hydrationLiters),
      guidelines: planData.guidelines
    };

    backend.planService.updateNutritionPlan(
      id,
      payload,
      (response) => {
        setIsSubmitting(false);
        setSuccessMessage("¡Plan nutricional actualizado correctamente!");
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
        <h2><FormattedMessage id="project.plans.EditNutritionPlan.title" /></h2>
        <p><FormattedMessage id="project.plans.EditNutritionPlan.disclaimer" /></p>
      </div>

      {successMessage && <div className="athlix-alert success" style={{ padding: '1rem', borderRadius: '8px', marginBottom: '2rem', backgroundColor: 'rgba(34, 197, 94, 0.2)', color: '#4ade80', border: '1px solid #22c55e' }}>{successMessage}</div>}
      {errorMessage && <div className="athlix-alert error">{errorMessage}</div>}

      <form onSubmit={handleSubmit} className="athlix-create-form">
        
        <div className="athlix-form-section">
          <h3 className="athlix-section-title"><FormattedMessage id="project.plans.CreateNutritionPlan.principalData" /></h3>
          <div className="athlix-form-grid-1">
            <div className="athlix-input-group">
              <label><FormattedMessage id="project.plans.CreateNutritionPlan.planDate" /></label>
              <input type="date" name="planDate" value={planData.planDate} onChange={handleMainChange} required />
            </div>
          </div>
        </div>

        <div className="athlix-form-section">
          <h3 className="athlix-section-title"><FormattedMessage id="project.plans.CreateNutritionPlan.macros" /></h3>
          
          <div className="athlix-macro-grid">
            <div className="athlix-input-group">
              <label><FaFire className="icon-orange" /><FormattedMessage id="project.plans.CreateNutritionPlan.calories" /> (Kcal)</label>
              <input type="number" min="0" name="targetCalories" value={planData.targetCalories} onChange={handleMainChange} required />
            </div>
            <div className="athlix-input-group">
              <label><GiSteak className="icon-red" /><FormattedMessage id="project.plans.CreateNutritionPlan.proteins" /> (g)</label>
              <input type="number" min="0" name="proteinGrams" value={planData.proteinGrams} onChange={handleMainChange} required />
            </div>
            <div className="athlix-input-group">
              <label><GiSlicedBread className="icon-yellow" /><FormattedMessage id="project.plans.CreateNutritionPlan.carbohydrates" /> (g)</label>
              <input type="number" min="0" name="carbsGrams" value={planData.carbsGrams} onChange={handleMainChange} required />
            </div>
            <div className="athlix-input-group">
              <label><GiAvocado className="icon-green" /><FormattedMessage id="project.plans.CreateNutritionPlan.fats" /> (g)</label>
              <input type="number" min="0" name="fatGrams" value={planData.fatGrams} onChange={handleMainChange} required />
            </div>
          </div>
        </div>

        <div className="athlix-form-section">
          <h3 className="athlix-section-title"><FormattedMessage id="project.plans.CreateNutritionPlan.extras" /></h3>
          <div className="athlix-form-grid-1">
            <div className="athlix-input-group">
              <label><GiWaterBottle className="icon-blue" /> <FormattedMessage id="project.plans.CreateNutritionPlan.hidratation" /></label>
              <input type="number" min="0" step="0.1" name="hydrationLiters" value={planData.hydrationLiters} onChange={handleMainChange} required />
            </div>
            <div className="athlix-input-group">
              <label><FaClipboardList className="icon-purple" /><FormattedMessage id="project.plans.CreateNutritionPlan.guidelines" /></label>
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

export default EditNutritionPlan;