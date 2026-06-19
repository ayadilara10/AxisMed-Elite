package axismed.model.medical;

import java.util.ArrayList;
import java.util.List;

public class RehabilitationProtocol {

    private String protocolId;
    private String planId;
    private String description;
    private int durationWeeks;
    private int sessionsPerWeek;
    private List<Exercise> exercises;

    public RehabilitationProtocol(String protocolId, String planId, String description,
                                  int durationWeeks, int sessionsPerWeek) {
        this.protocolId = protocolId;
        this.planId = planId;
        this.description = description;
        this.durationWeeks = durationWeeks;
        this.sessionsPerWeek = sessionsPerWeek;
        this.exercises = new ArrayList<>();
    }

    public String getProtocolId() { return protocolId; }
    public void setProtocolId(String protocolId) { this.protocolId = protocolId; }

    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getDurationWeeks() { return durationWeeks; }
    public void setDurationWeeks(int durationWeeks) { this.durationWeeks = durationWeeks; }

    public int getSessionsPerWeek() { return sessionsPerWeek; }
    public void setSessionsPerWeek(int sessionsPerWeek) { this.sessionsPerWeek = sessionsPerWeek; }

    public List<Exercise> getExercises() { return exercises; }
    public void setExercises(List<Exercise> exercises) { this.exercises = exercises; }

    public void addExercise(Exercise exercise) {
        exercises.add(exercise);
    }

    public void removeExercise(String exerciseName) {
        exercises.removeIf(e -> e.getName().equals(exerciseName));
    }

    @Override
    public String toString() {
        return "RehabilitationProtocol{id='" + protocolId + "', planId='" + planId +
               "', durationWeeks=" + durationWeeks + ", sessionsPerWeek=" + sessionsPerWeek +
               ", exercises=" + exercises.size() + "}";
    }
}
