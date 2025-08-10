package com.raju.codekatas.refactoring.payment;

class EmailNotificationService implements NotificationService {
    private final String notificationMessage;

    EmailNotificationService(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    @Override
    public void notifyUser(String userId) {
        System.out.println("[EMAIL to " + userId + "]: " + notificationMessage);
    }
}
