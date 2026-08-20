import React, { useState } from 'react';
import backend from "../../../backend";
import { FormattedMessage } from "react-intl";

import "../css/ReplanningProposals.css";

const ReplanningProposalReview = ({ proposal, isCoach, onReviewComplete }) => {
    const [isProcessing, setIsProcessing] = useState(false);

    const [confirmModal, setConfirmModal] = useState({
        isOpen: false,
        type: null
    });

    const data = proposal.proposalJson ? JSON.parse(proposal.proposalJson) : null;

    const formatDate = (dateString) => {
        if (!dateString) return "";
        
        const [year, month, day] = dateString.split('-');
        const date = new Date(year, month - 1, day);
        
        const formattedDate = new Intl.DateTimeFormat("es-ES", {
            weekday: "long",
            day: "numeric",
            month: "long",
            year: "numeric"
        }).format(date);

        return formattedDate.charAt(0).toUpperCase() + formattedDate.slice(1);
    };

    const executeAccept = () => {
        setIsProcessing(true);
        backend.planService.acceptProposal(
            proposal.id,
            () => {
                setIsProcessing(false);
                if (onReviewComplete) onReviewComplete();
            },
            (errors) => {
                setIsProcessing(false);
                console.error("Error al aceptar la propuesta:", errors);
                alert("Hubo un error al aplicar los cambios.");
            }
        );
    };

    const executeDeny = () => {
        setIsProcessing(true);
        backend.planService.denyProposal(
            proposal.id,
            () => {
                setIsProcessing(false);
                if (onReviewComplete) onReviewComplete();
            },
            (errors) => {
                setIsProcessing(false);
                console.error("Error al rechazar la propuesta:", errors);
                alert("Hubo un error al rechazar la propuesta.");
            }
        );
    };

    const openConfirmModal = (type) => {
        setConfirmModal({ isOpen: true, type });
    };

    const closeConfirmModal = () => {
        setConfirmModal({ isOpen: false, type: null });
    };

    const handleConfirm = () => {
        const type = confirmModal.type;
        closeConfirmModal();
        
        if (type === 'ACCEPT') {
            executeAccept();
        } else if (type === 'DENY') {
            executeDeny();
        }
    };

    if (!data) return <p><FormattedMessage id="project.plans.ReplanningProposalReview.loading" /></p>;

    return (
        <div className="ai-review-container">
            <h2 className="ai-review-title"><FormattedMessage id="project.plans.ReplanningProposalReview.title" /></h2>
            <p className="ai-reasoning"><strong><FormattedMessage id="project.plans.ReplanningProposalReview.reasoning" />:</strong> {data.readjustmentReasoning}</p>

            <div className="ai-grid">
                <div className="ai-card-section">
                    <h3 className="ai-card-title modified"><FormattedMessage id="project.plans.ReplanningProposalReview.modifiedSessions" /></h3>
                    {data.updatedSessions.length === 0 ? (
                        <p style={{color: '#64748b'}}><FormattedMessage id="project.plans.ReplanningProposalReview.noModifiedSessions" /></p>
                    ) : (
                        data.updatedSessions.map((session, index) => (
                            <div key={index} className="ai-session-item modified-item">
                                <div className="ai-session-header">{session.sport} - {formatDate(session.date)}</div>
                                <div className="ai-session-tss"><FormattedMessage id="project.plans.ReplanningProposalReview.newTSS" />{session.newTss}</div>
                                <ul className="ai-block-list">
                                    {session.updatedBlocks.map((block, bIdx) => (
                                        <li key={bIdx}>{block.sets}x{block.reps} {block.name} ({block.distanceOrDuration})</li>
                                    ))}
                                </ul>
                            </div>
                        ))
                    )}
                </div>

                <div className="ai-card-section">
                    <h3 className="ai-card-title rescheduled"><FormattedMessage id="project.plans.ReplanningProposalReview.rescheduledSession" /></h3>
                    {!data.rescheduledSession ? (
                        <p style={{color: '#64748b'}}><FormattedMessage id="project.plans.ReplanningProposalReview.noRescheduledSession" /></p>
                    ) : (
                        <div className="ai-session-item rescheduled-item">
                            <p className="ai-session-header"><FormattedMessage id="project.plans.ReplanningProposalReview.newDay" />{formatDate(data.rescheduledSession.newDate)}</p>
                            <p className="ai-session-tss"><FormattedMessage id="project.plans.ReplanningProposalReview.tssEstimated" />{data.rescheduledSession.tss}</p>
                            <ul className="ai-block-list">
                                {data.rescheduledSession.blocks.map((block, bIdx) => (
                                    <li key={bIdx}>{block.sets}x{block.reps} {block.name}</li>
                                ))}
                            </ul>
                        </div>
                    )}
                </div>
            </div>

            {proposal.status === 'PENDING' && isCoach && (
                <div className="ai-actions">
                    <button 
                        onClick={() => openConfirmModal('DENY')}
                        disabled={isProcessing}
                        className="ai-btn ai-btn-reject"
                    >
                        Rechazar
                    </button>
                    <button 
                        onClick={() => openConfirmModal('ACCEPT')} 
                        disabled={isProcessing}
                        className="ai-btn ai-btn-accept"
                    >
                        {isProcessing ? <FormattedMessage id="project.global.buttons.processing" /> : <FormattedMessage id="project.global.buttons.confirm" />}
                    </button>
                </div>
            )}

            {proposal.status === 'PENDING' && !isCoach && (
                <div className="ai-alert-pending">
                    <FormattedMessage id="project.plans.ReplanningProposalReview.coachPending" />
                </div>
            )}
            {confirmModal.isOpen && (
                <div className="athlix-modal-overlay" onClick={closeConfirmModal}>
                    <div className="athlix-modal-content" onClick={(e) => e.stopPropagation()}>
                        <div className="athlix-modal-header">
                            <h3>
                                {confirmModal.type === 'ACCEPT' ? <FormattedMessage id="project.plans.ReplanningProposalReview.confirm" /> : <FormattedMessage id="project.plans.ReplanningProposalReview.deny" />}
                            </h3>
                        </div>
                        <div className="athlix-modal-body">
                            <p>
                                {confirmModal.type === 'ACCEPT' 
                                    ? <FormattedMessage id="project.plans.ReplanningProposalReview.confirmModal" /> 
                                    : <FormattedMessage id="project.plans.ReplanningProposalReview.denyModal" />}
                            </p>
                        </div>
                        <div className="athlix-modal-footer">
                            <button className="athlix-btn-cancel" onClick={closeConfirmModal}>
                                <FormattedMessage id="project.global.buttons.cancel" />
                            </button>
                            <button 
                                className={confirmModal.type === 'ACCEPT' ? 'athlix-btn-confirm-success' : 'athlix-btn-confirm-danger'} 
                                onClick={handleConfirm}
                                style={confirmModal.type === 'ACCEPT' ? { backgroundColor: '#22c55e', color: 'white', padding: '0.5rem 1rem', borderRadius: '0.375rem', fontWeight: 'bold' } : {}}
                            >
                                {confirmModal.type === 'ACCEPT' ? <FormattedMessage id="project.plans.ReplanningProposalReview.confirmModalAccept" /> : <FormattedMessage id="project.plans.ReplanningProposalReview.confirmModalDeny" />}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default ReplanningProposalReview;