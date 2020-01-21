package org.kie.cloud.tests.utils;

import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.awaitility.core.ConditionFactory;

public final class AwaitilityUtils {

	private AwaitilityUtils() {

	}

	public static final ConditionFactory awaits() {
		return Awaitility.with().pollDelay(1, TimeUnit.SECONDS).and().pollInterval(5, TimeUnit.SECONDS).await()
				.atMost(30, TimeUnit.SECONDS);
	}
}
