package org.sviatdev.sampleservice.domain.wallet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletRepository {
    Optional<Wallet> findById(UUID id);

    Wallet save(Wallet wallet);

    boolean delete(UUID id);

    List<Wallet> findAll();
}
