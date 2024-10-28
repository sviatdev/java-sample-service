package org.sviatdev.sampleservice.domain.wallet.constraint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sviatdev.sampleservice.domain.wallet.Amount;
import org.sviatdev.sampleservice.domain.wallet.Transaction;
import org.sviatdev.sampleservice.domain.wallet.Wallet;
import org.sviatdev.sampleservice.domain.wallet.WalletError;
import org.sviatdev.sampleservice.system.Logging;

import java.util.List;
import java.util.function.Predicate;

import static javax.management.Query.and;

public class IntervalLimitTxConstraint implements TxConstraint {
    private static final Logger logger = LoggerFactory.getLogger(IntervalLimitTxConstraint.class);

    private final Amount limit;
    private final Transaction.Type txType;
    private final Interval interval;
    private final Predicate<Transaction> transactionPredicate;

    public IntervalLimitTxConstraint(Amount limit, Transaction.Type txType, Interval interval) {
        this.limit = limit;
        this.txType = txType;
        this.interval = interval;
        this.transactionPredicate = transactionPredicate(txType, interval);
    }

    private static Amount validLimit(Amount limit) {
        if(limit == null || !limit.isPositive()) {
            throw new IllegalArgumentException("Limit=%s should be non-null and not be less than 0".formatted(limit));
        }
        return limit;
    }

    private static Predicate<Transaction> transactionPredicate(Transaction.Type txType, Interval interval) {
        Predicate<Transaction> byType = tx -> tx.type() == txType;
        Predicate<Transaction> byTime = tx -> tx.time().query(interval);
        return byType.and(byTime);
    }
    @Override
    public void verify(Wallet wallet, Transaction tx) throws WalletError {
        if(tx.type() != txType) {
            return;
        }
        var sum = sumTransactions(wallet.history(), transactionPredicate).add(tx.amount());
        if(sum.isGreaterThan(limit)) {
            logger.info("Limits violation. {}. Violation: error={}; limit={}; actual={}", Logging.Arg.wallet(wallet), interval.error(), limit, sum);
            throw new WalletError(interval.error(), "Limit=%s; Actual=%s".formatted(limit, sum));
        }
    }

    @Override
    public String toString() {
        return "IntervalLimitTxConstraint{" +
                "limit=" + limit +
                ", txType=" + txType +
                ", interval=" + interval +
                ", transactionPredicate=" + transactionPredicate +
                '}';
    }

    private Amount sumTransactions(List<Transaction> transactions, Predicate<Transaction> predicate) {
        return transactions.stream().filter(predicate).map(Transaction::amount).reduce(Amount.ZERO, Amount::add);
    }
}
