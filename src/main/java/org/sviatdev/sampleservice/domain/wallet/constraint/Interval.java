package org.sviatdev.sampleservice.domain.wallet.constraint;


import org.sviatdev.sampleservice.domain.wallet.WalletError;
import org.sviatdev.sampleservice.system.TimeMachine;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.Objects;

public enum Interval implements TemporalQuery<Boolean> {
    CURRENT_MONTH(WalletError.Type.MONTHLY_LIMIT) {
        @Override
        public Boolean queryFrom(TemporalAccessor temporal) {
            return Objects.equals(YearMonth.from(TimeMachine.today()), YearMonth.from(temporal));
        }
    },
    CURRENT_DAY(WalletError.Type.DAILY_LIMIT) {
        @Override
        public Boolean queryFrom(TemporalAccessor temporal) {
            return Objects.equals(TimeMachine.today(), LocalDate.from(temporal));
        }
    },
    CURRENT_HOUR(WalletError.Type.HOURLY_LIMIT) {
        @Override
        public Boolean queryFrom(TemporalAccessor temporal) {
            return LocalDateTime.from(temporal).getHour() == TimeMachine.now().getHour() && CURRENT_DAY.queryFrom(temporal);
        }
    };

    private final WalletError.Type error;

    Interval(WalletError.Type error) {
        this.error = error;
    }

    public WalletError.Type error() {
        return error;
    }
}
