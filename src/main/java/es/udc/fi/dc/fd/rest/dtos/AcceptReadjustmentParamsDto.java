package es.udc.fi.dc.fd.rest.dtos;

import java.time.LocalDate;
import java.time.LocalTime;

public class AcceptReadjustmentParamsDto {
    
    private Long athleteId;
    private Long sessionId;
    private LocalDate newDate;
    private LocalTime newStartTime;
    private Boolean reschedule;

    public AcceptReadjustmentParamsDto() {}

    public AcceptReadjustmentParamsDto(Long athleteId, Long sessionId, LocalDate newDate, LocalTime newStartTime, Boolean reschedule) {
        this.athleteId = athleteId;
        this.sessionId = sessionId;
        this.newDate = newDate;
        this.newStartTime = newStartTime;
        this.reschedule = reschedule;
    }

    public Long getAthleteId() { return athleteId; }
    public void setAthleteId(Long athleteId) { this.athleteId = athleteId; }

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public LocalDate getNewDate() { return newDate; }
    public void setNewDate(LocalDate newDate) { this.newDate = newDate; }

    public LocalTime getNewStartTime() { return newStartTime; }
    public void setNewStartTime(LocalTime newStartTime) { this.newStartTime = newStartTime; }

    public Boolean getReschedule() { return reschedule; }
    public void setReschedule(Boolean reschedule) { this.reschedule = reschedule; }
}
