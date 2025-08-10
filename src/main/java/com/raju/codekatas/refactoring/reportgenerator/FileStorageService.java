package com.raju.codekatas.refactoring.reportgenerator;

import java.io.FileWriter;
import java.io.IOException;

public class FileStorageService implements ReportStorageService {

    @Override
    public void save(String report, String type) {
        if (report == null || type == null) {
            throw new IllegalArgumentException("Report and type cannot be null");
        }
        
        try (FileWriter fw = new FileWriter("report." + type.toLowerCase())) {
            fw.write(report);
            // Removed hardcoded console output for better separation of concerns
        } catch (IOException e) {
            throw new RuntimeException("Failed to save report: " + e.getMessage(), e);
        }
    }
}
