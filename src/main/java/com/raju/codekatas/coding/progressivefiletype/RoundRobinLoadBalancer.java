package com.raju.codekatas.coding.progressivefiletype;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

interface LoadBalancingStrategy {
    void addServer(String serverId);

    void removeServer(String serverId);

    String getNextServer();

    List<String> listServers(); // Optional: for testing/debugging

    default void addServer(String serverId, int weight) {
        throw new UnsupportedOperationException("Weighted add not supported");
    }
}

/*
 * 🔹 Problem: Round-Robin Load Balancer
 *
 * Implement a thread-safe load balancer using the **Round Robin** strategy.
 * You will maintain a dynamic list of server identifiers (e.g., "A", "B", "C").
 * The load balancer will distribute requests sequentially across the servers in the order they were added.
 *
 * 💡 Core Operations:
 *
 * 1. void addServer(String serverId)
 *    - Add a new server to the pool.
 *    - Ignore duplicates or handle as needed.
 *
 * 2. void removeServer(String serverId)
 *    - Remove an existing server.
 *    - If server does not exist, do nothing.
 *    - Maintain round-robin continuity (e.g., adjust index if needed).
 *
 * 3. String getNextServer()
 *    - Return the next server in round-robin order.
 *    - Loop back to the first server when the end is reached.
 *    - Throw exception or return null if no servers are registered.
 *
 * 4. List<String> listServers()
 *    - Return current list of servers (optional for testing).
 *
 * 🔐 Requirements:
 * - The implementation must be thread-safe for concurrent access.
 * - Ensure internal index doesn’t get corrupted under contention.
 *
 * 🧪 Example:
 *   addServer("A");
 *   addServer("B");
 *   addServer("C");
 *
 *   getNextServer(); // → "A"
 *   getNextServer(); // → "B"
 *   getNextServer(); // → "C"
 *   getNextServer(); // → "A"
 */


public class RoundRobinLoadBalancer implements LoadBalancingStrategy {

    private final List<String> servers;
    private final ReentrantReadWriteLock lock;
    private int nextServerIndex = 0;

    public RoundRobinLoadBalancer() {
        this.servers = new ArrayList<>();
        this.nextServerIndex = 0;
        this.lock = new ReentrantReadWriteLock();
    }


