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
package be.bitbox.traindelay.tracker.core.board;


import be.bitbox.traindelay.tracker.core.TrainDeparture;
import be.bitbox.traindelay.tracker.core.station.StationId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Board {

    private final StationId stationId;
    private final LocalDateTime time;
    private List<TrainDeparture> departures;

    private Board(StationId stationId, LocalDateTime time) {
        this.stationId = stationId;
        this.time = time;
        this.departures = new ArrayList<>();
    }

    public StationId getStationId() {
        return stationId;
    }

    public static Board aBoardForStation(StationId stationId, LocalDateTime time) {
        return new Board(stationId, time);
    }

    public LocalDateTime getTime() {
        return time;
    }

    public List<TrainDeparture> getDepartures() {
        return departures;
    }

    public void addDeparture(TrainDeparture trainDeparture) {
        departures.add(trainDeparture);
    }

    @Override
    public String toString() {
        return "Board{" +
                "stationId=" + stationId +
                ", time=" + time +
                ", departures=" + departures +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Board board = (Board) o;

        if (!stationId.equals(board.stationId)) return false;
        if (!time.equals(board.time)) return false;
        return departures.equals(board.departures);
    }

    @Override
    public int hashCode() {
        int result = stationId.hashCode();
        result = 31 * result + time.hashCode();
        result = 31 * result + departures.hashCode();
        return result;
    }
}
