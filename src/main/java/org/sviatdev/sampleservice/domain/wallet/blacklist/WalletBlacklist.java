package org.sviatdev.sampleservice.domain.wallet.blacklist;

import org.sviatdev.sampleservice.domain.wallet.Wallet;
import org.sviatdev.sampleservice.domain.wallet.WalletError;

public interface WalletBlacklist {

    /**
     * Checks the wallet state and throws {@link WalletError} when violation is found.
     *
     * @param wallet object under the checks
     * @throws WalletError if state of the wallet does not satisfy business rules of {@link WalletBlacklist} implementation.
     */
    void check(Wallet wallet);
}
