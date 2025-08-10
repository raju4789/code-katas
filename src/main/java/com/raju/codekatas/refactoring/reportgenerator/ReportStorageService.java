package com.raju.codekatas.refactoring.reportgenerator;

/**
 * Service interface for storing reports to persistent storage.
 */
public interface ReportStorageService {

    /**
     * Saves a report to storage.
     * 
     * @param report The report content to save
     * @param type The report type/format (used for file extension)
     * @throws IllegalArgumentException if report or type is null
     * @throws RuntimeException if storage operation fails
     */
    void save(String report, String type);
}
