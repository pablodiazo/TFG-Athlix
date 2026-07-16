package es.udc.fi.dc.fd.rest.dtos;

public class CoachRequestDto {

    private Long id;
    private Long coachId;
    private String coachFirstName;
    private String coachLastName;
    private String status;
    private String athleteFirstName;
    private String athleteLastName;
    private String athleteEmail;

    public CoachRequestDto() {}

    public CoachRequestDto(Long id, Long coachId, String coachFirstName, String coachLastName, String status, String athleteFirstName, String athleteLastName, String athleteEmail) {
        this.id = id;
        this.coachId = coachId;
        this.coachFirstName = coachFirstName;
        this.coachLastName = coachLastName;
        this.status = status;
        this.athleteFirstName = athleteFirstName;
        this.athleteLastName = athleteLastName;
        this.athleteEmail = athleteEmail;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCoachId() { return coachId; }
    public void setCoachId(Long coachId) { this.coachId = coachId; }

    public String getCoachFirstName() { return coachFirstName; }
    public void setCoachFirstName(String coachFirstName) { this.coachFirstName = coachFirstName; }

    public String getCoachLastName() { return coachLastName; }
    public void setCoachLastName(String coachLastName) { this.coachLastName = coachLastName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAthleteFirstName() { return athleteFirstName; }
    public void setAthleteFirstName(String athleteFirstName) { this.athleteFirstName = athleteFirstName; }

    public String getAthleteLastName() { return athleteLastName; }
    public void setAthleteLastName(String athleteLastName) { this.athleteLastName = athleteLastName; }

    public String getAthleteEmail() { return athleteEmail; }    
    public void setAthleteEmail(String athleteEmail) { this.athleteEmail = athleteEmail; }
}