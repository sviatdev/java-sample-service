package org.sviatdev.sampleservice.system;

import net.logstash.logback.argument.StructuredArgument;
import org.sviatdev.sampleservice.domain.wallet.Transaction;
import org.sviatdev.sampleservice.domain.wallet.Wallet;

import java.math.BigDecimal;
import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.kv;

public final class Logging {
    public static final String TRANSACTION = "tx";
    public static final String WALLET = "wallet";

    private Logging() {
    }

    public static final class Arg {
        private Arg() {

        }

        public static StructuredArgument tx(Transaction transaction) {
            return kv(TRANSACTION, new TxArg(transaction.id(), transaction.type(), transaction.amount().toDecimal()));
        }

        public static StructuredArgument wallet(Wallet wallet) {
            return kv(WALLET, new WalletArg(wallet.id(), wallet.owner()));
        }

        public static StructuredArgument walletId(UUID id) {
            return kv(WALLET, new WalletArg(id, null));
        }
    }

    private record TxArg(UUID id, Transaction.Type type, BigDecimal amount) {

    }

    private record WalletArg(UUID id, String owner) {

    }
}
