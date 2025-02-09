package raju.codekatas.bank.core;

import com.raju.codekatas.bank.Account;
import com.raju.codekatas.bank.core.AccountImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Problem Statement: <a href="https://kata-log.rocks/banking-kata">Bank Kata</a>
 */
public class AccountImplTest {

    @Test
    @DisplayName("should deposit amount to account")
    void test1() {
        Account account = new AccountImpl(0);
        account.deposit(100);
        assertEquals("Your account balance is: 100", account.printStatement());
    }

    @Test
    @DisplayName("should withdraw amount from account")
    void test2() {
        Account account = new AccountImpl(100);
        account.withdraw(50);
        assertEquals("Your account balance is: 50", account.printStatement());
    }

    @Test
    @DisplayName("should throw exception when withdraw amount is more than balance")
    void test3() {
        Account account = new AccountImpl(100);
        try {
            account.withdraw(200);
        } catch (Exception e) {
            assertEquals("Insufficient balance", e.getMessage());
        }
    }

    @Test
    @DisplayName("should throw exception when deposit amount is negative")
    void test4() {
        Account account = new AccountImpl(100);
        try {
            account.deposit(-100);
        } catch (Exception e) {
            assertEquals("Amount should be positive", e.getMessage());
        }
    }

}
