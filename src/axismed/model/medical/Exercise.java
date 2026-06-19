package axismed.model.medical;

public class Exercise {

    private String name;
    private String description;
    private int sets;
    private int reps;
    private String duration;
    private String notes;

    public Exercise(String name, String description, int sets, int reps, String duration, String notes) {
        this.name = name;
        this.description = description;
        this.sets = sets;
        this.reps = reps;
        this.duration = duration;
        this.notes = notes;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getSets() { return sets; }
    public void setSets(int sets) { this.sets = sets; }

    public int getReps() { return reps; }
    public void setReps(int reps) { this.reps = reps; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return "Exercise{name='" + name + "', sets=" + sets + ", reps=" + reps +
               ", duration='" + duration + "', description='" + description + "'}";
    }
}
