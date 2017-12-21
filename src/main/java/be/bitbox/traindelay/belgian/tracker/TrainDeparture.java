/*
 * Copyright 2017 Bitbox : BelgianTrainDelayTracker
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
package be.bitbox.traindelay.belgian.tracker;

import java.time.LocalDateTime;

public class TrainDeparture {
    private final LocalDateTime time;
    private final int delay;
    private final boolean canceled;
    private final String vehicule;
    private final String platform;
    private final boolean platformChange;

    public TrainDeparture(LocalDateTime time, int delay, boolean canceled, String vehicule, String platform, boolean platformChange) {
        this.time = time;
        this.delay = delay;
        this.canceled = canceled;
        this.vehicule = vehicule;
        this.platform = platform;
        this.platformChange = platformChange;
    }

    public static TrainDeparture aTrainDeparture(LocalDateTime time, int delay, boolean canceled, String vehicule, String platform, boolean platformChange) {
        return new TrainDeparture(time, delay, canceled, vehicule, platform, platformChange);
    }

    @Override
    public String toString() {
        return "TrainDeparture{" +
                "time=" + time +
                ", delay=" + delay +
                ", canceled=" + canceled +
                ", vehicule='" + vehicule + '\'' +
                ", platform='" + platform + '\'' +
                ", platformChange=" + platformChange +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrainDeparture that = (TrainDeparture) o;

        if (delay != that.delay) return false;
        if (canceled != that.canceled) return false;
        if (platformChange != that.platformChange) return false;
        if (!time.equals(that.time)) return false;
        if (!vehicule.equals(that.vehicule)) return false;
        return platform.equals(that.platform);
    }

    @Override
    public int hashCode() {
        int result = time.hashCode();
        result = 31 * result + delay;
        result = 31 * result + (canceled ? 1 : 0);
        result = 31 * result + vehicule.hashCode();
        result = 31 * result + platform.hashCode();
        result = 31 * result + (platformChange ? 1 : 0);
        return result;
    }
}
