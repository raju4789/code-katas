package com.raju.codekatas.gossip.strategy;

import com.raju.codekatas.gossip.model.Driver;

import java.util.List;

public interface GossipExchangeStrategy {

    void exchangeGossips(List<Driver> driversAtStop);
}
