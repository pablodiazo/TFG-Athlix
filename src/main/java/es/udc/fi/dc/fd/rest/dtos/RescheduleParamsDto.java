package es.udc.fi.dc.fd.rest.dtos;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;

public class RescheduleParamsDto {

    @NotNull
    private Long sessionId;

    @NotNull
    private LocalDate newDate;

    @NotNull
    private LocalTime newStartTime;

    public RescheduleParamsDto() {}

    public RescheduleParamsDto(Long sessionId, LocalDate newDate, LocalTime newStartTime) {
        this.sessionId = sessionId;
        this.newDate = newDate;
        this.newStartTime = newStartTime;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public LocalDate getNewDate() {
        return newDate;
    }

    public void setNewDate(LocalDate newDate) {
        this.newDate = newDate;
    }

    public LocalTime getNewStartTime() {
        return newStartTime;
    }

    public void setNewStartTime(LocalTime newStartTime) {
        this.newStartTime = newStartTime;
    }
}