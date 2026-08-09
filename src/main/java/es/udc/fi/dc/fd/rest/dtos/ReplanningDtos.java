package es.udc.fi.dc.fd.rest.dtos;

import java.util.List;

public class ReplanningDtos {
    
    public static class ReplanApiRequest {
        private ContextApiRequest context;
        private SessionApiRequest failedSession;
        private List<SessionApiRequest> adjustableSessions;

        public ReplanApiRequest() {}
        public ReplanApiRequest(ContextApiRequest context, SessionApiRequest failedSession, List<SessionApiRequest> adjustableSessions) {
            this.context = context;
            this.failedSession = failedSession;
            this.adjustableSessions = adjustableSessions;
        }

        public ContextApiRequest getContext() { return context; }
        public void setContext(ContextApiRequest context) { this.context = context; }
        public SessionApiRequest getFailedSession() { return failedSession; }
        public void setFailedSession(SessionApiRequest failedSession) { this.failedSession = failedSession; }
        public List<SessionApiRequest> getAdjustableSessions() { return adjustableSessions; }
        public void setAdjustableSessions(List<SessionApiRequest> adjustableSessions) { this.adjustableSessions = adjustableSessions; }
    }

    public static class ContextApiRequest {
        private double athleteWeeklyTargetTss;
        private double missingTssToCompensate;

        public ContextApiRequest() {}
        public ContextApiRequest(double athleteWeeklyTargetTss, double missingTssToCompensate) {
            this.athleteWeeklyTargetTss = athleteWeeklyTargetTss;
            this.missingTssToCompensate = missingTssToCompensate;
        }

        public double getAthleteWeeklyTargetTss() { return athleteWeeklyTargetTss; }
        public void setAthleteWeeklyTargetTss(double athleteWeeklyTargetTss) { this.athleteWeeklyTargetTss = athleteWeeklyTargetTss; }
        public double getMissingTssToCompensate() { return missingTssToCompensate; }
        public void setMissingTssToCompensate(double missingTssToCompensate) { this.missingTssToCompensate = missingTssToCompensate; }
    }

    public static class SessionApiRequest {
        private String date;
        private String sport;
        private double tss;
        private List<BlockApiRequest> blocks;

        public SessionApiRequest() {}
        public SessionApiRequest(String date, String sport, double tss, List<BlockApiRequest> blocks) {
            this.date = date;
            this.sport = sport;
            this.tss = tss;
            this.blocks = blocks;
        }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public String getSport() { return sport; }
        public void setSport(String sport) { this.sport = sport; }
        public double getTss() { return tss; }
        public void setTss(double tss) { this.tss = tss; }
        public List<BlockApiRequest> getBlocks() { return blocks; }
        public void setBlocks(List<BlockApiRequest> blocks) { this.blocks = blocks; }
    }

    public static class BlockApiRequest {
        private String name;
        private String distanceOrDuration;
        private String pace;
        private Integer sets;
        private Integer reps;
        private String rest;

        public BlockApiRequest() {}
        public BlockApiRequest(String name, String distanceOrDuration, String pace, Integer sets, Integer reps, String rest) {
            this.name = name;
            this.distanceOrDuration = distanceOrDuration;
            this.pace = pace;
            this.sets = sets;
            this.reps = reps;
            this.rest = rest;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDistanceOrDuration() { return distanceOrDuration; }
        public void setDistanceOrDuration(String distanceOrDuration) { this.distanceOrDuration = distanceOrDuration; }
        public String getPace() { return pace; }
        public void setPace(String pace) { this.pace = pace; }
        public Integer getSets() { return sets; }
        public void setSets(Integer sets) { this.sets = sets; }
        public Integer getReps() { return reps; }
        public void setReps(Integer reps) { this.reps = reps; }
        public String getRest() { return rest; }
        public void setRest(String rest) { this.rest = rest; }
    }

    public static class PlanReadjustmentApiResponse {
        private String readjustmentReasoning;
        private List<UpdatedSessionApiResponse> updatedSessions;
        private RescheduledSessionApiResponse rescheduledSession;

        public PlanReadjustmentApiResponse() {}

        public String getReadjustmentReasoning() { return readjustmentReasoning; }
        public void setReadjustmentReasoning(String readjustmentReasoning) { this.readjustmentReasoning = readjustmentReasoning; }
        public List<UpdatedSessionApiResponse> getUpdatedSessions() { return updatedSessions; }
        public void setUpdatedSessions(List<UpdatedSessionApiResponse> updatedSessions) { this.updatedSessions = updatedSessions; }
        public RescheduledSessionApiResponse getRescheduledSession() { return rescheduledSession; }
        public void setRescheduledSession(RescheduledSessionApiResponse rescheduledSession) { this.rescheduledSession = rescheduledSession; }
    }

    public static class UpdatedSessionApiResponse {
        private String date;
        private String sport;
        private double newTss;
        private List<UpdatedBlockApiResponse> updatedBlocks;

        public UpdatedSessionApiResponse() {}

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public String getSport() { return sport; }
        public void setSport(String sport) { this.sport = sport; }
        public double getNewTss() { return newTss; }
        public void setNewTss(double newTss) { this.newTss = newTss; }
        public List<UpdatedBlockApiResponse> getUpdatedBlocks() { return updatedBlocks; }
        public void setUpdatedBlocks(List<UpdatedBlockApiResponse> updatedBlocks) { this.updatedBlocks = updatedBlocks; }
    }

    public static class UpdatedBlockApiResponse {
        private String name;
        private String distanceOrDuration;
        private String pace;
        private Integer sets;
        private Integer reps;
        private String rest;

        public UpdatedBlockApiResponse() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDistanceOrDuration() { return distanceOrDuration; }
        public void setDistanceOrDuration(String distanceOrDuration) { this.distanceOrDuration = distanceOrDuration; }
        public String getPace() { return pace; }
        public void setPace(String pace) { this.pace = pace; }
        public Integer getSets() { return sets; }
        public void setSets(Integer sets) { this.sets = sets; }
        public Integer getReps() { return reps; }
        public void setReps(Integer reps) { this.reps = reps; }
        public String getRest() { return rest; }
        public void setRest(String rest) { this.rest = rest; }
    }

    public static class RescheduledSessionApiResponse {
        private String newDate;
        private String sport;
        private double tss;
        private List<UpdatedBlockApiResponse> blocks;

        public RescheduledSessionApiResponse() {}

        public String getNewDate() { return newDate; }
        public void setNewDate(String newDate) { this.newDate = newDate; }
        public String getSport() { return sport; }
        public void setSport(String sport) { this.sport = sport; }
        public double getTss() { return tss; }
        public void setTss(double tss) { this.tss = tss; }
        public List<UpdatedBlockApiResponse> getBlocks() { return blocks; }
        public void setBlocks(List<UpdatedBlockApiResponse> blocks) { this.blocks = blocks; }
    }
}