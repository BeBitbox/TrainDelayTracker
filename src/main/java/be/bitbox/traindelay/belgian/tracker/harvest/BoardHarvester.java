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
import be.bitbox.traindelay.belgian.tracker.TrainDeparture;
import be.bitbox.traindelay.belgian.tracker.nmbs.NMBSBoardRequester;
import be.bitbox.traindelay.belgian.tracker.station.Station;
import be.bitbox.traindelay.belgian.tracker.station.StationId;
import com.google.common.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static be.bitbox.traindelay.belgian.tracker.harvest.TrainDepartureEventBuilder.aTrainDepartureEvent;
import static java.util.stream.Collectors.toList;

public class BoardHarvester {

    private final NMBSBoardRequester nmbsBoardRequester;
    private final List<Station> stationList;
    private final EventBus eventBus;
    private final Map<StationId, Board> lastBoards;

    public BoardHarvester(NMBSBoardRequester nmbsBoardRequester, List<Station> stationList, EventBus eventBus) {
        this.nmbsBoardRequester = nmbsBoardRequester;
        this.stationList = stationList;
        this.eventBus = eventBus;
        lastBoards = new HashMap<>();
    }

    void harvest() {
        stationList.forEach(station -> {
            Board board = nmbsBoardRequester.requestBoard(station.stationId());
            if (lastBoards.containsKey(station.stationId())) {
                compareBoards(lastBoards.get(station.stationId()), board);
            }
            lastBoards.put(station.stationId(), board);
        });
    }

    private void compareBoards(Board oldBoard, Board newBoard) {
        List<TrainDeparture> trainDepartures = oldBoard.getDepartures()
                .stream()
                .filter(trainDeparture -> !newBoard.getDepartures().contains(trainDeparture))
                .collect(toList());
        spawnEventsForNewTrainDepartures(newBoard, trainDepartures);
    }

    private void spawnEventsForNewTrainDepartures(Board newBoard, List<TrainDeparture> trainDepartures) {
        trainDepartures.forEach(trainDeparture -> {
            TrainDepartureEvent event = aTrainDepartureEvent()
                    .withEventCreationTime(newBoard.getTime())
                    .withStationId(newBoard.getStationId())
                    .withExpectedDepartureTime(trainDeparture.getTime())
                    .withDelay(trainDeparture.getDelay())
                    .withCanceled(trainDeparture.isCanceled())
                    .withVehicule(trainDeparture.getVehicule())
                    .withPlatform(trainDeparture.getPlatform())
                    .withPlatformChange(trainDeparture.isPlatformChange())
                    .build();
            eventBus.post(event);
        });
    }
}
