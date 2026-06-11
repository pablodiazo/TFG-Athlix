package es.udc.fi.dc.fd.rest.dtos;

public class NotificationDto {
    private Long id;
    private Long athleteId;
    private String message;
    private String type;
    private String planDate;
    private boolean isRead;

    public NotificationDto(Long id, Long athleteId, String message, String type, String planDate, boolean isRead) {
        this.id = id;
        this.athleteId = athleteId;
        this.message = message;
        this.type = type;
        this.planDate = planDate;
        this.isRead = isRead;
    }

    public Long getId() { return id; }
    public Long getAthleteId() { return athleteId; }
    public String getMessage() { return message; }
    public String getType() { return type; }
    public String getPlanDate() { return planDate; }
    public boolean getIsRead() { return isRead; }
}