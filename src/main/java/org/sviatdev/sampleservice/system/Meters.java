package org.sviatdev.sampleservice.system;

import io.micrometer.core.instrument.Tag;

public final class Meters {
    // Gauges
    public static final String GAUGE_STORAGE = "storage.size";
    // Counters
    public static final String COUNTER_BLACKLIST_BY_OWNER = "blacklist.owner.checks";
    public static final String COUNTER_REPOSITORY_OPERATION = "repository.operations";

    private Meters() {
        // Do nothing
    }

    public static final class Tags {
        public static final Tag success = result("success");
        public static final Tag rejected = result("rejected");

        public Tags() {
        }

        public static Tag name(String name) {
            return Tag.of("name", name);
        }

        public static Tag result(String result) {
            return Tag.of("result", result);
        }

        public static Tag operation(String operation) {
            return Tag.of("operation", operation);
        }
    }
}
