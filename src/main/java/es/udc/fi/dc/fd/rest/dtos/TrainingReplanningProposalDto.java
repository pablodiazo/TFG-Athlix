package es.udc.fi.dc.fd.rest.dtos;

public class TrainingReplanningProposalDto {

    private Long id;
    private Long athleteId;
    private Long coachId;
    private Long failedSessionId;
    private String proposalJson;
    private String status;
    private String creationDate;

    public TrainingReplanningProposalDto() {}

    public TrainingReplanningProposalDto(Long id, Long athleteId, Long coachId, Long failedSessionId, String proposalJson, String status, String creationDate) {
        this.id = id;
        this.athleteId = athleteId;
        this.coachId = coachId;
        this.failedSessionId = failedSessionId;
        this.proposalJson = proposalJson;
        this.status = status;
        this.creationDate = creationDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAthleteId() { return athleteId; }
    public void setAthleteId(Long athleteId) { this.athleteId = athleteId; }
    public Long getCoachId() { return coachId; }
    public void setCoachId(Long coachId) { this.coachId = coachId; }
    public Long getFailedSessionId() { return failedSessionId; }
    public void setFailedSessionId(Long failedSessionId) { this.failedSessionId = failedSessionId; }
    public String getProposalJson() { return proposalJson; }
    public void setProposalJson(String proposalJson) { this.proposalJson = proposalJson; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCreationDate() { return creationDate; }
    public void setCreationDate(String creationDate) { this.creationDate = creationDate; }
}