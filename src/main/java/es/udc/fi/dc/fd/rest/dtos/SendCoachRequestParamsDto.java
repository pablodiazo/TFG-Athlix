package es.udc.fi.dc.fd.rest.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public class SendCoachRequestParamsDto {

    private String athleteEmail;

    public SendCoachRequestParamsDto() {}

    @NotNull
    @Email
    public String getAthleteEmail() {
        return athleteEmail;
    }

    public void setAthleteEmail(String athleteEmail) {
        this.athleteEmail = athleteEmail;
    }
}