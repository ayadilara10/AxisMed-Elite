package axismed.repository;

import axismed.model.medical.Diagnosis;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DiagnosisRepository {

    private static final String FILE_PATH = "data/diagnoses.csv";
    private static final String[] HEADER = {
        "diagnosisId", "patientId", "doctorId", "appointmentId",
        "clinicCity", "name", "description", "icdCode", "date", "isChronicRelated"
    };

    private final HashMap<String, Diagnosis> diagnoses = new HashMap<>();
    private final CSVReaderService reader = CSVReaderService.getInstance();
    private final CSVWriterService writer = CSVWriterService.getInstance();

    public DiagnosisRepository() {
        load();
    }

    private void load() {
        try {
            List<String[]> rows = reader.readAll(FILE_PATH);
            for (String[] row : rows) {
                Diagnosis d = fromCSV(row);
                diagnoses.put(d.getDiagnosisId(), d);
            }
        } catch (IOException e) {
            System.out.println("Warning: Could not load diagnoses.csv — " + e.getMessage());
        }
    }

    private Diagnosis fromCSV(String[] row) {
        return new Diagnosis(
            row[0],
            row[1],
            row[2],
            row[3],
            row[4],
            row[5],
            row[6],
            row[7],
            LocalDate.parse(row[8]),
            Boolean.parseBoolean(row[9])
        );
    }

    private String[] toCSV(Diagnosis d) {
        return new String[]{
            d.getDiagnosisId(),
            d.getPatientId(),
            d.getDoctorId(),
            d.getAppointmentId(),
            d.getClinicCity(),
            d.getDiagnosisName(),
            d.getDescription(),
            d.getIcdCode() != null ? d.getIcdCode() : "",
            d.getDiagnosisDate().toString(),
            String.valueOf(d.isChronicRelated())
        };
    }

    public void save(Diagnosis diagnosis) {
        diagnoses.put(diagnosis.getDiagnosisId(), diagnosis);
        persist();
    }

    public Diagnosis findById(String diagnosisId) {
        return diagnoses.get(diagnosisId);
    }

    public List<Diagnosis> findAll() {
        return new ArrayList<>(diagnoses.values());
    }

    public List<Diagnosis> findByPatientId(String patientId) {
        return diagnoses.values().stream()
            .filter(d -> d.getPatientId().equals(patientId))
            .collect(Collectors.toList());
    }

    public void update(Diagnosis diagnosis) {
        diagnoses.put(diagnosis.getDiagnosisId(), diagnosis);
        persist();
    }

    private void persist() {
        List<String[]> rows = diagnoses.values().stream()
            .map(this::toCSV)
            .collect(Collectors.toList());
        try {
            writer.writeAll(FILE_PATH, rows, HEADER);
        } catch (IOException e) {
            System.out.println("Warning: Could not save diagnoses.csv — " + e.getMessage());
        }
    }
}
