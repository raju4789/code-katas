package com.raju.codekatas.refactoring.reportgenerator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive JUnit test suite for the report generator system.
 * Tests all components with full coverage and edge cases.
 */
@DisplayName("Report Generator Test Suite")
public class ReportGeneratorTestSuite {
    
    @BeforeEach
    void setUp() {
        // Clean up before each test
        cleanup();
    }
    
    @AfterEach
    void tearDown() {
        cleanup();
    }
    
    @Nested
    @DisplayName("ReportGenerator Integration Tests")
    class ReportGeneratorTests {
        
        private ReportGenerator generator;
        
        @BeforeEach
        void setUpGenerator() {
            Map<String, ReportGenerationService> generators = new HashMap<>();
            generators.put("CSV", new CSVReportGenerationService());
            generators.put("JSON", new JSONReportGenerationService());
            
            ReportStorageService reportStorageService = new FileStorageService();
            generator = new ReportGenerator(generators, reportStorageService);
        }
        
        @Test
        @DisplayName("Should generate CSV report without timestamp")
        void testCsvGenerationWithoutTimestamp() throws IOException {
            generator.generateReport(Arrays.asList("A", "B", "C"), "CSV", false);
            
            String content = Files.readString(Paths.get("report.csv"));
            assertEquals("A,B,C", content.trim());
        }
        
        @Test
        @DisplayName("Should generate CSV report with timestamp")
        void testCsvGenerationWithTimestamp() throws IOException {
            generator.generateReport(Arrays.asList("X", "Y"), "CSV", true);
            
            String content = Files.readString(Paths.get("report.csv"));
            assertTrue(content.startsWith("X,Y"));
            assertTrue(content.contains("Generated at:"));
        }
        
        @Test
        @DisplayName("Should generate JSON report without timestamp")
        void testJsonGenerationWithoutTimestamp() throws IOException {
            generator.generateReport(Arrays.asList("apple", "banana"), "JSON", false);
            
            String content = Files.readString(Paths.get("report.json"));
            assertEquals("[\"apple\",\"banana\"]", content.trim());
        }
        
        @Test
        @DisplayName("Should generate JSON report with timestamp")
        void testJsonGenerationWithTimestamp() throws IOException {
            generator.generateReport(Arrays.asList("red", "blue"), "JSON", true);
            
            String content = Files.readString(Paths.get("report.json"));
            assertTrue(content.startsWith("[\"red\",\"blue\"]"));
            assertTrue(content.contains("Generated at:"));
        }
        
        @Test
        @DisplayName("Should generate single item CSV report")
        void testSingleItemCsvReport() throws IOException {
            generator.generateReport(Arrays.asList("SingleItem"), "CSV", false);
            
            String content = Files.readString(Paths.get("report.csv"));
            assertEquals("SingleItem", content.trim());
        }
        
        @Test
        @DisplayName("Should generate single item JSON report")
        void testSingleItemJsonReport() throws IOException {
            generator.generateReport(Arrays.asList("SingleItem"), "JSON", false);
            
            String content = Files.readString(Paths.get("report.json"));
            assertEquals("[\"SingleItem\"]", content.trim());
        }
    }
    
    @Nested
    @DisplayName("CSV Report Generation Service Tests")
    class CSVReportGenerationServiceTests {
        
        private CSVReportGenerationService csvService;
        
        @BeforeEach
        void setUpCSVService() {
            csvService = new CSVReportGenerationService();
        }
        
        @Test
        @DisplayName("Should generate CSV with multiple items")
        void testCSVServiceWithMultipleItems() {
            String result = csvService.generateReport(Arrays.asList("item1", "item2", "item3"), false);
            assertEquals("item1,item2,item3", result);
        }
        
        @Test
        @DisplayName("Should generate CSV with single item")
        void testCSVServiceWithSingleItem() {
            String result = csvService.generateReport(Arrays.asList("onlyItem"), false);
            assertEquals("onlyItem", result);
        }
        
        @Test
        @DisplayName("Should generate empty CSV for empty list")
        void testCSVServiceWithEmptyList() {
            String result = csvService.generateReport(new ArrayList<>(), false);
            assertEquals("", result);
        }
        
        @Test
        @DisplayName("Should generate CSV with timestamp")
        void testCSVServiceWithTimestamp() {
            String result = csvService.generateReport(Arrays.asList("a", "b"), true);
            assertTrue(result.startsWith("a,b"));
            assertTrue(result.contains("Generated at:"));
        }
        
