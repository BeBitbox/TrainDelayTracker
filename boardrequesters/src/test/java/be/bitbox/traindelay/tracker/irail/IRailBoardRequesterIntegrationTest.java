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
package be.bitbox.traindelay.tracker.irail;

import be.bitbox.traindelay.tracker.core.board.Board;
import be.bitbox.traindelay.tracker.core.board.BoardRequestException;
import be.bitbox.traindelay.tracker.core.board.BoardRequester;
import be.bitbox.traindelay.tracker.core.station.Country;
import be.bitbox.traindelay.tracker.core.station.Station;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static be.bitbox.traindelay.tracker.core.station.Station.aStation;
import static be.bitbox.traindelay.tracker.core.station.StationId.aStationId;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class IRailBoardRequesterIntegrationTest {

    @Test (expected = BoardRequestException.class)
    public void retrieveBoard_fakeUrl() {
        BoardRequester requestManager = new IRailBoardRequester("https://fake/?id=");
        Station station = aStation(aStationId("BE.NMBS.008892106"), "fake", Country.BE);
        requestManager.requestBoardFor(station);
    }

    @Test
    public void retrieveBoard_existingUrl() {
        BoardRequester requestManager = new IRailBoardRequester("https://api.irail.be/liveboard/?id=");
        Station station = aStation(aStationId("BE.NMBS.008892106"), "fake", Country.BE);
        Board board = requestManager.requestBoardFor(station);
        assertThat(board.getStationId(), CoreMatchers.is(station.stationId()));
        assertTrue(board.getDepartures().size() > 1);
    }
}