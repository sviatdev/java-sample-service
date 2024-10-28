package org.sviatdev.sampleservice.domain.wallet;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class Amount {

    public static final Amount ZERO = new Amount(BigDecimal.ZERO);

    private final BigDecimal value;

    public Amount(BigDecimal decimal) {
        this.value = requireNonNull(decimal, "Cannot create Amount from null value")
                .setScale(2, RoundingMode.UNNECESSARY);
    }

    private Amount(String stringValue) {
        this(new BigDecimal(stringValue));
    }

    public static Amount of(BigDecimal decimal) {
        return new Amount(decimal);
    }

    public static Amount of(String stringValue) {
        return new Amount(stringValue);
    }

    public BigDecimal toDecimal() {
        return value;
    }

    public Amount add(Amount that) {
        return new Amount(value.add(that.value));
    }

    public Amount subtract(Amount that) {
        return new Amount(value.subtract(that.value));
    }

    public boolean isGreaterThan(Amount that) {
        return value.compareTo(that.value) > 0;
    }

    public boolean isLessThan(Amount that) {
        return value.compareTo(that.value) < 0;
    }

    public boolean isPositive() {
        return isGreaterThan(ZERO);
    }

    public boolean isNegative() {
        return isLessThan(ZERO);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Amount amount = (Amount) o;
        return Objects.equals(value, amount.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return "Amount{" +
                "value=" + value +
                '}';
    }
}