        @Test
        @DisplayName("Should handle special characters in CSV")
        void testCSVServiceWithSpecialCharacters() {
            String result = csvService.generateReport(Arrays.asList("item,with,comma", "item\"with\"quote"), false);
            assertEquals("item,with,comma,item\"with\"quote", result);
        }
        
        @Test
        @DisplayName("Should throw exception for null data")
        void testCSVServiceWithNullData() {
            assertThrows(IllegalArgumentException.class, 
                () -> csvService.generateReport(null, false));
        }
        
        @Test
        @DisplayName("Should handle empty strings in data")
        void testCSVServiceWithEmptyStrings() {
            String result = csvService.generateReport(Arrays.asList("", "notEmpty", ""), false);
            assertEquals(",notEmpty,", result);
        }
    }
    
    @Nested
    @DisplayName("JSON Report Generation Service Tests")
    class JSONReportGenerationServiceTests {
        
        private JSONReportGenerationService jsonService;
        
        @BeforeEach
        void setUpJSONService() {
            jsonService = new JSONReportGenerationService();
        }
        
        @Test
        @DisplayName("Should generate JSON with multiple items")
        void testJSONServiceWithMultipleItems() {
            String result = jsonService.generateReport(Arrays.asList("item1", "item2", "item3"), false);
            assertEquals("[\"item1\",\"item2\",\"item3\"]", result);
        }
        
        @Test
        @DisplayName("Should generate JSON with single item")
        void testJSONServiceWithSingleItem() {
            String result = jsonService.generateReport(Arrays.asList("onlyItem"), false);
            assertEquals("[\"onlyItem\"]", result);
        }
        
        @Test
        @DisplayName("Should generate empty JSON array for empty list")
        void testJSONServiceWithEmptyList() {
            String result = jsonService.generateReport(new ArrayList<>(), false);
            assertEquals("[]", result);
        }
        
        @Test
        @DisplayName("Should generate JSON with timestamp")
        void testJSONServiceWithTimestamp() {
            String result = jsonService.generateReport(Arrays.asList("a", "b"), true);
            assertTrue(result.startsWith("[\"a\",\"b\"]"));
            assertTrue(result.contains("Generated at:"));
        }
        
        @Test
        @DisplayName("Should handle special characters in JSON")
        void testJSONServiceWithSpecialCharacters() {
            String result = jsonService.generateReport(Arrays.asList("item\"with\"quote", "item\\with\\backslash"), false);
            assertEquals("[\"item\"with\"quote\",\"item\\with\\backslash\"]", result);
        }
        
        @Test
        @DisplayName("Should throw exception for null data")
        void testJSONServiceWithNullData() {
            assertThrows(IllegalArgumentException.class, 
                () -> jsonService.generateReport(null, false));
        }
        
        @Test
        @DisplayName("Should handle whitespace-only strings")
        void testJSONServiceWithWhitespaceOnlyStrings() {
            String result = jsonService.generateReport(Arrays.asList("   ", "\t", "\n"), false);
            assertEquals("[\"   \",\"\t\",\"\n\"]", result);
        }
    }
    
    @Nested
    @DisplayName("File Storage Service Tests")
    class FileStorageServiceTests {
        
        private FileStorageService storageService;
        
        @BeforeEach
        void setUpStorageService() {
            storageService = new FileStorageService();
        }
        
        @Test
        @DisplayName("Should save file with basic operation")
        void testFileStorageBasicOperation() throws IOException {
            storageService.save("test content", "TEST");
            String content = Files.readString(Paths.get("report.test"));
            assertEquals("test content", content);
        }
        
        @Test
        @DisplayName("Should overwrite existing file")
        void testFileStorageOverwrite() throws IOException {
            storageService.save("first content", "OVERWRITE");
            storageService.save("second content", "OVERWRITE");
            String content = Files.readString(Paths.get("report.overwrite"));
            assertEquals("second content", content);
        }
        
        @Test
        @DisplayName("Should handle different file types")
        void testFileStorageDifferentTypes() throws IOException {
            storageService.save("csv content", "CSV");
            storageService.save("json content", "JSON");
            
            String csvContent = Files.readString(Paths.get("report.csv"));
            String jsonContent = Files.readString(Paths.get("report.json"));
            
            assertEquals("csv content", csvContent);
            assertEquals("json content", jsonContent);
        }
        
