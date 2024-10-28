package org.sviatdev.sampleservice.domain.wallet.blacklist;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sviatdev.sampleservice.domain.wallet.Wallet;
import org.sviatdev.sampleservice.domain.wallet.WalletError;
import org.sviatdev.sampleservice.system.Logging;
import org.sviatdev.sampleservice.system.Meters;

import java.util.Set;

public class ByOwnerWalletBlacklist implements WalletBlacklist {
    private static final Logger logger = LoggerFactory.getLogger(ByOwnerWalletBlacklist.class);
    private final Set<String> blacklist;
    private final Counter passedCounter;
    private final Counter rejectedCounter;


    public ByOwnerWalletBlacklist(Set<String> blacklist) {
        this.blacklist = blacklist;
        this.passedCounter = Metrics.counter(Meters.COUNTER_BLACKLIST_BY_OWNER, Tags.of(Meters.Tags.success));
        this.rejectedCounter = Metrics.counter(Meters.COUNTER_REPOSITORY_OPERATION, Tags.of(Meters.Tags.rejected));
    }

    @Override
    public void check(Wallet wallet) {
        if (blacklist.isEmpty()) {
            return;
        }

        var owner = wallet.owner();
        if (blacklist.contains(owner)) {
            logger.info("Rejected Wallet: {}", Logging.Arg.wallet(wallet));
            rejectedCounter.increment();
            throw new WalletError(WalletError.Type.BLACKLISTED_OWNER, "Owner is in blacklist: owner=%s".formatted(owner));
        }
        passedCounter.increment();
    }
}