    @Override
    public void addServer(String serverId) {
        lock.writeLock().lock();
        try {
            if (servers.contains(serverId)) {
                return;
            }
            servers.add(serverId);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void removeServer(String serverId) {
        lock.writeLock().lock();

        try {
            servers.remove(serverId);

            if (nextServerIndex >= servers.size()) {
                nextServerIndex = 0;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public String getNextServer() throws IllegalStateException {
        lock.writeLock().lock();

        try {

            if (servers.isEmpty()) {
                throw new IllegalStateException("No servers available");
            }

            if (nextServerIndex >= servers.size()) {
                nextServerIndex = 0;
            }

            String serverId = servers.get(nextServerIndex);

            ++nextServerIndex;
            return serverId;

        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public List<String> listServers() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(servers);
        } finally {
            lock.readLock().unlock();
        }
    }
}

/*

🔹 Problem: Random Load Balancer

    Implement a thread-safe load balancer that selects backend servers at random.

    Each request is routed to one of the registered servers with uniform probability.

    💡 Core Operations:

    void addServer(String serverId)

    Adds a new server to the pool.

    Ignore duplicates.

    void removeServer(String serverId)

    Removes a server from the pool.

    Do nothing if the server does not exist.

    String getNextServer()

    Returns a server ID randomly from the pool.

    Uniform random selection — every server has equal chance.

    If no servers are registered, throw IllegalStateException.

    List<String> listServers()

    Returns the current list of servers (optional, for testing/debugging).

    🔐 Requirements:

    Must be thread-safe.

    Random selection should not bias certain servers (e.g., avoid modulo bias).

    Should be fast even under high concurrency.

    🧪 Example:

    addServer("A"); addServer("B"); addServer("C");

    getNextServer(); // → Randomly returns "A", "B", or "C"
*/

class RandomLoadBalancer implements LoadBalancingStrategy {

    private final List<String> servers;
    private final ReentrantReadWriteLock lock;
    private final Random random;

    public RandomLoadBalancer() {
        this.servers = new ArrayList<>();
        this.lock = new ReentrantReadWriteLock();
        this.random = new Random(); // Optionally use ThreadLocalRandom in final version
    }

    @Override
    public void addServer(String serverId) {
        lock.writeLock().lock();

        try {

            if (servers.contains(serverId)) {
                return;
            }

            servers.add(serverId);

        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void removeServer(String serverId) {
        lock.writeLock().lock();

        try {
            servers.remove(serverId);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public String getNextServer() throws IllegalStateException {
        lock.readLock().lock();

        try {
            if (servers.isEmpty()) {
                throw new IllegalStateException("No Servers Available");
            }
            int nextServerIndex = random.nextInt(servers.size());
            //int nextServerIndex = ThreadLocalRandom.current().nextInt(servers.size());

            return servers.get(nextServerIndex);

        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<String> listServers() {
        lock.readLock().lock();

        try {
            return new ArrayList<>(servers);
        } finally {
            lock.readLock().unlock();
        }
    }
}

/*
 * WeightedRoundRobinLoadBalancer
 *
 * Problem Statement:
 *
 * Design and implement a Weighted Round Robin Load Balancer that distributes
 * incoming requests to servers based on their assigned weights.
 *
 * Each server has a weight (positive integer) representing its relative capacity.
 * Servers with higher weights should receive proportionally more requests.
 *
 * Methods:
 *
 * - addServer(String serverId, int weight):
 *      Adds a server with a given weight.
 *      If the server already exists, update its weight.
 *
 * - removeServer(String serverId):
 *      Removes a server from the pool.
 *
 * - getNextServer():
 *      Returns the next server based on weighted round robin logic.
 *      Should throw IllegalStateException if no servers are available.
 *
 * - listServers():
 *      Returns a list of server IDs currently registered.
 *
 * Example:
 *
 * Servers:
 *  S1 with weight 3
 *  S2 with weight 1
 *  S3 with weight 2
 *
 * Expected request distribution over 6 requests:
 *  S1, S1, S1, S2, S3, S3
 *
 * This means S1 gets 3 requests, S2 gets 1, S3 gets 2, cycling continuously.
 *
 * Note:
 * - The implementation should be thread-safe.
 * - Efficient locking and state management is expected.
 */

class WeightedLoadBalancer implements LoadBalancingStrategy {

    private final List<Server> servers;
    private final ReentrantReadWriteLock lock;
    private int totalWeight;

    public WeightedLoadBalancer() {
        this.servers = new ArrayList<>();
        this.lock = new ReentrantReadWriteLock();
        this.totalWeight = 0;
    }

    @Override
    public void addServer(String serverId) {

        lock.writeLock().lock();

        try {
            addServer(serverId, 1);
        } finally {
            lock.writeLock().unlock();
        }

    }

    @Override
    public void addServer(String serverId, int weight) {
        lock.writeLock().lock();

        try {
            boolean serverAlreadyExists = servers.stream().anyMatch(server -> server.id.equals(serverId));

            if (serverAlreadyExists) {
                return;
            }
            Server server = new Server(serverId, weight);
            totalWeight += weight;
            servers.add(server);


        } finally {
            lock.writeLock().unlock();
        }

    }

    @Override
    public void removeServer(String serverId) {
        lock.writeLock().lock();

        try {
            Optional<Server> serverToDelete = servers.stream().filter(server -> server.id.equals(serverId)).findFirst();

            if (serverToDelete.isEmpty()) {
                return;
            }

            totalWeight -= serverToDelete.get().weight;
            servers.remove(serverToDelete.get());

        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * ✅ Steps to Select the Next Server:
     * Check for available servers
     * If no servers are present, return null.
     * <p>
     * Update each server's currentWeight
     * For every server in the list, increase its currentWeight by its fixed weight.
     * <p>
     * Find the server with the highest currentWeight
     * Select the server that now has the largest currentWeight.
     * <p>
     * Adjust the selected server’s currentWeight
     * Subtract the total weight of all servers from the selected server’s currentWeight.
     * <p>
     * Return the selected server's ID
     *
     * @return serverId
     */
    @Override
    public String getNextServer() {
        lock.writeLock().lock();

        try {

            if (servers.isEmpty()) {
                return null;
            }

            for (Server server : servers) {
                server.currentWeight += server.weight;
            }

            Server nextServer = servers
                    .stream()
                    .max(Comparator.comparingInt(server -> server.currentWeight))
                    .get();

            nextServer.currentWeight -= totalWeight;
            return nextServer.id;
        } finally {
            lock.writeLock().unlock();
        }

    }

    @Override
    public List<String> listServers() {
        lock.readLock().lock();

        try {
            return servers.stream().map(server -> server.id).toList();
        } finally {
            lock.readLock().unlock();
        }

    }

    private static class Server {
        String id;
        int weight;
        int currentWeight;

        Server(String id, int weight) {
            this.id = id;
            this.weight = weight;
            this.currentWeight = 0;
        }
    }
}




