package com.raju.codekatas.gossip.strategy;

import com.raju.codekatas.gossip.model.Driver;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DefaultGossipExchangeStrategy implements GossipExchangeStrategy {

    @Override
    public void exchangeGossips(List<Driver> driversAtStop) {
        Set<Integer> combinedGossips = new HashSet<>();
        for (Driver driver : driversAtStop) {
            combinedGossips.addAll(driver.getGossips());
        }
        for (Driver driver : driversAtStop) {
            driver.addGossips(combinedGossips);
        }
    }
}
