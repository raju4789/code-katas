package com.raju.codekatas.refactoring.reportgenerator;

import java.util.List;

/**
 * Service interface for generating reports in different formats.
 */
public interface ReportGenerationService {

    /**
     * Generates a report from the provided data.
     * 
     * @param data List of strings to include in the report
     * @param includeTimestamp Whether to include timestamp in the report
     * @return Generated report as a string
     * @throws IllegalArgumentException if data is null
     */
    String generateReport(List<String> data, boolean includeTimestamp);
}
