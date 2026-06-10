package es.udc.fi.dc.fd.rest.dtos;

public class NotificationDto {
    private Long id;
    private Long athleteId;
    private String message;
    private String planDate;
    private boolean isRead;

    public NotificationDto(Long id, Long athleteId, String message, String planDate, boolean isRead) {
        this.id = id;
        this.athleteId = athleteId;
        this.message = message;
        this.planDate = planDate;
        this.isRead = isRead;
    }

    public Long getId() { return id; }
    public Long getUserId() { return athleteId; }
    public String getMessage() { return message; }
    public String getPlanDate() { return planDate; }
    public boolean getIsRead() { return isRead; }
}