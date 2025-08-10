package com.raju.codekatas.refactoring.reportgenerator;

import java.util.List;

public class CSVReportGenerationService implements ReportGenerationService {

    @Override
    public String generateReport(List<String> data, boolean includeTimestamp) {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        
        if (data.isEmpty()) {
            return includeTimestamp ? "\nGenerated at: " + System.currentTimeMillis() : "";
        }

        StringBuilder report = new StringBuilder();
        
        // Avoid trailing comma issue by handling first element separately
        report.append(data.get(0));
        for (int i = 1; i < data.size(); i++) {
            report.append(",").append(data.get(i));
        }

        if (includeTimestamp) {
            report.append("\nGenerated at: ").append(System.currentTimeMillis());
        }

        return report.toString();
    }
}
