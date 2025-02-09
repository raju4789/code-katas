package com.raju.codekatas.gossip.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Driver {
    private final int id;
    private final List<Integer> route;
    private final Set<Integer> gossips;

    public Driver(int id, List<Integer> route) {
        this.id = id;
        this.route = route;
        this.gossips = new HashSet<>();
        this.gossips.add(id); // Each driver starts with their own gossip
    }

    public int getId() {
        return id;
    }

    public int getCurrentStop(int minute) {
        return route.get(minute % route.size());
    }

    public Set<Integer> getGossips() {
        return gossips;
    }

    public void addGossips(Set<Integer> newGossips) {
        gossips.addAll(newGossips);
    }

    public boolean knowsAllGossips(int totalDrivers) {
        return gossips.size() == totalDrivers;
    }
}
