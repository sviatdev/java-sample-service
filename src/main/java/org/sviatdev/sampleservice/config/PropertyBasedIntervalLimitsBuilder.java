package org.sviatdev.sampleservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sviatdev.sampleservice.config.properties.WalletProperties;
import org.sviatdev.sampleservice.domain.wallet.Amount;
import org.sviatdev.sampleservice.domain.wallet.Transaction;
import org.sviatdev.sampleservice.domain.wallet.constraint.Interval;
import org.sviatdev.sampleservice.domain.wallet.constraint.IntervalLimitTxConstraint;
import org.sviatdev.sampleservice.domain.wallet.constraint.TxConstraint;

import java.util.stream.Stream;

public class PropertyBasedIntervalLimitsBuilder {
    private static final Logger logger = LoggerFactory.getLogger(PropertyBasedIntervalLimitsBuilder.class);

    private final WalletProperties properties;
    private final IntervalLimitFactory factory;

    public PropertyBasedIntervalLimitsBuilder(WalletProperties properties, IntervalLimitFactory factory) {
        this.properties = properties;
        this.factory = factory;
    }

    public TxConstraint build() {
        var limitProps = properties.getLimits();
        logger.info("Initializing wallet limit constraints from properties: {}", limitProps);
        var constraints = limitProps.entrySet().stream()
                .flatMap(typeLimits -> constrainsForEachInterval(typeLimits.getKey(), typeLimits.getValue()))
                .toList();
        logger.info("Initializing wallet limit constraints: {}", constraints);
        return TxConstraint.composite(constraints);
    }

    private Stream<TxConstraint> constrainsForEachInterval(Transaction.Type type, WalletProperties.Limits limits) {
        return limits.forRelevantIntervals().entrySet().stream().map(intervalLimit -> factory.create(intervalLimit.getValue(), type, intervalLimit.getKey()));
    }

    @FunctionalInterface
    interface IntervalLimitFactory {
        IntervalLimitTxConstraint create(Amount limit, Transaction.Type type, Interval interval);
    }
}
