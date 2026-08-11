import React, { useState } from 'react';
import backend from "../../../backend";

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

    if (!data) return <p>Cargando datos de la propuesta...</p>;

    return (
        <div className="ai-review-container">
            <h2 className="ai-review-title">Reajuste sugerido por el LLM</h2>
            <p className="ai-reasoning"><strong>Razonamiento:</strong> {data.readjustmentReasoning}</p>

            <div className="ai-grid">
                <div className="ai-card-section">
                    <h3 className="ai-card-title modified">Sesiones Modificadas</h3>
                    {data.updatedSessions.length === 0 ? (
                        <p style={{color: '#64748b'}}>No hay alteraciones en otras sesiones.</p>
                    ) : (
                        data.updatedSessions.map((session, index) => (
                            <div key={index} className="ai-session-item modified-item">
                                <div className="ai-session-header">{session.sport} - {formatDate(session.date)}</div>
                                <div className="ai-session-tss">Nuevo TSS: {session.newTss}</div>
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
                    <h3 className="ai-card-title rescheduled">Sesión Recolocada</h3>
                    {!data.rescheduledSession ? (
                        <p style={{color: '#64748b'}}>La sesión fallida no se ha movido a otro día.</p>
                    ) : (
                        <div className="ai-session-item rescheduled-item">
                            <p className="ai-session-header">Nuevo Día: {formatDate(data.rescheduledSession.newDate)}</p>
                            <p className="ai-session-tss">TSS Estimado: {data.rescheduledSession.tss}</p>
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
                        {isProcessing ? 'Procesando...' : 'Aceptar Propuesta'}
                    </button>
                </div>
            )}

            {proposal.status === 'PENDING' && !isCoach && (
                <div className="ai-alert-pending">
                    Pendiente de revisión por tu entrenador.
                </div>
            )}
            {confirmModal.isOpen && (
                <div className="athlix-modal-overlay" onClick={closeConfirmModal}>
                    <div className="athlix-modal-content" onClick={(e) => e.stopPropagation()}>
                        <div className="athlix-modal-header">
                            <h3>
                                {confirmModal.type === 'ACCEPT' ? 'Aplicar Reajuste' : 'Rechazar Propuesta'}
                            </h3>
                        </div>
                        <div className="athlix-modal-body">
                            <p>
                                {confirmModal.type === 'ACCEPT' 
                                    ? '¿Estás seguro de que quieres aplicar estos cambios en la semana del atleta? Esta acción modificará su calendario de forma inmediata.' 
                                    : '¿Estás seguro de que deseas descartar esta propuesta? El atleta será notificado de tu decisión.'}
                            </p>
                        </div>
                        <div className="athlix-modal-footer">
                            <button className="athlix-btn-cancel" onClick={closeConfirmModal}>
                                Cancelar
                            </button>
                            <button 
                                className={confirmModal.type === 'ACCEPT' ? 'athlix-btn-confirm-success' : 'athlix-btn-confirm-danger'} 
                                onClick={handleConfirm}
                                style={confirmModal.type === 'ACCEPT' ? { backgroundColor: '#22c55e', color: 'white', padding: '0.5rem 1rem', borderRadius: '0.375rem', fontWeight: 'bold' } : {}}
                            >
                                {confirmModal.type === 'ACCEPT' ? 'Sí, Aplicar' : 'Sí, Rechazar'}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default ReplanningProposalReview;