package org.sviatdev.sampleservice.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.concurrent.atomic.AtomicReference;

public final class TimeMachine {
    private static final Logger logger = LoggerFactory.getLogger(TimeMachine.class);
    private static final AtomicReference<Clock> clock = new AtomicReference<>(Clock.systemUTC());

    public TimeMachine() {
    }

    public static void setClock(Clock clock) {
        TimeMachine.clock.set(clock);
        logger.debug("Applying new clock implmentation. clock={}", clock);
    }

    public static LocalDateTime now() {return LocalDateTime.now(clock.get());}

    public static LocalDate today() {
        return LocalDate.now(clock.get());
    }

    public static OffsetDateTime enrichWithOffset(LocalDateTime time) {
        return OffsetDateTime.of(time, clock.get().getZone().getRules().getOffset(time));
    }
}
