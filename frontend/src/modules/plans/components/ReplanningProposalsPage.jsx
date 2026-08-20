import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
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
                <p className="text-gray-600 text-lg">Cargando la propuesta de la IA...</p>
            </div>
        );
    }

    if (error || !proposal) {
        return (
            <div className="ai-proposal-wrapper">
                <p className="text-red-500 text-lg font-bold mb-4">No se pudo cargar la propuesta.</p>
                <p className="text-gray-600 mb-4">Es posible que ya haya sido revisada o cancelada.</p>
                <button 
                    onClick={() => navigate(-1)} 
                    className="ai-back-btn"
                >
                    Volver atrás
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
                &larr; Volver
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