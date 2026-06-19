package axismed.repository;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVWriterService<T> {

    private static CSVWriterService instance;

    private CSVWriterService() {}

    public static CSVWriterService getInstance() {
        if (instance == null) {
            instance = new CSVWriterService();
        }
        return instance;
    }

    /**
     * Overwrites the file with a header row followed by all data rows.
     */
    public void writeAll(String filePath, List<String[]> rows, String[] header) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            writer.write(formatRow(header));
            writer.newLine();
            for (String[] row : rows) {
                writer.write(formatRow(row));
                writer.newLine();
            }
        }
    }

    /**
     * Appends a single row to an existing CSV file without touching the header.
     */
    public void appendRow(String filePath, String[] row) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(formatRow(row));
            writer.newLine();
        }
    }

    private String formatRow(String[] fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(escapeField(fields[i] != null ? fields[i] : ""));
        }
        return sb.toString();
    }

    private String escapeField(String field) {
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return '"' + field.replace("\"", "\"\"") + '"';
        }
        return field;
    }
}
