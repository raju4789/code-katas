package com.raju.codekatas.refactoring.eventsourcing;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * LEGACY CODE - Simple bank account with mutable state and basic audit trail
 * <p>
 * REFACTORING CHALLENGE:
 * Convert this to Event Sourcing pattern with immutable events and state derivation.
 * <p>
 * TIME LIMIT: 40 minutes
 * <p>
 * REQUIREMENTS:
 * 1. Create Event classes for all operations (AccountCreated, MoneyDeposited, MoneyWithdrawn)
 * 2. Store events instead of mutating state directly
 * 3. Derive current state by replaying events
 * 4. Support querying balance at any point in time
 * 5. Make the system immutable and thread-safe
 */
public class BankAccountL {

    private String accountId;
    private String customerName;
    private double balance;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
    private List<String> transactionHistory; // Simple string-based audit trail

    public BankAccountL(String accountId, String customerName) {
        this.accountId = accountId;
        this.customerName = customerName;
        this.balance = 0.0;
        this.createdAt = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
        this.transactionHistory = new ArrayList<>();

        // Add creation to history
        transactionHistory.add(String.format("%s: Account created for %s",
                LocalDateTime.now(), customerName));

        System.out.println("Account created: " + accountId + " for " + customerName);
    }

    public static void main(String[] args) {
        // Demo of the legacy bank account system
        BankAccountL account1 = new BankAccountL("ACC-001", "John Doe");
        BankAccountL account2 = new BankAccountL("ACC-002", "Jane Smith");

        try {
            // Perform some transactions
            account1.deposit(1000.0, "Initial deposit");
            account1.withdraw(150.0, "ATM withdrawal");
            account1.transfer(account2, 200.0, "Payment to Jane");
            account2.deposit(500.0, "Salary deposit");

            // Show current state
            System.out.println("\n=== Current State ===");
            System.out.println(account1.getAccountInfo());
            System.out.println(account2.getAccountInfo());

            // Show transaction history
            System.out.println("\n=== Account 1 Transaction History ===");
            for (String transaction : account1.getTransactionHistory()) {
                System.out.println(transaction);
            }

            // Try historical balance query (fragile!)
            LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
            System.out.println("\nBalance one minute ago: $" + account1.getBalanceAtTime(oneMinuteAgo));

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void deposit(double amount, String description) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        double oldBalance = this.balance;
        this.balance += amount;
        this.lastModified = LocalDateTime.now();

        String transaction = String.format("%s: Deposited $%.2f - %s (Balance: $%.2f -> $%.2f)",
                LocalDateTime.now(), amount, description, oldBalance, this.balance);
        transactionHistory.add(transaction);

        System.out.println("Deposit successful: " + accountId + ", Amount: $" + amount);
    }

    public void withdraw(double amount, String description) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        if (this.balance < amount) {
            String failedTransaction = String.format("%s: FAILED WITHDRAWAL $%.2f - %s (Insufficient funds, balance: $%.2f)",
                    LocalDateTime.now(), amount, description, this.balance);
            transactionHistory.add(failedTransaction);
            throw new IllegalStateException("Insufficient funds");
        }

        double oldBalance = this.balance;
        this.balance -= amount;
        this.lastModified = LocalDateTime.now();

        String transaction = String.format("%s: Withdrew $%.2f - %s (Balance: $%.2f -> $%.2f)",
                LocalDateTime.now(), amount, description, oldBalance, this.balance);
        transactionHistory.add(transaction);

        System.out.println("Withdrawal successful: " + accountId + ", Amount: $" + amount);
    }

    public void transfer(BankAccountL toAccount, double amount, String description) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        if (this.balance < amount) {
            String failedTransaction = String.format("%s: FAILED TRANSFER $%.2f to %s - %s (Insufficient funds)",
                    LocalDateTime.now(), amount, toAccount.getAccountId(), description);
            transactionHistory.add(failedTransaction);
            throw new IllegalStateException("Insufficient funds for transfer");
        }

        // Perform transfer
        double oldFromBalance = this.balance;
        double oldToBalance = toAccount.balance;

        this.balance -= amount;
        toAccount.balance += amount;

        LocalDateTime now = LocalDateTime.now();
        this.lastModified = now;
        toAccount.lastModified = now;

        // Add to both accounts' history
        String fromTransaction = String.format("%s: Transferred $%.2f to %s - %s (Balance: $%.2f -> $%.2f)",
                now, amount, toAccount.getAccountId(), description, oldFromBalance, this.balance);
        this.transactionHistory.add(fromTransaction);

        String toTransaction = String.format("%s: Received $%.2f from %s - %s (Balance: $%.2f -> $%.2f)",
                now, amount, this.accountId, description, oldToBalance, toAccount.balance);
        toAccount.transactionHistory.add(toTransaction);

        System.out.println("Transfer successful: " + this.accountId + " -> " + toAccount.getAccountId() + ", Amount: $" + amount);
    }

    public double getBalance() {
        return balance;
    }

    public double getBalanceAtTime(LocalDateTime targetTime) {
        // This is a naive and error-prone way to calculate historical balance
        // by parsing transaction history strings
        double calculatedBalance = 0.0;

        for (String transaction : transactionHistory) {
            if (transaction.contains("Account created")) {
                continue; // No balance change
            }

            // Extract timestamp from transaction string (fragile parsing!)
            String timestampStr = transaction.substring(0, transaction.indexOf(":"));
            try {
                LocalDateTime transactionTime = LocalDateTime.parse(timestampStr);
                if (transactionTime.isAfter(targetTime)) {
                    break; // Stop at target time
                }

                // Parse amount changes (very fragile!)
                if (transaction.contains("Deposited")) {
                    String amountStr = transaction.substring(transaction.indexOf("$") + 1, transaction.indexOf(" -"));
                    calculatedBalance += Double.parseDouble(amountStr);
                } else if (transaction.contains("Withdrew")) {
                    String amountStr = transaction.substring(transaction.indexOf("$") + 1, transaction.indexOf(" -"));
                    calculatedBalance -= Double.parseDouble(amountStr);
                } else if (transaction.contains("Transferred")) {
                    String amountStr = transaction.substring(transaction.indexOf("$") + 1, transaction.indexOf(" to"));
                    calculatedBalance -= Double.parseDouble(amountStr);
                } else if (transaction.contains("Received")) {
                    String amountStr = transaction.substring(transaction.indexOf("$") + 1, transaction.indexOf(" from"));
                    calculatedBalance += Double.parseDouble(amountStr);
                }
            } catch (Exception e) {
                // Parsing failed - this approach is very fragile!
                System.err.println("Error parsing transaction: " + transaction);
            }
        }

        return calculatedBalance;
    }

    public List<String> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }

    public String getAccountInfo() {
        return String.format("Account[%s] Customer: %s, Balance: $%.2f, Created: %s, Last Modified: %s",
                accountId, customerName, balance, createdAt, lastModified);
    }

    // Getters
    public String getAccountId() {
        return accountId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }
}
