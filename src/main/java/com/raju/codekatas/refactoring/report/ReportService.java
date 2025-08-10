package com.raju.codekatas.refactoring.report;

import java.util.ArrayList;
import java.util.List;

public class ReportService {
    private List<Order> orders = new ArrayList<>();

    public static void main(String[] args) {
        ReportService service = new ReportService();
        service.orders.add(new Order("VIP123", 100.0, "Shipped"));
        service.orders.add(new Order("REG456", 50.0, "Processing"));

        System.out.println(service.generateReport("TEXT"));
        System.out.println(service.generateReport("JSON"));
    }

    public String generateReport(String format) {
        StringBuilder sb = new StringBuilder();

        if (format.equals("TEXT")) {
            sb.append("Customer Orders Report\n");
            sb.append("======================\n");
            for (Order order : orders) {
                sb.append("ID: ").append(order.getId())
                        .append(", Amount: ").append(order.getAmount())
                        .append(", Status: ").append(order.getStatus())
                        .append("\n");
            }
        } else if (format.equals("JSON")) {
            sb.append("{ \"orders\": [");
            for (int i = 0; i < orders.size(); i++) {
                Order order = orders.get(i);
                sb.append("{")
                        .append("\"id\": \"").append(order.getId()).append("\", ")
                        .append("\"amount\": ").append(order.getAmount()).append(", ")
                        .append("\"status\": \"").append(order.getStatus()).append("\"")
                        .append("}");
                if (i < orders.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append("] }");
        } else {
            return "Unsupported format";
        }

        return sb.toString();
    }
}

class Order {
    private String id;
    private double amount;
    private String status;

    public Order(String id, double amount, String status) {
        this.id = id;
        this.amount = amount;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }
}

