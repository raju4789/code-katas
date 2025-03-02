package com.raju.codekatas.gossip.factory;

import com.raju.codekatas.gossip.model.Driver;

import java.util.ArrayList;
import java.util.List;

public class DriverFactory {

    public static List<Driver> createDrivers(List<List<Integer>> routes) {
        List<Driver> drivers = new ArrayList<>();
        for (int i = 0; i < routes.size(); i++) {
            drivers.add(new Driver(i, routes.get(i)));
        }
        return drivers;
    }
}
