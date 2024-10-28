package org.sviatdev.sampleservice.domain.wallet;

import net.logstash.logback.argument.StructuredArgument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sviatdev.sampleservice.domain.wallet.constraint.TxConstraint;
import org.sviatdev.sampleservice.system.Logging.Arg;
import org.sviatdev.sampleservice.system.TimeMachine;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class Wallet {
    private static final Logger logger = LoggerFactory.getLogger(Wallet.class);
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final TxConstraint txConstraint;
    private final StructuredArgument walletLogArg;
    private final UUID id;
    private final LocalDateTime createdAt;
    private final String owner;
    private final List<Transaction> history = new ArrayList<>();
    private Amount balance;

    public Wallet(TxConstraint txConstraint, String owner, Amount balance) {
        this.id = UUID.randomUUID();
        this.createdAt = TimeMachine.now();
        this.txConstraint = requireNonNull(txConstraint, "Cannot create Wallet with 'null' txConstraint");
        this.owner = requireNonNull(owner, "Cannot create Wallet with 'null' owner");
        this.balance = new ValidBalance(balance).value();
        this.walletLogArg = Arg.wallet(this);
    }

    public void deposit(Amount amount) {
        //TODO
    }

    public void withdraw(Amount amount) {
        // TODO
    }

    public UUID id() {
        return id;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }

    public String owner() {
        return owner;
    }

    public List<Transaction> history() {
        lock.readLock().lock();
        try {
            return List.copyOf(history);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Amount balance() {
        lock.readLock().lock();
        try {
            return balance;
        } finally {
            lock.readLock().unlock();
        }
    }

    public TxConstraint txConstraint() {
        return txConstraint;
    }

    private void applyTx(Supplier<Transaction> transaction) {
        lock.writeLock().lock();
        try {
            var tx = transaction.get();
            logger.debug("Processing transaction. {}. {}.", walletLogArg, Arg.tx(tx));
            txConstraint.verify(this, tx);
            balance = tx.balance();
            history.add(tx);
            logger.debug("Transacion success. {}. {}.", walletLogArg, Arg.tx(tx));
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", owner='" + owner + '\'' +
                ", value=" + balance +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wallet wallet = (Wallet) o;
        return id.equals(wallet.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    private record ValidBalance(Amount value) {
        ValidBalance {
            requireNonNull(value, "Cannot create Wallet with 'null' balance");
            if (value.isNegative()) {
                throw new WalletError(WalletError.Type.NEGATIVE_BALANCE, "Wallet cannot have a negative balance=%s".formatted(value));
            }
        }
    }
}
