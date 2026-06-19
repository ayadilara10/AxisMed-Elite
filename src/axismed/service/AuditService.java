package axismed.service;

import axismed.repository.CSVWriterService;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditService {

    private static final String AUDIT_FILE = "data/audit_log.csv";
    private static final String[] HEADER = { "actionName", "timestamp", "userEmail" };
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private static AuditService instance;

    private final CSVWriterService writer = CSVWriterService.getInstance();

    private AuditService() {
        ensureFileExists();
    }

    public static AuditService getInstance() {
        if (instance == null) {
            instance = new AuditService();
        }
        return instance;
    }

    public void log(String actionName, String userEmail) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String[] row = { actionName, timestamp, userEmail };
        try {
            writer.appendRow(AUDIT_FILE, row);
        } catch (IOException e) {
            System.out.println("Warning: Could not write to audit_log.csv — " + e.getMessage());
        }
    }

    private void ensureFileExists() {
        File file = new File(AUDIT_FILE);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                writer.writeAll(AUDIT_FILE, java.util.Collections.emptyList(), HEADER);
            } catch (IOException e) {
                System.out.println("Warning: Could not create audit_log.csv — " + e.getMessage());
            }
        }
    }
}
