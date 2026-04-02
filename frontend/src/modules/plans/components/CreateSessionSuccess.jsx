import React from "react";
import { useNavigate } from "react-router-dom";
import { FormattedMessage } from "react-intl";
import { FaCheckCircle, FaPlus, FaUser } from "react-icons/fa";
import "../css/CreateSessionSuccess.css";

const CreateSessionSuccess = () => {
  const navigate = useNavigate();

  return (
    <div className="athlix-success-wrapper">
      <div className="athlix-success-card">
        
        <div className="athlix-success-icon-container">
          <FaCheckCircle className="athlix-success-icon" />
        </div>
        
        <h2 className="athlix-success-title">
          <FormattedMessage id="project.plans.CreateSessionSuccess.title" />
        </h2>
        <p className="athlix-success-text">
          <FormattedMessage id="project.plans.CreateSessionSuccess.text" />
        </p>
        
        <div className="athlix-success-actions">
          <button 
            className="athlix-btn-primary" 
            onClick={() => navigate('/plans/create-training-session')}
          >
            <FaPlus /> <FormattedMessage id="project.plans.CreateSessionSuccess.actions" />
          </button>
          
          <button 
            className="athlix-btn-outline" 
            onClick={() => navigate('/users/profile')}
          >
            <FaUser /> <FormattedMessage id="project.plans.CreateSessionSuccess.view" />
          </button>
        </div>

      </div>
    </div>
  );
};

export default CreateSessionSuccess;