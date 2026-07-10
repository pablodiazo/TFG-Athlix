package es.udc.fi.dc.fd.rest.dtos;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import jakarta.validation.constraints.NotNull;
import es.udc.fi.dc.fd.model.entities.TrainingSession;

public class UpdateSessionParamsDto {

    @NotNull
    private LocalDate sessionDate;
    
    @NotNull
    private LocalTime startTime;
    
    @NotNull
    private TrainingSession.SportType sport;
    
    private String objective;
    private String totalDistanceOrDuration;
    private List<TrainingBlockDto> blocks;

    public UpdateSessionParamsDto() {}

    public LocalDate getSessionDate() { return sessionDate; }
    public void setSessionDate(LocalDate sessionDate) { this.sessionDate = sessionDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public TrainingSession.SportType getSport() { return sport; }
    public void setSport(TrainingSession.SportType sport) { this.sport = sport; }

    public String getObjective() { return objective; }
    public void setObjective(String objective) { this.objective = objective; }

    public String getTotalDistanceOrDuration() { return totalDistanceOrDuration; }
    public void setTotalDistanceOrDuration(String totalDistanceOrDuration) { this.totalDistanceOrDuration = totalDistanceOrDuration; }

    public List<TrainingBlockDto> getBlocks() { return blocks; }
    public void setBlocks(List<TrainingBlockDto> blocks) { this.blocks = blocks; }
}