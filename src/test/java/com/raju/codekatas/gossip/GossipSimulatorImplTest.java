package com.raju.codekatas.gossip;

import com.raju.codekatas.gossip.simulator.GossipSimulator;
import com.raju.codekatas.gossip.simulator.GossipSimulatorImpl;
import com.raju.codekatas.gossip.strategy.DefaultGossipExchangeStrategy;
import com.raju.codekatas.gossip.strategy.GossipExchangeStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GossipSimulatorImplTest {


    private GossipSimulator gossipSimulator;

    @BeforeEach
    public void setUp() {
        GossipExchangeStrategy gossipExchangeStrategy = new DefaultGossipExchangeStrategy();
        gossipSimulator = new GossipSimulatorImpl(gossipExchangeStrategy, 480);
    }


    @Test
    @DisplayName("should return never when no routes are provided")
    void shouldReturnNeverWhenNoRoutesAreProvided() {
        List<List<Integer>> routes = null;
        String result = gossipSimulator.simulate(routes);
        assertEquals("never", result);
    }

    @Test
    @DisplayName("should return 1 when only one route is present")
    void shouldReturnZeroWhenOnlyOneRouteIsPresent() {
        List<List<Integer>> routes = List.of(List.of(1, 2, 3, 4, 5));
        String result = gossipSimulator.simulate(routes);
        assertEquals("1", result);
    }

    @Test
    @DisplayName("should return 'never' if gossips cant be exchanges throughout the day")
    void shouldReturnNeverIfGossipsCantBeExchangedThroughoutTheDay() {
        List<List<Integer>> routes = Arrays.asList(
                Arrays.asList(2, 1, 2), // Driver 1's route
                Arrays.asList(5, 2, 8) // Driver 2's route
        );
        String result = gossipSimulator.simulate(routes);
        assertEquals("never", result);
    }

    @Test
    @DisplayName("should return valid number of stops when multiple routes are present")
    void shouldReturnValidNumberOfStopsWhenMultipleRoutesArePresent() {
        List<List<Integer>> routes = Arrays.asList(
                Arrays.asList(3, 1, 2, 3), // Driver 1's route
                Arrays.asList(3, 2, 3, 1), // Driver 2's route
                Arrays.asList(4, 2, 3, 4, 5) // Driver 3's route
        );
        String result = gossipSimulator.simulate(routes);
        assertEquals("5", result);
    }

    @Test
    @DisplayName("should return '1' when all start at the same stop")
    void shouldReturnZeroWhenAllStartAtTheSameStop() {
        List<List<Integer>> routes = Arrays.asList(
                Arrays.asList(1, 2, 3, 4, 5), // Driver 1's route
                Arrays.asList(1, 2, 3, 4, 5), // Driver 2's route
                Arrays.asList(1, 2, 3, 4, 5) // Driver 3's route
        );
        String result = gossipSimulator.simulate(routes);
        assertEquals("1", result);
    }

    @Test
    @DisplayName("should return 'never' when two drivers meet, one never meets")
    void shouldReturnNeverWhenTwoDriversMeetOneNeverMeets() {
        List<List<Integer>> routes = Arrays.asList(
                Arrays.asList(1, 2, 3), // Driver 1's route
                Arrays.asList(3, 2, 1), // Driver 2's route
                Arrays.asList(4, 5, 6)  // Driver 3's route
        );
        String result = gossipSimulator.simulate(routes);
        assertEquals("never", result);
    }

}
