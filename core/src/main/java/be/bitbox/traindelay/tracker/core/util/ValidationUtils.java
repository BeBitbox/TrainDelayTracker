/*
 * Copyright 2018 Bitbox : TrainDelayTracker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.bitbox.traindelay.tracker.core.util;

public class ValidationUtils {

    private ValidationUtils() {}

    public static void checkNotEmpty(String toBeValidated, String message) {
        if (toBeValidated == null || toBeValidated.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static boolean isNumeric(String potentialNumber) {
        if (potentialNumber == null || potentialNumber.isEmpty()) {
            return false;
        }
        return potentialNumber.matches("-?\\d+(\\.\\d+)?");
    }
}