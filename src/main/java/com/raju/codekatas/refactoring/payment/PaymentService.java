package com.raju.codekatas.refactoring.payment;

import java.util.List;
import java.util.Map;


public class PaymentService {
    private final Map<PaymentMethodType, PaymentMethodHandler> paymentHandlers;
    private final Map<PaymentMethodType, List<NotificationService>> notifiers;

    PaymentService(Map<PaymentMethodType, PaymentMethodHandler> paymentHandlers, Map<PaymentMethodType, List<NotificationService>> notifiers) {
        this.paymentHandlers = paymentHandlers;
        this.notifiers = notifiers;
    }

    public static void main(String[] args) {
        Map<PaymentMethodType, PaymentMethodHandler> paymentHandlers = Map.of(
                PaymentMethodType.CREDIT_CARD, new CreditCardHandler(),
                PaymentMethodType.PAYPAL, new PaypalHandler(),
                PaymentMethodType.BANK_TRANSFER, new BankTransferHandler()
        );

        Map<PaymentMethodType, List<NotificationService>> notifiers = Map.of(
                PaymentMethodType.CREDIT_CARD, List.of(new EmailNotificationService(PaymentMethodType.CREDIT_CARD.successMessage())),
                PaymentMethodType.PAYPAL, List.of(new EmailNotificationService(PaymentMethodType.PAYPAL.successMessage())),
                PaymentMethodType.BANK_TRANSFER, List.of(new SMSNotificationService(PaymentMethodType.BANK_TRANSFER.successMessage()))
        );

        PaymentService service = new PaymentService(paymentHandlers, notifiers);
        service.processPayments(List.of("VIP123", "REG456"), PaymentMethodType.CREDIT_CARD);
    }


    public void processPayments(List<String> userIds, PaymentMethodType method) {

        try {
            PaymentMethodHandler handler = paymentHandlers.get(method);

            if (handler == null) {
                throw new IllegalArgumentException("Unknown payment method: " + method);
            }

            for (String userId : userIds) {
                if (!handler.isValidUser(userId)) {
                    System.out.println("Invalid user: " + userId);
                    continue;
                }

                try {
                    handler.processPaymentForUser(userId);
                    for (NotificationService notifier : notifiers.getOrDefault(method, List.of())) {
                        notifier.notifyUser(userId);
                    }
                } catch (Exception e) {
                    System.err.println("Error processing payment for user " + userId + ": " + e.getMessage());
                }
            }
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }
}


