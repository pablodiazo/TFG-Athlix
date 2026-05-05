package es.udc.fi.dc.fd.rest.dtos;

import es.udc.fi.dc.fd.model.entities.TrainingBlock;

public class TrainingBlockConversor {

    private TrainingBlockConversor() {}

    public static TrainingBlockDto toTrainingBlockDto(TrainingBlock block) {
        return new TrainingBlockDto(block.getId(), block.getBlockOrder(), block.getName(), block.getSets(), 
                                    block.getReps(), block.getDistanceOrDuration(), block.getPace(), block.getRest(), block.getDone());
    }
    
}
