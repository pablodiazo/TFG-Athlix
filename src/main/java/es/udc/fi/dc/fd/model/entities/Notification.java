package es.udc.fi.dc.fd.model.entities;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private Users user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "athleteId")
    private Users athlete;
    
    private String message;

    private String type;
    
    private LocalDate planDate;
    
    private boolean isRead;

    public Notification() {}

    public Notification(Users user, Users athlete, String message, String type, LocalDate planDate) {
        this.user = user;
        this.athlete = athlete;
        this.message = message;
        this.type = type;
        this.planDate = planDate;
        this.isRead = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Users getUser() { return user; }
    public void setUser(Users user) { this.user = user; }

    public Users getAthlete() { return athlete; }
    public void setAthlete(Users athlete) { this.athlete = athlete; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDate getPlanDate() { return planDate; }
    public void setPlanDate(LocalDate planDate) { this.planDate = planDate; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}