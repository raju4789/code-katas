package com.raju.codekatas.refactoring.sessionmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {

    private SessionManager sessionManager;

    @BeforeEach
    void setup() {
        sessionManager = new SessionManager(2000); // 2 sec expiry
    }

    @Test
    void returnsExistingNonExpiredSession() {
        SessionManager.Session s1 = sessionManager.getSession("abc");
        SessionManager.Session s2 = sessionManager.getSession("abc");

        assertSame(s1, s2);
        assertFalse(s1.isExpired());
    }

    @Test
    void createsNewSessionIfExpired() throws InterruptedException {
        SessionManager.Session s1 = sessionManager.getSession("xyz");

        // Wait for expiry
        Thread.sleep(2100);

        SessionManager.Session s2 = sessionManager.getSession("xyz");

        assertNotSame(s1, s2);
        assertTrue(s1.isExpired());
        assertFalse(s2.isExpired());
    }

    @Test
    void concurrentAccessReturnsSameSession() throws InterruptedException {
        final SessionManager sm = new SessionManager(3000);

        Runnable task = () -> {
            SessionManager.Session session = sm.getSession("concurrent");
            assertNotNull(session);
            assertEquals("concurrent", session.getId());
            assertFalse(session.isExpired());
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);

        t1.start();
        t2.start();

        t1.join();
        t2.join();
    }
}

