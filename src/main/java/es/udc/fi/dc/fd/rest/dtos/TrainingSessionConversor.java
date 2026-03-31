package es.udc.fi.dc.fd.rest.dtos;

import java.util.List;
import java.util.stream.Collectors;

import es.udc.fi.dc.fd.model.entities.TrainingSession;

public class TrainingSessionConversor {

    private TrainingSessionConversor() {}

    public static TrainingSessionDto toTrainingSessionDto(TrainingSession trainingSession) {
        List<TrainingBlockDto> blocks = trainingSession.getBlocks().stream().map(b -> 
            new TrainingBlockDto(b.getId(), b.getBlockOrder(), b.getName(), b.getSets(), 
                                 b.getReps(), b.getDistanceOrDuration(), b.getPace(), b.getRest())
        ).collect(Collectors.toList());

        return new TrainingSessionDto(trainingSession.getId(), trainingSession.getSessionDate(), 
                                      trainingSession.getStartTime(), trainingSession.getSport(), 
                                      trainingSession.getObjective(), trainingSession.getTotalDistanceOrDuration(), blocks);
    }
    
}
