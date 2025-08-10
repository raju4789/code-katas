package com.raju.codekatas.refactoring.payment;

class SMSNotificationService implements NotificationService {
    private final String notificationMessage;

    SMSNotificationService(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    @Override
    public void notifyUser(String userId) {
        System.out.println("[SMS to " + userId + "]: " + notificationMessage);
    }
}
