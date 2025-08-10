package com.raju.codekatas.refactoring.reportgenerator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for ReportGenerator covering basic functionality.
 */
public class ReportGeneratorTest {
    
    private ReportGenerator generator;
    
    @BeforeEach
    void setUp() {
        Map<String, ReportGenerationService> generators = new HashMap<>();
        generators.put("CSV", new CSVReportGenerationService());
        generators.put("JSON", new JSONReportGenerationService());
        
        ReportStorageService reportStorageService = new FileStorageService();
        generator = new ReportGenerator(generators, reportStorageService);
    }
    
    @Test
    void testGenerateCsvReportWithoutTimestamp() throws IOException {
        generator.generateReport(Arrays.asList("A", "B", "C"), "CSV", false);

        String content = Files.readString(Paths.get("report.csv"));
        assertEquals("A,B,C", content.trim());
    }

    @Test
    void testGenerateCsvReportWithTimestamp() throws IOException {
        generator.generateReport(Arrays.asList("X", "Y"), "CSV", true);

        String content = Files.readString(Paths.get("report.csv"));
        assertTrue(content.startsWith("X,Y"));
        assertTrue(content.contains("Generated at:"));
    }

    @Test
    void testGenerateJsonReportWithoutTimestamp() throws IOException {
        generator.generateReport(Arrays.asList("apple", "banana"), "JSON", false);

        String content = Files.readString(Paths.get("report.json"));
        assertEquals("[\"apple\",\"banana\"]", content.trim());
    }

    @Test
    void testGenerateJsonReportWithTimestamp() throws IOException {
        generator.generateReport(Arrays.asList("red", "blue"), "JSON", true);

        String content = Files.readString(Paths.get("report.json"));
        assertTrue(content.startsWith("[\"red\",\"blue\"]"));
        assertTrue(content.contains("Generated at:"));
    }

    @Test
    void testInvalidTypeThrowsException() {
        assertThrows(IllegalArgumentException.class, 
            () -> generator.generateReport(Arrays.asList("a", "b"), "XML", false));
    }

    @Test
    void testNullDataThrowsException() {
        assertThrows(IllegalArgumentException.class, 
            () -> generator.generateReport(null, "CSV", false));
    }

    @Test
    void testNullTypeThrowsException() {
        assertThrows(IllegalArgumentException.class, 
            () -> generator.generateReport(Arrays.asList("test"), null, false));
    }

    @Test
    void testSingleItemCsvReport() throws IOException {
        generator.generateReport(Arrays.asList("SingleItem"), "CSV", false);

        String content = Files.readString(Paths.get("report.csv"));
        assertEquals("SingleItem", content.trim());
    }

    @Test
    void testSingleItemJsonReport() throws IOException {
        generator.generateReport(Arrays.asList("SingleItem"), "JSON", false);

        String content = Files.readString(Paths.get("report.json"));
        assertEquals("[\"SingleItem\"]", content.trim());
    }

    @Test
    void testEmptyDataCsvReport() throws IOException {
        generator.generateReport(Arrays.asList(), "CSV", false);

        String content = Files.readString(Paths.get("report.csv"));
        assertEquals("", content.trim());
    }

    @Test
    void testEmptyDataJsonReport() throws IOException {
        generator.generateReport(Arrays.asList(), "JSON", false);

        String content = Files.readString(Paths.get("report.json"));
        assertEquals("[]", content.trim());
    }
    
    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get("report.csv"));
        Files.deleteIfExists(Paths.get("report.json"));
        Files.deleteIfExists(Paths.get("report.xml"));
        Files.deleteIfExists(Paths.get("report.test"));
        Files.deleteIfExists(Paths.get("report.overwrite"));
    }
}

