package com.raju.codekatas.refactoring.reportgenerator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ReportGeneratorL {

    public void generateReport(List<String> data, String type, boolean includeTimestamp) {
        System.out.println("Generating report of type: " + type);
        String report = "";

        if (type.equals("CSV")) {
            for (String item : data) {
                report += item + ",";
            }
            report = report.substring(0, report.length() - 1);
        } else if (type.equals("JSON")) {
            report = "[";
            for (String item : data) {
                report += "\"" + item + "\",";
            }
            report = report.substring(0, report.length() - 1) + "]";
        } else {
            System.out.println("Invalid type: " + type);
            return;
        }

        if (includeTimestamp) {
            report += "\nGenerated at: " + System.currentTimeMillis();
        }

        try {
            FileWriter fw = new FileWriter("report." + type.toLowerCase());
            fw.write(report);
            fw.close();
            System.out.println("Report saved successfully!");
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }
}

