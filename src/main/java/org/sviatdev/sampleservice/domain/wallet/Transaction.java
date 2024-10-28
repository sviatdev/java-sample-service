package org.sviatdev.sampleservice.domain.wallet;

import org.sviatdev.sampleservice.system.TimeMachine;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class Transaction {
    private final UUID id;
    private final Type type;
    private final LocalDateTime time;
    private final Amount amount;
    private final Amount balance;
    private final String note;

    public Transaction(Type type, Amount amount, Amount balance, String note) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.time = TimeMachine.now();
        this.amount = ensurePositive(amount);
        this.balance = type.applyToBalance(balance, amount);
        this.note = note;
    }

    public static Transaction deposit(Amount amount, Amount balance, String note) {
        return new Transaction(Type.DEPOSIT, amount, balance, note);
    }

    public static Transaction withdraw(Amount amount, Amount balance, String note) {
        return new Transaction(Type.WITHDRAW, amount, balance, note);
    }

    public UUID id() {
        return id;
    }

    public Type type() {
        return type;
    }

    public LocalDateTime time() {
        return time;
    }

    public Amount amount() {
        return amount;
    }

    public Amount balance() {
        return balance;
    }

    public String note() {
        return note;
    }

    private Amount ensurePositive(Amount amount) {
        return Optional.of(amount)
                .filter(Amount::isPositive)
                .orElseThrow(() -> new WalletError(WalletError.Type.NON_POSITIVE_OPERATION_AMOUNT,
                        "Amount=%s should be a positive number".formatted(amount)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", type=" + type +
                ", time=" + time +
                ", amount=" + amount +
                ", balance=" + balance +
                ", note='" + note + '\'' +
                '}';
    }

    public enum Type {
        DEPOSIT {
            @Override
            Amount applyToBalance(Amount balance, Amount amount) {
                return balance.add(amount);
            }
        }, WITHDRAW {
            @Override
            Amount applyToBalance(Amount balance, Amount amount) {
                var result = balance.subtract(amount);
                if (result.isNegative()) {
                    throw new WalletError(WalletError.Type.NEGATIVE_BALANCE,
                            "Operation %s with amount=%s leads to negative value=%s".formatted(this, amount, result));
                }
                return result;
            }
        };


        abstract Amount applyToBalance(Amount balance, Amount amount);
    }
}
