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
import be.bitbox.traindelay.belgian.tracker.nmbs.StationRetriever;
import be.bitbox.traindelay.belgian.tracker.station.Country;
import be.bitbox.traindelay.belgian.tracker.station.Station;
import be.bitbox.traindelay.belgian.tracker.station.StationId;
import com.google.common.eventbus.EventBus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;

import static be.bitbox.traindelay.belgian.tracker.Board.aBoardForStation;
import static be.bitbox.traindelay.belgian.tracker.TrainDeparture.aTrainDeparture;
import static be.bitbox.traindelay.belgian.tracker.harvest.TrainDepartureEventBuilder.aTrainDepartureEvent;
import static be.bitbox.traindelay.belgian.tracker.station.Station.aStation;
import static be.bitbox.traindelay.belgian.tracker.station.StationId.aStationId;
import static java.time.LocalDateTime.now;
import static java.time.LocalDateTime.of;
import static java.time.Month.JANUARY;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BoardHarvesterTest {

    @Mock
    private NMBSBoardRequester nmbsBoardRequester;

    @Mock
    private StationRetriever stationRetriever;

    @Mock
    private EventBus eventBus;

    @Test
    public void checkBoardForEveryTrain_FirstHarvest() {
        StationId id1 = aStationId("id1");
        Station station = aStation(id1, "name", Country.BE);
        StationId id2 = aStationId("id2");
        Station station2 = aStation(id2, "name2", Country.BE);
        when(stationRetriever.getBelgianStations()).thenReturn(asList(station, station2));

        BoardHarvester boardHarvester = new BoardHarvester(nmbsBoardRequester, stationRetriever, eventBus);
        boardHarvester.harvest();

        verify(nmbsBoardRequester, atLeastOnce()).requestBoard(id1);
        verify(nmbsBoardRequester, atLeastOnce()).requestBoard(id2);
        verify(eventBus, never()).post(any());
    }

    @Test
    public void checkBoardForEveryTrain_NoChangesInBoard_MultipleHarvest() {
        StationId id = aStationId("id");
        Station station = aStation(id, "name", Country.BE);
        when(nmbsBoardRequester.requestBoard(id)).thenReturn(aBoardForStation(id, now()));
        when(stationRetriever.getBelgianStations()).thenReturn(singletonList(station));

        BoardHarvester boardHarvester = new BoardHarvester(nmbsBoardRequester, stationRetriever, eventBus);
        boardHarvester.harvest();
        boardHarvester.harvest();
        boardHarvester.harvest();
        boardHarvester.harvest();

        verify(nmbsBoardRequester, times(4)).requestBoard(id);
        verify(eventBus, never()).post(any());
    }

    @Test
    public void checkBoardForOneTrain_OneTrainDeparted_TwoHarvest() {
        StationId id = aStationId("id");
        Station station = aStation(id, "name", Country.BE);
        when(stationRetriever.getBelgianStations()).thenReturn(singletonList(station));

        BoardHarvester boardHarvester = new BoardHarvester(nmbsBoardRequester, stationRetriever, eventBus);

        Board board = aBoardForStation(id, now());
        LocalDateTime trainLeavingTime = of(2018, JANUARY, 12, 5, 45, 0);
        int delay = 5;
        boolean canceled = false;
        String vehicule = "MyTrain";
        String platform = "C";
        boolean platformChange = false;
        board.addDeparture(aTrainDeparture(trainLeavingTime, delay, canceled, vehicule, platform, platformChange));

        when(nmbsBoardRequester.requestBoard(id)).thenReturn(board);
        boardHarvester.harvest();
        LocalDateTime boardTime = now();
        when(nmbsBoardRequester.requestBoard(id)).thenReturn(aBoardForStation(id, boardTime));
        boardHarvester.harvest();

        verify(nmbsBoardRequester, times(2)).requestBoard(id);
        TrainDepartureEventBuilder eventBuilder = aTrainDepartureEvent()
                .withEventCreationTime(boardTime)
                .withStationId(id)
                .withDelay(delay)
                .withCanceled(canceled)
                .withExpectedDepartureTime(trainLeavingTime)
                .withPlatform(platform)
                .withPlatformChange(platformChange)
                .withVehicule(vehicule);
        verify(eventBus, only()).post(eventBuilder.build());
    }

    @Test
    public void checkBoardForOneTrain_OneFutureTrainRemovedFromBoard_TwoHarvest() {
        StationId id = aStationId("id");
        Station station = aStation(id, "name", Country.BE);
        when(stationRetriever.getBelgianStations()).thenReturn(singletonList(station));

        BoardHarvester boardHarvester = new BoardHarvester(nmbsBoardRequester, stationRetriever, eventBus);

        LocalDateTime now = now();
        Board board = aBoardForStation(id, now);
        LocalDateTime trainLeavingTime = now.plusHours(4);
        int delay = 5;
        boolean canceled = false;
        String vehicule = "MyTrain";
        String platform = "C";
        boolean platformChange = false;
        board.addDeparture(aTrainDeparture(trainLeavingTime, delay, canceled, vehicule, platform, platformChange));

        when(nmbsBoardRequester.requestBoard(id)).thenReturn(board);
        boardHarvester.harvest();
        LocalDateTime boardTime = now();
        when(nmbsBoardRequester.requestBoard(id)).thenReturn(aBoardForStation(id, boardTime));
        boardHarvester.harvest();

        verify(nmbsBoardRequester, times(2)).requestBoard(id);
        verify(eventBus, never()).post(any());
    }

    @Test
    public void checkBoardForThreeTrain_TwoTrainDeparted_FourHarvest() {
        StationId id1 = aStationId("id1");
        Station station1 = aStation(id1, "name1", Country.BE);
        StationId id2 = aStationId("id2");
        Station station2 = aStation(id2, "name2", Country.BE);
        StationId id3 = aStationId("id3");
        Station station3 = aStation(id3, "name3", Country.BE);
        when(stationRetriever.getBelgianStations()).thenReturn(asList(station1, station2, station3));

        BoardHarvester boardHarvester = new BoardHarvester(nmbsBoardRequester, stationRetriever, eventBus);

        // Tick1:  station1 : 0 train, station2 : 2 train, station3 : 1 train
        Board board_1_1 = aBoardForStation(id1, now());
        Board board_1_2 = aBoardForStation(id2, now());
        LocalDateTime trainLeavingTime_1_2_1 = of(2018, JANUARY, 12, 5, 46, 0);
        board_1_2.addDeparture(aTrainDeparture(trainLeavingTime_1_2_1, 5, false, "MyTrain1", "C", false));
        LocalDateTime trainLeavingTime_1_2_2 = of(2018, JANUARY, 12, 5, 46, 0);
        board_1_2.addDeparture(aTrainDeparture(trainLeavingTime_1_2_2, 0, true, "MyTrain2", "7", true));
        Board board_1_3 = aBoardForStation(id3, now());
        LocalDateTime trainLeavingTime_1_2_3 = of(2018, JANUARY, 12, 5, 48, 0);
        board_1_3.addDeparture(aTrainDeparture(trainLeavingTime_1_2_3, 7, false, "MyTrain3", "1", false));

        //ASSERTIONS TICK 1
        when(nmbsBoardRequester.requestBoard(id1)).thenReturn(board_1_1);
        when(nmbsBoardRequester.requestBoard(id2)).thenReturn(board_1_2);
        when(nmbsBoardRequester.requestBoard(id3)).thenReturn(board_1_3);
        boardHarvester.harvest();
        verify(eventBus, never()).post(any());

        // Tick2:  station1 : 1 train, station2 : 0 train, station3 : 2 train
        Board board_2_1 = aBoardForStation(id1, now());
        LocalDateTime trainLeavingTime_2_1_1 = of(2018, JANUARY, 12, 6, 0, 0);
        board_2_1.addDeparture(aTrainDeparture(trainLeavingTime_2_1_1, 0, false, "MyTrain1", "E", false));
        LocalDateTime time_2_2 = now();
        Board board_2_2 = aBoardForStation(id2, time_2_2);
        Board board_2_3 = aBoardForStation(id3, now());
        LocalDateTime trainLeavingTime_2_3_1 = of(2018, JANUARY, 12, 5, 48, 0);
        board_2_3.addDeparture(aTrainDeparture(trainLeavingTime_2_3_1, 7, false, "MyTrain3", "1", false));
        LocalDateTime trainLeavingTime_2_3_2 = of(2018, JANUARY, 12, 5, 49, 0);
        board_2_3.addDeparture(aTrainDeparture(trainLeavingTime_2_3_2, 1, false, "MyTrain2", "4", false));

        //ASSERTIONS TICK 2
        when(nmbsBoardRequester.requestBoard(id1)).thenReturn(board_2_1);
        when(nmbsBoardRequester.requestBoard(id2)).thenReturn(board_2_2);
        when(nmbsBoardRequester.requestBoard(id3)).thenReturn(board_2_3);
        boardHarvester.harvest();
        TrainDepartureEvent event1_1 = aTrainDepartureEvent()
                .withEventCreationTime(time_2_2)
                .withStationId(id2)
                .withDelay(5)
                .withCanceled(false)
                .withExpectedDepartureTime(trainLeavingTime_1_2_1)
                .withPlatform("C")
                .withPlatformChange(false)
                .withVehicule("MyTrain1")
                .build();
        TrainDepartureEvent event1_2 = aTrainDepartureEvent()
                .withEventCreationTime(time_2_2)
                .withStationId(id2)
                .withDelay(0)
                .withCanceled(true)
                .withExpectedDepartureTime(trainLeavingTime_1_2_2)
                .withPlatform("7")
                .withPlatformChange(true)
                .withVehicule("MyTrain2")
                .build();
        verify(eventBus, times(1)).post(event1_1);
        verify(eventBus, times(1)).post(event1_2);

        // Tick3 : same as tick2 :  station1 : 1 train, station2 : 0 train, station3 : 2 train
        reset(eventBus);
        Board board_3_1 = aBoardForStation(id1, now());
        LocalDateTime trainLeavingTime_3_1_1 = of(2018, JANUARY, 12, 6, 0, 0);
        board_3_1.addDeparture(aTrainDeparture(trainLeavingTime_3_1_1, 0, false, "MyTrain1", "E", false));
        LocalDateTime time_3_2 = now();
        Board board_3_2 = aBoardForStation(id2, time_3_2);
        Board board_3_3 = aBoardForStation(id3, now());
        LocalDateTime trainLeavingTime_3_3_1 = of(2018, JANUARY, 12, 5, 48, 0);
        board_3_3.addDeparture(aTrainDeparture(trainLeavingTime_3_3_1, 7, false, "MyTrain3", "1", false));
        LocalDateTime trainLeavingTime_3_3_2 = of(2018, JANUARY, 12, 5, 49, 0);
        board_3_3.addDeparture(aTrainDeparture(trainLeavingTime_3_3_2, 1, false, "MyTrain2", "4", false));

        //ASSERTIONS TICK 3
        when(nmbsBoardRequester.requestBoard(id1)).thenReturn(board_3_1);
        when(nmbsBoardRequester.requestBoard(id2)).thenReturn(board_3_2);
        when(nmbsBoardRequester.requestBoard(id3)).thenReturn(board_3_3);
        boardHarvester.harvest();
        verify(eventBus, never()).post(any());

        // Tick4:  station1 : 1 train, station2 : 1 train, station3 : 1 train (added delay)
        reset(eventBus);
        Board board_4_1 = aBoardForStation(id1, now());
        LocalDateTime trainLeavingTime_4_1_1 = of(2018, JANUARY, 12, 6, 0, 0);
        board_4_1.addDeparture(aTrainDeparture(trainLeavingTime_4_1_1, 0, false, "MyTrain1", "E", false));
        Board board_4_2 = aBoardForStation(id2, now());
        LocalDateTime trainLeavingTime_4_2_1 = of(2018, JANUARY, 12, 6, 11, 0);
        board_4_2.addDeparture(aTrainDeparture(trainLeavingTime_4_2_1, 7, false, "MyTrain3", "10", false));
        LocalDateTime time_4_3 = now();
        Board board_4_3 = aBoardForStation(id3, time_4_3);
        LocalDateTime trainLeavingTime_4_3_1 = of(2018, JANUARY, 12, 5, 49, 0);
        board_4_3.addDeparture(aTrainDeparture(trainLeavingTime_4_3_1, 10, false, "MyTrain2", "4", false));

        //ASSERTIONS TICK 4
        when(nmbsBoardRequester.requestBoard(id1)).thenReturn(board_4_1);
        when(nmbsBoardRequester.requestBoard(id2)).thenReturn(board_4_2);
        when(nmbsBoardRequester.requestBoard(id3)).thenReturn(board_4_3);
        boardHarvester.harvest();
        TrainDepartureEvent event4_3 = aTrainDepartureEvent()
                .withEventCreationTime(time_4_3)
                .withStationId(id3)
                .withDelay(7)
                .withCanceled(false)
                .withExpectedDepartureTime(trainLeavingTime_3_3_1)
                .withPlatform("1")
                .withPlatformChange(false)
                .withVehicule("MyTrain3")
                .build();
        verify(eventBus, only()).post(event4_3);
    }
}