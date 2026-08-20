import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import { FormattedMessage } from "react-intl";
import backend from "../../../backend";
import { ReplanningProposalReview } from "../../plans";
import users from "../../users";

import "../css/ReplanningProposals.css";

const AiProposalPage = () => {
    const { sessionId } = useParams();
    const navigate = useNavigate();

    const user = useSelector(users.selectors.getUser);
    
    const isCoach = user !== null && user.role === 'COACH';
    
    const [proposal, setProposal] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(false);

    useEffect(() => {
        setIsLoading(true);
        backend.planService.getPendingAiProposal(
            sessionId,
            (data) => {
                setProposal(data);
                setIsLoading(false);
            },
            (err) => {
                console.error("Error al cargar la propuesta:", err);
                setError(true);
                setIsLoading(false);
            }
        );
    }, [sessionId]);

    const handleReviewComplete = () => {
        navigate("/plans/athletes");
    };

    if (isLoading) {
        return (
            <div className="ai-proposal-wrapper">
                <p className="text-gray-600 text-lg"><FormattedMessage id="project.plans.ReplanningProposals.loading" /></p>
            </div>
        );
    }

    if (error || !proposal) {
        return (
            <div className="ai-proposal-wrapper">
                <p className="text-red-500 text-lg font-bold mb-4"><FormattedMessage id="project.plans.ReplanningProposals.noCharging" /></p>
                <button 
                    onClick={() => navigate(-1)} 
                    className="ai-back-btn"
                >
                    <FormattedMessage id="project.plans.ReplanningProposals.back" />
                </button>
            </div>
        );
    }

    return (
        <div className="ai-proposal-wrapper">
            <button 
                onClick={() => navigate(-1)}
                className="ai-back-btn"
            >
                &larr; <FormattedMessage id="project.global.buttons.back" />
            </button>
            
            <ReplanningProposalReview 
                proposal={proposal} 
                isCoach={isCoach} 
                onReviewComplete={handleReviewComplete} 
            />
        </div>
    );
};

export default AiProposalPage;