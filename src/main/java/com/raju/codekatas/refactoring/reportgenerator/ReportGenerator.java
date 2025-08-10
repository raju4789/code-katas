package com.raju.codekatas.refactoring.reportgenerator;

import java.util.List;
import java.util.Map;

public class ReportGenerator {
    private final Map<String, ReportGenerationService> generators;
    private final ReportStorageService reportStorageService;

    public ReportGenerator(Map<String, ReportGenerationService> generators, ReportStorageService reportStorageService) {
        this.generators = generators;
        this.reportStorageService = reportStorageService;
    }


    /**
     * Generates and saves a report in the specified format.
     * 
     * @param data List of strings to include in the report
     * @param type Report format type (must be supported)
     * @param includeTimestamp Whether to include timestamp in the report
     * @throws IllegalArgumentException if data/type is null or type is unsupported
     */
    public void generateReport(List<String> data, String type, boolean includeTimestamp) {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }

        ReportGenerationService reportGenerationService = generators.get(type);

        if (reportGenerationService == null) {
            throw new IllegalArgumentException("Unsupported report format: " + type);
        }

        String report = reportGenerationService.generateReport(data, includeTimestamp);

        reportStorageService.save(report, type);
    }
}
