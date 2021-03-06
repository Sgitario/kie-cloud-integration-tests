package org.kie.cloud.tests.utils;

import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.awaitility.core.ConditionFactory;

public final class AwaitilityUtils {

	private AwaitilityUtils() {

	}

    public static final ConditionFactory awaitsFast() {
        return awaitsCustom(200, 500, secToMillis(20));
    }

    public static final ConditionFactory awaitsLong() {
        return awaitsCustom(secToMillis(1), secToMillis(10), secToMillis(60));
	}

    public static final ConditionFactory awaitsCustom(int delayMillis, int pollMillis, int atMostMillis) {
        return Awaitility.catchUncaughtExceptions()
                         .pollDelay(delayMillis, TimeUnit.MILLISECONDS)
                         .pollInterval(pollMillis, TimeUnit.MILLISECONDS)
                         .ignoreExceptions()
                         .atMost(atMostMillis, TimeUnit.MILLISECONDS);
    }

    private static int secToMillis(int seconds) {
        return seconds * 1_000;
    }
}
