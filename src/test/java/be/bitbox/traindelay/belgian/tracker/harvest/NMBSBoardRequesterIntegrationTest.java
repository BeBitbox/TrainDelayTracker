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
package be.bitbox.traindelay.belgian.tracker.harvest;

import be.bitbox.traindelay.belgian.tracker.Board;
import be.bitbox.traindelay.belgian.tracker.nmbs.NMBSBoardRequester;
import be.bitbox.traindelay.belgian.tracker.station.StationId;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Test;

import static be.bitbox.traindelay.belgian.tracker.station.StationId.aStationId;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class NMBSBoardRequesterIntegrationTest {

    @Test
    public void retrieveBoard_fakeUrl() throws Exception {
        NMBSBoardRequester requestManager = new NMBSBoardRequester("https://api.irail.be/liveboard/?id=");
        StationId expectedStationID = aStationId("BE.NMBS.008892106");
        Board board = requestManager.requestBoard(expectedStationID);
//        assertThat();
    }

    @Test
    public void retrieveBoard_existingUrl() {
        NMBSBoardRequester requestManager = new NMBSBoardRequester("https://api.irail.be/liveboard/?id=");
        StationId expectedStationID = aStationId("BE.NMBS.008892106");
        Board board = requestManager.requestBoard(expectedStationID);
        assertThat(board.getStationId(), is(expectedStationID));
        assertTrue(board.getDepartures().size() > 1);
    }
}