package es.udc.fi.dc.fd.rest.dtos;

public class NotificationDto {
    private Long id;
    private Long athleteId;
    private Long sessionId;
    private String message;
    private String type;
    private String planDate;
    private boolean isRead;
    private boolean isReviewed;
    private String newDate;
    private String newStartTime;

    public NotificationDto(Long id, Long athleteId, Long sessionId, String message, String type, String planDate, boolean isRead, boolean isReviewed) {
        this.id = id;
        this.athleteId = athleteId;
        this.sessionId = sessionId;
        this.message = message;
        this.type = type;
        this.planDate = planDate;
        this.isRead = isRead;
        this.isReviewed = isReviewed;
    }

    public NotificationDto(Long id, Long athleteId, Long sessionId, String message, String type, String planDate, boolean isRead, boolean isReviewed, String newDate, String newStartTime) {
        this.id = id;
        this.athleteId = athleteId;
        this.sessionId = sessionId;
        this.message = message;
        this.type = type;
        this.planDate = planDate;
        this.isRead = isRead;
        this.isReviewed = isReviewed;
        this.newDate = newDate;
        this.newStartTime = newStartTime;
    }

    public Long getId() { return id; }
    public Long getAthleteId() { return athleteId; }
    public Long getSessionId() { return sessionId; }
    public String getMessage() { return message; }
    public String getType() { return type; }
    public String getPlanDate() { return planDate; }
    public boolean getIsRead() { return isRead; }
    public boolean getIsReviewed() { return isReviewed; }
    public String getNewDate() { return newDate; }
    public String getNewStartTime() { return newStartTime; }
}