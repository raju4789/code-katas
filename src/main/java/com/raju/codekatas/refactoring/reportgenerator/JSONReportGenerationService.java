package com.raju.codekatas.refactoring.reportgenerator;

import java.util.List;

public class JSONReportGenerationService implements ReportGenerationService {

    @Override
    public String generateReport(List<String> data, boolean includeTimestamp) {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        
        if (data.isEmpty()) {
            String baseReport = "[]";
            return includeTimestamp ? baseReport + "\nGenerated at: " + System.currentTimeMillis() : baseReport;
        }

        StringBuilder report = new StringBuilder("[");
        
        // Avoid trailing comma issue by handling first element separately
        report.append("\"").append(data.get(0)).append("\"");
        for (int i = 1; i < data.size(); i++) {
            report.append(",\"").append(data.get(i)).append("\"");
        }
        report.append("]");

        if (includeTimestamp) {
            report.append("\nGenerated at: ").append(System.currentTimeMillis());
        }

        return report.toString();
    }
}
