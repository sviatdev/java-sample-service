package org.sviatdev.sampleservice.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.sviatdev.sampleservice.config.properties.WalletProperties;
import org.sviatdev.sampleservice.domain.wallet.Amount;
import org.sviatdev.sampleservice.domain.wallet.blacklist.ByOwnerWalletBlacklist;
import org.sviatdev.sampleservice.domain.wallet.blacklist.WalletBlacklist;
import org.sviatdev.sampleservice.domain.wallet.constraint.IntervalLimitTxConstraint;
import org.sviatdev.sampleservice.domain.wallet.constraint.TxConstraint;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(WalletProperties.class)
public class WalletConfig {

    @Bean
    public WalletBlacklist walletBlacklist(WalletProperties props) {
        return new ByOwnerWalletBlacklist(props.getBlacklist());
    }

    @Bean
    public TxConstraint txConstraint(WalletProperties props) {
       return new PropertyBasedIntervalLimitsBuilder(props, IntervalLimitTxConstraint::new).build();
    }

    @Component
    @ConfigurationPropertiesBinding
    static class StringToAmountConverter implements Converter<String, Amount> {

        @Override
        public Amount convert(String source) {
            return Amount.of(source);
        }
    }

    @Component
    @ConfigurationPropertiesBinding
    static class NumberToAmountConverter implements Converter<Number, Amount> {
        @Override
        public Amount convert(Number source) {
            return Amount.of(String.valueOf(source));
        }
    }
}
