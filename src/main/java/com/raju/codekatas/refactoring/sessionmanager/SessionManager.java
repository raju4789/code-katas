package com.raju.codekatas.refactoring.sessionmanager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    private final long ttl;

    public SessionManager(long ttl) {
        this.ttl = ttl;
    }

    public Session getSession(String sessionId) {

        Session session = sessions.get(sessionId);

        if (session != null && !session.isExpired()) {
            return session;
        }

        return sessions.compute(sessionId, (key, existingSession) -> {
            if (existingSession != null && !existingSession.isExpired()) {
                return existingSession;
            }
            return createNewSession(key);
        });
    }

    private Session createNewSession(String sessionId) {
        // Simulate expensive session creation
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        return new Session(sessionId, System.currentTimeMillis() + ttl);
    }

    public static class Session {
        private final String id;
        private final long expiryTime;

        public Session(String id, long expiryTime) {
            this.id = id;
            this.expiryTime = expiryTime;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }

        public String getId() {
            return id;
        }
    }
}

