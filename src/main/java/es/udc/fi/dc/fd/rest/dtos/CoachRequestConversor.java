package es.udc.fi.dc.fd.rest.dtos;

import java.util.List;
import java.util.stream.Collectors;

import es.udc.fi.dc.fd.model.entities.CoachRequest;

public class CoachRequestConversor {

    private CoachRequestConversor() {}

    public static CoachRequestDto toCoachRequestDto(CoachRequest request) {
        return new CoachRequestDto(
            request.getId(),
            request.getCoach().getId(),
            request.getCoach().getFirstName(),
            request.getCoach().getLastName(),
            request.getStatus().toString(),
            request.getAthlete().getFirstName(),
            request.getAthlete().getLastName(),
            request.getAthlete().getEmail()
        );
    }

    public static List<CoachRequestDto> toCoachRequestDtos(List<CoachRequest> requests) {
        return requests.stream()
            .map(CoachRequestConversor::toCoachRequestDto)
            .collect(Collectors.toList());
    }
}