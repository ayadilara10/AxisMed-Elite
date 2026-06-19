package axismed.repository;

import axismed.model.medical.MedicalRecord;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MedicalRecordRepository {

    private static final String FILE_PATH = "data/medical_records.csv";
    private static final String[] HEADER = {
        "recordId", "patientId", "createdDate", "lastUpdatedBy", "generalNotes"
    };

    private final HashMap<String, MedicalRecord> records = new HashMap<>();
    private final CSVReaderService reader = CSVReaderService.getInstance();
    private final CSVWriterService writer = CSVWriterService.getInstance();

    public MedicalRecordRepository() {
        load();
    }

    private void load() {
        try {
            List<String[]> rows = reader.readAll(FILE_PATH);
            for (String[] row : rows) {
                MedicalRecord r = fromCSV(row);
                records.put(r.getRecordId(), r);
            }
        } catch (IOException e) {
            System.out.println("Warning: Could not load medical_records.csv — " + e.getMessage());
        }
    }

    private MedicalRecord fromCSV(String[] row) {
        MedicalRecord record = new MedicalRecord(
            row[0],
            row[1],
            LocalDate.parse(row[2])
        );
        record.setLastUpdatedByClinic(row[3]);

        if (row[4] != null && !row[4].isEmpty()) {
            for (String note : row[4].split(";")) {
                record.addNote(note.trim());
            }
        }

        return record;
    }

    private String[] toCSV(MedicalRecord r) {
        String notes = String.join(";", r.getGeneralNotes());
        return new String[]{
            r.getRecordId(),
            r.getPatientId(),
            r.getCreatedDate().toString(),
            r.getLastUpdatedByClinic() != null ? r.getLastUpdatedByClinic() : "",
            notes
        };
    }

    public void save(MedicalRecord record) {
        records.put(record.getRecordId(), record);
        persist();
    }

    public MedicalRecord findById(String recordId) {
        return records.get(recordId);
    }

    public MedicalRecord findByPatientId(String patientId) {
        return records.values().stream()
            .filter(r -> r.getPatientId().equals(patientId))
            .findFirst().orElse(null);
    }

    public List<MedicalRecord> findAll() {
        return new ArrayList<>(records.values());
    }

    public void update(MedicalRecord record) {
        records.put(record.getRecordId(), record);
        persist();
    }

    private void persist() {
        List<String[]> rows = records.values().stream()
            .map(this::toCSV)
            .collect(Collectors.toList());
        try {
            writer.writeAll(FILE_PATH, rows, HEADER);
        } catch (IOException e) {
            System.out.println("Warning: Could not save medical_records.csv — " + e.getMessage());
        }
    }
}
