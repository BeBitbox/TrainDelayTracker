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
package be.bitbox.traindelay.tracker.nmbs;

import be.bitbox.traindelay.tracker.core.board.Board;
import be.bitbox.traindelay.tracker.core.board.BoardRequestException;
import be.bitbox.traindelay.tracker.core.board.BoardRequester;
import be.bitbox.traindelay.tracker.core.station.StationId;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class NMBSBoardRequesterIntegrationTest {

    @Test (expected = BoardRequestException.class)
    public void retrieveBoard_fakeUrl() throws Exception {
        BoardRequester requestManager = new NMBSBoardRequester("https://fake/?id=");
        StationId expectedStationID = StationId.aStationId("BE.NMBS.008892106");
        requestManager.requestBoard(expectedStationID);
    }

    @Test
    public void retrieveBoard_existingUrl() {
        BoardRequester requestManager = new NMBSBoardRequester("https://api.irail.be/liveboard/?id=");
        StationId expectedStationID = StationId.aStationId("BE.NMBS.008892106");
        Board board = requestManager.requestBoard(expectedStationID);
        assertThat(board.getStationId(), CoreMatchers.is(expectedStationID));
        assertTrue(board.getDepartures().size() > 1);
    }
}