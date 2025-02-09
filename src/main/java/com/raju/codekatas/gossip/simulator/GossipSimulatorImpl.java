package com.raju.codekatas.gossip.simulator;

import com.raju.codekatas.gossip.factory.DriverFactory;
import com.raju.codekatas.gossip.model.Driver;
import com.raju.codekatas.gossip.strategy.GossipExchangeStrategy;
import com.raju.codekatas.gossip.utils.ApplicationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GossipSimulatorImpl implements GossipSimulator {

    private static final Logger logger = LoggerFactory.getLogger(GossipSimulatorImpl.class);


    private final int maxMinutes;


    private final GossipExchangeStrategy gossipExchangeStrategy;

    public GossipSimulatorImpl(GossipExchangeStrategy gossipExchangeStrategy, int maxMinutes) {
        this.maxMinutes = maxMinutes;
        this.gossipExchangeStrategy = gossipExchangeStrategy;
    }


    @Override
    public String simulate(List<List<Integer>> routes) {
        if (routes == null || routes.isEmpty()) {
            logger.warn("No routes provided");
            return ApplicationConstants.NEVER;
        }

        if (routes.size() == 1) {
            logger.info("Only one route/driver, no gossip exchange needed.");
            return ApplicationConstants.ONE;
        }

        List<Driver> drivers = DriverFactory.createDrivers(routes);

        for (int minute = 0; minute < maxMinutes; minute++) {
            Map<Integer, List<Driver>> stopToDrivers = groupDriversByStop(drivers, minute);

            // Exchange gossips at each stop
            for (List<Driver> driversAtStop : stopToDrivers.values()) {
                gossipExchangeStrategy.exchangeGossips(driversAtStop);
            }

            // Check if all drivers know all gossips
            if (allDriversKnowAllGossips(drivers)) {
                logger.info("All drivers know all gossips after {} minutes", minute + 1);
                return String.valueOf(minute + 1);
            }
        }

        logger.info("Gossips not exchanged after {} minutes", maxMinutes);
        return ApplicationConstants.NEVER;
    }

    private Map<Integer, List<Driver>> groupDriversByStop(List<Driver> drivers, int minute) {
        Map<Integer, List<Driver>> stopToDrivers = new HashMap<>();
        for (Driver driver : drivers) {
            int currentStop = driver.getCurrentStop(minute);
            // Check if the current stop already exists in the map
            if (!stopToDrivers.containsKey(currentStop)) {
                // If not, create a new list for this stop
                stopToDrivers.put(currentStop, new ArrayList<>());
            }
            // Add the driver to the list of drivers at this stop
            stopToDrivers.get(currentStop).add(driver);
        }
        return stopToDrivers;
    }

    private boolean allDriversKnowAllGossips(List<Driver> drivers) {
        int totalDrivers = drivers.size();
        boolean allDriversKnowAllGossips = true;
        for (Driver driver : drivers) {
            if (!driver.knowsAllGossips(totalDrivers)) {
                allDriversKnowAllGossips = false;
                break;
            }
        }
        return allDriversKnowAllGossips;
    }
}
