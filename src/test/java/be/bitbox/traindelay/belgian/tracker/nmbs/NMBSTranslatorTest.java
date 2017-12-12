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
package be.bitbox.traindelay.belgian.tracker.nmbs;

import be.bitbox.traindelay.belgian.tracker.Board;
import be.bitbox.traindelay.belgian.tracker.BoardTranslationException;
import be.bitbox.traindelay.belgian.tracker.TrainDeparture;
import be.bitbox.traindelay.belgian.tracker.station.StationId;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static be.bitbox.traindelay.belgian.tracker.station.StationId.aStationId;
import static java.time.Month.SEPTEMBER;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class NMBSTranslatorTest {

    private static final String STATION_ID = "BE.NMBS.008892106";
    private NMBSTranslator nmbsTranslator = new NMBSTranslator();

    @Test(expected = BoardTranslationException.class)
    public void translateFromNull_ShouldThrowExceptions() {
        nmbsTranslator.translateFrom(null);
    }

    @Test(expected = BoardTranslationException.class)
    public void translateFromEmptyBoard_ShouldThrowExceptions() {
        nmbsTranslator.translateFrom(new LiveBoard());
    }

    @Test(expected = BoardTranslationException.class)
    public void translateFromBoardWithEmptyStationID_ShouldThrowExceptions() {
        LiveBoard liveBoard = new LiveBoard();
        liveBoard.setStationinfo(new Stationinfo());
        nmbsTranslator.translateFrom(liveBoard);
    }

    @Test
    public void translateFromEmptyBoard_NoDepartures_shouldBeOk() {
        LiveBoard liveBoard = new LiveBoard();
        Stationinfo stationinfo = new Stationinfo();
        stationinfo.setId(STATION_ID);
        liveBoard.setStationinfo(stationinfo);
        liveBoard.setTimestamp(1220227200L);

        Board board = nmbsTranslator.translateFrom(liveBoard);

        StationId expectedStationId = aStationId(STATION_ID);
        LocalDateTime expectedTime = LocalDateTime.of(2008, SEPTEMBER, 1, 2, 0, 0);
        assertThat(board.getTime(), is(expectedTime));
        assertThat(board.getStationId(), is(expectedStationId));
    }

    @Test
    public void translateFromBoard_OneDeparture_shouldBeOk() {
        LiveBoard liveBoard = new LiveBoard();
        Stationinfo stationinfo = new Stationinfo();
        stationinfo.setId(STATION_ID);
        liveBoard.setStationinfo(stationinfo);
        Departures departures = new Departures();
        Departure departure = new Departure();
        departure.setCanceled(1);
        departure.setDelay(220);
        departures.setDeparture(Collections.singletonList(departure));
        liveBoard.setDepartures(departures);

        Board board = nmbsTranslator.translateFrom(liveBoard);

        List<TrainDeparture> trainDepartures = board.getDepartures();
        assertThat(trainDepartures.size(), is(1));
        TrainDeparture expectedTrainDeparture = null;
        assertThat(trainDepartures.get(0), is(expectedTrainDeparture));
    }

}