package com.raju.codekatas.refactoring.order;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class OrderProcessor {

    private final Map<String, OrderHandler> handlers;

    public OrderProcessor(Map<String, OrderHandler> handlers) {
        this.handlers = handlers;
    }

    public static void main(String[] args) {
        Map<String, OrderHandler> handlers = Map.of(
                "ONLINE", new OnlineOrderHandler(),
                "OFFLINE", new OfflineOrderHandler()
        );
        OrderProcessor op = new OrderProcessor(handlers);
        List<String> orders = Arrays.asList("VIP123", "REG456");
        op.processOrders(orders, "ONLINE");
        op.processOrders(orders, "OFFLINE");
    }


    public void processOrders(List<String> orders, String type) {

        OrderHandler handler = handlers.get(type);

        if (handler == null) {
            System.out.println("Unknown order type: " + type);
            return;
        }

        for (String order : orders) {
            handler.process(order);
        }
    }
}

