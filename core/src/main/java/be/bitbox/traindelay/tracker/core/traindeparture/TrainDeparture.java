/*
 * Copyright 2017 Bitbox : TrainDelayTracker
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
package be.bitbox.traindelay.tracker.core.traindeparture;

import java.time.LocalDateTime;
import java.util.Objects;

public class TrainDeparture {
    private final LocalDateTime time;
    private final int delay;
    private final boolean canceled;
    private final String vehicle;
    private final String platform;
    private final boolean platformChange;

    private TrainDeparture(LocalDateTime time, int delay, boolean canceled, String vehicle, String platform, boolean platformChange) {
        this.time = time;
        this.delay = delay;
        this.canceled = canceled;
        this.vehicle = vehicle;
        this.platform = platform;
        this.platformChange = platformChange;
    }

    public static TrainDeparture aTrainDeparture(LocalDateTime time, int delay, boolean canceled, String vehicle, String platform, boolean platformChange) {
        return new TrainDeparture(time, delay, canceled, vehicle, platform, platformChange);
    }

    public LocalDateTime getTime() {
        return time;
    }

    public int getDelay() {
        return delay;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public String getVehicle() {
        return vehicle;
    }

    public String getPlatform() {
        return platform;
    }

    public boolean isPlatformChange() {
        return platformChange;
    }

    @Override
    public String toString() {
        return "TrainDeparture{" +
                "time=" + time +
                ", delay=" + delay +
                ", canceled=" + canceled +
                ", vehicle='" + vehicle + '\'' +
                ", platform='" + platform + '\'' +
                ", platformChange=" + platformChange +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrainDeparture that = (TrainDeparture) o;

        if (!Objects.equals(time, that.time)) return false;
        return Objects.equals(vehicle, that.vehicle);
    }

    @Override
    public int hashCode() {
        int result = time != null ? time.hashCode() : 0;
        result = 31 * result + (vehicle != null ? vehicle.hashCode() : 0);
        return result;
    }
}
