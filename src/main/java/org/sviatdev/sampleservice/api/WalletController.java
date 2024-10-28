package org.sviatdev.sampleservice.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import org.sviatdev.sampleservice.config.properties.WalletProperties;
import org.sviatdev.sampleservice.domain.wallet.Amount;
import org.sviatdev.sampleservice.domain.wallet.Wallet;
import org.sviatdev.sampleservice.domain.wallet.WalletRepository;
import org.sviatdev.sampleservice.domain.wallet.blacklist.WalletBlacklist;
import org.sviatdev.sampleservice.domain.wallet.constraint.TxConstraint;
import org.sviatdev.sampleservice.system.Logging;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

@RestController
@RequestMapping("/wallets")
public class WalletController {
    private static final Logger logger = LoggerFactory.getLogger(WalletController.class);
    private final ConversionService conversionService;
    private final WalletBlacklist blacklist;
    private final WalletProperties props;
    private final WalletRepository repository;
    private final TxConstraint txConstraint;

    public WalletController(ConversionService conversionService,
                            WalletBlacklist blacklist,
                            WalletProperties props,
                            WalletRepository repository,
                            TxConstraint txConstraint) {
        this.conversionService = conversionService;
        this.blacklist = blacklist;
        this.props = props;
        this.repository = repository;
        this.txConstraint = txConstraint;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestParam String owner) {
        var wallet = new Wallet(txConstraint, owner, props.getInitialBalance());
        blacklist.check(wallet);
        wallet = repository.save(wallet);
        logger.debug("Successful Wallet registration: {}", Logging.Arg.wallet(wallet));
        return ResponseEntity.created(selfUri().pathSegment("{id}").build(Map.of("id", wallet.id()))).build();
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<Wallet> getById(@PathVariable UUID id) {
//        return ResponseEntity.of(repository.findById(id).map(this::toWalletResponse));
//    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if(repository.delete(id)) {
            logger.debug("Successful Wallet deletion: {}", Logging.Arg.walletId(id));
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

//    @GetMapping
//    public List<WalletListValue> findAll() {
//        return repository.findAll().stream().map(this::toWalletListValue).toList();
//    }

    @PostMapping("/{id}/deposits")
    public ResponseEntity<Void> deposit(@PathVariable UUID id, @RequestParam BigDecimal amount) {
        return handleOperation(id, amount, Wallet::deposit);
    }

    @PostMapping("/{id}/withdrawals")
    public ResponseEntity<Void> withdraw(@PathVariable UUID id, @RequestParam BigDecimal amount) {
        return handleOperation(id, amount, Wallet::withdraw);
    }

    private ResponseEntity<Void> handleOperation(UUID id, BigDecimal value, BiConsumer<Wallet, Amount> operation) {
        var amount = Amount.of(value);
        return repository.findById(id).map(wallet -> {
            operation.accept(wallet, amount);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    private UriComponentsBuilder selfUri() {
        return MvcUriComponentsBuilder.fromController(getClass());
    }

//    private WalletResource toWalletResponse(Wallet wallet) {
//        return conversionService.convert(wallet, WalletResource.class);
//    }
//
//    private WalletListValue toWalletListValue(Wallet wallet) {
//        return conversionService.convert(wallet, WalletListValue.class);
//    }
}