        @Test
        @DisplayName("Should throw exception for null report")
        void testFileStorageWithNullReport() {
            assertThrows(IllegalArgumentException.class, 
                () -> storageService.save(null, "CSV"));
        }
        
        @Test
        @DisplayName("Should throw exception for null type")
        void testFileStorageWithNullType() {
            assertThrows(IllegalArgumentException.class, 
                () -> storageService.save("content", null));
        }
    }
    
    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {
        
        private ReportGenerator generator;
        
        @BeforeEach
        void setUpGenerator() {
            Map<String, ReportGenerationService> generators = new HashMap<>();
            generators.put("CSV", new CSVReportGenerationService());
            generators.put("JSON", new JSONReportGenerationService());
            
            ReportStorageService reportStorageService = new FileStorageService();
            generator = new ReportGenerator(generators, reportStorageService);
        }
        
        @Test
        @DisplayName("Should handle empty data list")
        void testEmptyDataList() throws IOException {
            generator.generateReport(new ArrayList<>(), "CSV", false);
            String content = Files.readString(Paths.get("report.csv"));
            assertEquals("", content.trim());
            
            generator.generateReport(new ArrayList<>(), "JSON", false);
            content = Files.readString(Paths.get("report.json"));
            assertEquals("[]", content.trim());
        }
        
        @Test
        @DisplayName("Should handle very large data list")
        void testVeryLargeDataList() throws IOException {
            List<String> largeList = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                largeList.add("item" + i);
            }
            
            generator.generateReport(largeList, "CSV", false);
            
            String content = Files.readString(Paths.get("report.csv"));
            assertTrue(content.contains("item0"));
            assertTrue(content.contains("item999"));
            
            // Count commas to verify all items are present
            long commaCount = content.chars().filter(ch -> ch == ',').count();
            assertEquals(999L, commaCount);
        }
        
        @Test
        @DisplayName("Should handle data with empty strings")
        void testDataWithEmptyStrings() {
            CSVReportGenerationService csvService = new CSVReportGenerationService();
            String result = csvService.generateReport(Arrays.asList("", "notEmpty", ""), false);
            assertEquals(",notEmpty,", result);
        }
    }
    
    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {
        
        private ReportGenerator generator;
        
        @BeforeEach
        void setUpGenerator() {
            Map<String, ReportGenerationService> generators = new HashMap<>();
            generators.put("CSV", new CSVReportGenerationService());
            generators.put("JSON", new JSONReportGenerationService());
            
            ReportStorageService reportStorageService = new FileStorageService();
            generator = new ReportGenerator(generators, reportStorageService);
        }
        
        @Test
        @DisplayName("Should throw exception for null data input")
        void testNullDataInput() {
            assertThrows(IllegalArgumentException.class, 
                () -> generator.generateReport(null, "CSV", false));
        }
        
        @Test
        @DisplayName("Should throw exception for null type input")
        void testNullTypeInput() {
            assertThrows(IllegalArgumentException.class, 
                () -> generator.generateReport(Arrays.asList("test"), null, false));
        }
        
        @Test
        @DisplayName("Should throw exception for unsupported report type")
        void testUnsupportedReportType() {
            assertThrows(IllegalArgumentException.class, 
                () -> generator.generateReport(Arrays.asList("test"), "XML", false));
        }
        
        @Test
        @DisplayName("Should verify exception message for unsupported type")
        void testUnsupportedReportTypeMessage() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> generator.generateReport(Arrays.asList("test"), "XML", false));
            assertTrue(exception.getMessage().contains("Unsupported report format: XML"));
        }
        
        @Test
        @DisplayName("Should verify exception message for null data")
        void testNullDataInputMessage() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> generator.generateReport(null, "CSV", false));
            assertEquals("Data cannot be null", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should verify exception message for null type")
        void testNullTypeInputMessage() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> generator.generateReport(Arrays.asList("test"), null, false));
            assertEquals("Type cannot be null", exception.getMessage());
        }
    }
    
    private void cleanup() {
        try {
            Files.deleteIfExists(Paths.get("report.csv"));
            Files.deleteIfExists(Paths.get("report.json"));
            Files.deleteIfExists(Paths.get("report.xml"));
            Files.deleteIfExists(Paths.get("report.test"));
            Files.deleteIfExists(Paths.get("report.overwrite"));
        } catch (IOException e) {
            // Ignore cleanup errors in tests
        }
    }
}