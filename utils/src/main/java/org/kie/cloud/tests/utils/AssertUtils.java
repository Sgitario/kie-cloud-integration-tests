/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.cloud.tests.utils;

import lombok.extern.slf4j.Slf4j;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.kie.cloud.tests.utils.AwaitilityUtils.awaitsFast;

@Slf4j
public final class AssertUtils {

    private AssertUtils() {

    }

    public static final void tryAssert(Runnable action, String message) {
        try {
            awaitsFast().until(() -> {
                try {
                    action.run();
                } catch (Exception error) {
                    log.debug("Error in assertion '{}'. Will try again.", message, error);
                    return false;
                }

                return true;
            });
            assertTrue(true);
        } catch (Exception ex) {
            log.error("Error in assert", ex);
            fail(message + ". Cause: " + ex.getMessage());
        }
    }

}
