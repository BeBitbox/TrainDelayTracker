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
package be.bitbox.traindelay.tracker.core;

import java.time.LocalDateTime;

public class TrainDeparture {
    private final LocalDateTime time;
    private final int delay;
    private final boolean canceled;
    private final String vehicule;
    private final String platform;
    private final boolean platformChange;

    private TrainDeparture(LocalDateTime time, int delay, boolean canceled, String vehicule, String platform, boolean platformChange) {
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

    public LocalDateTime getTime() {
        return time;
    }

    public int getDelay() {
        return delay;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public String getVehicule() {
        return vehicule;
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

        if (time != null ? !time.equals(that.time) : that.time != null) return false;
        return vehicule != null ? vehicule.equals(that.vehicule) : that.vehicule == null;
    }

    @Override
    public int hashCode() {
        int result = time != null ? time.hashCode() : 0;
        result = 31 * result + (vehicule != null ? vehicule.hashCode() : 0);
        return result;
    }
}
