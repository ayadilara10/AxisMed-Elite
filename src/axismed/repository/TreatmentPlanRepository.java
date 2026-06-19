package axismed.repository;

import axismed.enums.TreatmentStatus;
import axismed.model.medical.TreatmentPlan;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class TreatmentPlanRepository {

    private static final String FILE_PATH = "data/treatment_plans.csv";
    private static final String[] HEADER = {
        "planId", "patientId", "staffId", "diagnosisId",
        "startDate", "endDate", "status", "recommendations"
    };

    private final HashMap<String, TreatmentPlan> plans = new HashMap<>();
    private final CSVReaderService reader = CSVReaderService.getInstance();
    private final CSVWriterService writer = CSVWriterService.getInstance();

    public TreatmentPlanRepository() {
        load();
    }

    private void load() {
        try {
            List<String[]> rows = reader.readAll(FILE_PATH);
            for (String[] row : rows) {
                TreatmentPlan p = fromCSV(row);
                plans.put(p.getPlanId(), p);
            }
        } catch (IOException e) {
            System.out.println("Warning: Could not load treatment_plans.csv — " + e.getMessage());
        }
    }

    private TreatmentPlan fromCSV(String[] row) {
        String diagnosisId = (row[3] == null || row[3].isEmpty()) ? null : row[3];
        LocalDate endDate  = (row[5] == null || row[5].isEmpty()) ? null : LocalDate.parse(row[5]);

        return new TreatmentPlan(
            row[0],
            row[1],
            row[2],
            diagnosisId,
            LocalDate.parse(row[4]),
            endDate,
            TreatmentStatus.valueOf(row[6]),
            row[7]
        );
    }

    private String[] toCSV(TreatmentPlan p) {
        return new String[]{
            p.getPlanId(),
            p.getPatientId(),
            p.getCreatedByStaffId(),
            p.getDiagnosisId() != null ? p.getDiagnosisId() : "",
            p.getStartDate().toString(),
            p.getEndDate()   != null ? p.getEndDate().toString() : "",
            p.getStatus().name(),
            p.getFreeTextRecommendations() != null ? p.getFreeTextRecommendations() : ""
        };
    }

    public void save(TreatmentPlan plan) {
        plans.put(plan.getPlanId(), plan);
        persist();
    }

    public TreatmentPlan findById(String planId) {
        return plans.get(planId);
    }

    public List<TreatmentPlan> findAll() {
        return new ArrayList<>(plans.values());
    }

    public List<TreatmentPlan> findByPatientId(String patientId) {
        return plans.values().stream()
            .filter(p -> p.getPatientId().equals(patientId))
            .collect(Collectors.toList());
    }

    public void update(TreatmentPlan plan) {
        plans.put(plan.getPlanId(), plan);
        persist();
    }

    private void persist() {
        List<String[]> rows = plans.values().stream()
            .map(this::toCSV)
            .collect(Collectors.toList());
        try {
            writer.writeAll(FILE_PATH, rows, HEADER);
        } catch (IOException e) {
            System.out.println("Warning: Could not save treatment_plans.csv — " + e.getMessage());
        }
    }
}
