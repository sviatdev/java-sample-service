package org.sviatdev.sampleservice.domain.wallet;

public class WalletError extends RuntimeException {
    private final Type type;

    public WalletError(Type type, String message) {
        super(message);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        BLACKLISTED_OWNER,
        DUPLICATE_BY_OWNER,
        NEGATIVE_BALANCE,
        NON_POSITIVE_OPERATION_AMOUNT,
        HOURLY_LIMIT,
        DAILY_LIMIT,
        MONTHLY_LIMIT;

        public String fullName() {
            return WalletError.class.getSimpleName() + "." + this;
        }
    }

}
