package org.sviatdev.sampleservice.domain.wallet.constraint;

import org.sviatdev.sampleservice.domain.wallet.Transaction;
import org.sviatdev.sampleservice.domain.wallet.Wallet;
import org.sviatdev.sampleservice.domain.wallet.WalletError;

import java.util.List;

@FunctionalInterface
public interface TxConstraint {
    TxConstraint EMPTY = (wallet, tx) -> {};

    static TxConstraint composite(List<TxConstraint> constraints) {
        return constraints.isEmpty() ? EMPTY : new Composite(constraints);
    }

    void verify(Wallet wallet, Transaction tx) throws WalletError;

    record Composite(List<TxConstraint> constraints) implements TxConstraint {
        public Composite {
            constraints = List.copyOf(constraints);
        }

        @Override
        public void verify(Wallet wallet, Transaction tx) throws WalletError {
            constraints.forEach(constraint -> constraint.verify(wallet, tx));
        }
    }
}
