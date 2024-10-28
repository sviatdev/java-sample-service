package org.sviatdev.sampleservice.config.properties;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.sviatdev.sampleservice.domain.wallet.Amount;
import org.sviatdev.sampleservice.domain.wallet.Transaction;
import org.sviatdev.sampleservice.domain.wallet.constraint.Interval;

import java.util.*;

import static org.sviatdev.sampleservice.domain.wallet.constraint.Interval.*;

@ConfigurationProperties("sample.wallet")
public class WalletProperties {
    private final Set<String> blacklist = new LinkedHashSet<>();
    private final Map<Transaction.Type, Limits> limits = new LinkedHashMap<>();
    private Amount initialBalance = Amount.ZERO;

    public Set<String> getBlacklist() {
        return blacklist;
    }

    public Map<Transaction.Type, Limits> getLimits() {
        return limits;
    }

    public Amount getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(Amount initialBalance) {
        this.initialBalance = initialBalance;
    }

    @PostConstruct
    void validate() {
        if (initialBalance.isNegative()) {
            throw new IllegalArgumentException("Parameter initialBalance=%s should be non-negative".formatted(initialBalance));
        }
        limits.values().forEach(Limits::validate);
    }

    @Override
    public String toString() {
        return "WalletProperties{" +
                "blacklist=" + blacklist +
                ", limits=" + limits +
                ", initialBalance=" + initialBalance +
                '}';
    }

    public static class Limits {
        private static final String PARAM_HOURLY = "hourly";
        private static final String PARAM_DAILY = "daily";
        private static final String PARAM_MONTHLY = "monthly";
        private Amount hourly = Amount.ZERO;
        private Amount daily = Amount.ZERO;
        private Amount monthly = Amount.ZERO;

        public static Limits of(Amount hourly, Amount daily, Amount monthly) {
            var limits = new Limits();
            limits.setHourly(hourly);
            limits.setDaily(daily);
            limits.setMonthly(monthly);
            limits.validate();
            return limits;

        }

        public Amount getHourly() {
            return hourly;
        }

        public void setHourly(Amount hourly) {
            this.hourly = hourly;
        }

        public Amount getDaily() {
            return daily;
        }

        public void setDaily(Amount daily) {
            this.daily = daily;
        }

        public Amount getMonthly() {
            return monthly;
        }

        public void setMonthly(Amount monthly) {
            this.monthly = monthly;
        }

        public Map<Interval, Amount> forRelevantIntervals() {
            var limits = new EnumMap<Interval, Amount>(Interval.class);
            addRelevantIntervalLimit(limits, CURRENT_HOUR, hourly);
            addRelevantIntervalLimit(limits, CURRENT_DAY, daily);
            addRelevantIntervalLimit(limits, CURRENT_MONTH, monthly);
            return Map.copyOf(limits);
        }

        private void addRelevantIntervalLimit(EnumMap<Interval, Amount> intervals, Interval interval, Amount limit) {
            if (isRelevant(limit)) {
                intervals.put(interval, limit);
            }
        }

        public boolean isRelevant(Amount limit) {
            return limit.isPositive();
        }

        void validate() {
            checkIsNonNegative(PARAM_HOURLY, hourly);
            checkIsNonNegative(PARAM_DAILY, daily);
            checkIsNonNegative(PARAM_MONTHLY, monthly);
            checkRelativeValues(PARAM_HOURLY, PARAM_DAILY, hourly, daily);
            checkRelativeValues(PARAM_DAILY, PARAM_MONTHLY, daily, monthly);
            checkRelativeValues(PARAM_HOURLY, PARAM_MONTHLY, hourly, monthly);
        }

        private void checkIsNonNegative(String parameter, Amount value) {
            if (value.isNegative()) {
                throw new IllegalArgumentException("Parameter %s=%s should not be less than 0"
                        .formatted(parameter, value));
            }
        }

        private void checkRelativeValues(String parameter, String boundaryParameter, Amount value, Amount boundaryValue) {
            if (value.isPositive() && boundaryValue.isPositive() && value.isGreaterThan(boundaryValue)) {
                throw new IllegalArgumentException("Parameter %s=%s should not be greater than %s=%s"
                        .formatted(parameter, value, boundaryParameter, boundaryValue));
            }
        }

        @Override
        public String toString() {
            return "Limits{" +
                    "hourly=" + hourly +
                    ", daily=" + daily +
                    ", monthly=" + monthly +
                    '}';
        }
    }
}
