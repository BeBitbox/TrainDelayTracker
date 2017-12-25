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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static be.bitbox.traindelay.belgian.tracker.Board.aBoardForStation;
import static be.bitbox.traindelay.belgian.tracker.TrainDeparture.aTrainDeparture;
import static be.bitbox.traindelay.belgian.tracker.station.StationId.aStationId;
import static java.time.Month.DECEMBER;
import static java.time.Month.SEPTEMBER;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class NMBSTranslatorTest {

    private static final String STATION_ID = "BE.NMBS.008892106";
    private NMBSTranslator nmbsTranslator = new NMBSTranslator();
    private static final String VEHICLE = "BE.NMBS.IC3016";
    private static final String PLATFORM = "4";

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
        Board expectedBoard = aBoardForStation(expectedStationId, expectedTime);
        assertThat(board, is(expectedBoard));
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
        departure.setDelay(60);
        departure.setTime(1513703280L);
        Platforminfo platforminfo = new Platforminfo();
        platforminfo.setNormal(0);
        platforminfo.setName(PLATFORM);
        departure.setPlatforminfo(platforminfo);
        departure.setVehicle(VEHICLE);
        departures.setDeparture(Collections.singletonList(departure));
        liveBoard.setDepartures(departures);

        Board board = nmbsTranslator.translateFrom(liveBoard);

        List<TrainDeparture> trainDepartures = board.getDepartures();
        assertThat(trainDepartures.size(), is(1));
        LocalDateTime expectedTime = LocalDateTime.of(2017, DECEMBER, 19, 18, 8, 0);
        int expectedDelay = 60;
        TrainDeparture expectedTrainDeparture = aTrainDeparture(expectedTime, expectedDelay, true, VEHICLE, PLATFORM, false);
        assertThat(trainDepartures.get(0), is(expectedTrainDeparture));
    }

    @Test
    public void translateRealLiveBoard() throws IOException {
        InputStream resourceAsStream = this.getClass().getResourceAsStream("liveboard.json");
        ObjectMapper objectMapper = new ObjectMapper();
        LiveBoard liveBoard = objectMapper.readValue(resourceAsStream, LiveBoard.class);
        Board board = nmbsTranslator.translateFrom(liveBoard);

        assertThat(board.getStationId(), is(aStationId("BE.NMBS.008814001")));
        assertThat(board.getTime(), is(LocalDateTime.of(2017, DECEMBER, 22, 17, 38, 46)));
        assertThat(board.getDepartures().size(), is(50));

        LocalDateTime expectedTime = LocalDateTime.of(2017, DECEMBER, 22, 17, 25, 0);
        TrainDeparture trainDeparture = aTrainDeparture(expectedTime, 5160, false, "BE.NMBS.THA9461", "3", false);
        assertThat(board.getDepartures().get(0), is(trainDeparture));

        LocalDateTime expectedTime2 = LocalDateTime.of(2017, DECEMBER, 22, 17, 31, 0);
        TrainDeparture trainDeparture2 = aTrainDeparture(expectedTime2, 420, false, "BE.NMBS.IC3438", "20", false);
        assertThat(board.getDepartures().get(1), is(trainDeparture2));

        LocalDateTime expectedTime3 = LocalDateTime.of(2017, DECEMBER, 22, 17, 34, 0);
        TrainDeparture trainDeparture3 = aTrainDeparture(expectedTime3, 240, false, "BE.NMBS.S23688", "17", false);
        assertThat(board.getDepartures().get(2), is(trainDeparture3));

        LocalDateTime expectedTime4 = LocalDateTime.of(2017, DECEMBER, 22, 17, 35, 0);
        TrainDeparture trainDeparture4 = aTrainDeparture(expectedTime4, 120, false, "BE.NMBS.IC438", "9", false);
        assertThat(board.getDepartures().get(3), is(trainDeparture4));

        LocalDateTime expectedTime5 = LocalDateTime.of(2017, DECEMBER, 22, 17, 38, 0);
        TrainDeparture trainDeparture5 = aTrainDeparture(expectedTime5, 720, false, "BE.NMBS.IC2340", "14", false);
        assertThat(board.getDepartures().get(4), is(trainDeparture5));


        LocalDateTime expectedTime6 = LocalDateTime.of(2017, DECEMBER, 22, 17, 38, 0);
        TrainDeparture trainDeparture6 = aTrainDeparture(expectedTime6, 180, false, "BE.NMBS.P8803", "18", true);
        assertThat(board.getDepartures().get(5), is(trainDeparture5));
    }
}