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
import be.bitbox.traindelay.belgian.tracker.BoardNotFoundException;
import be.bitbox.traindelay.belgian.tracker.BoardRequestException;
import be.bitbox.traindelay.belgian.tracker.TrainDeparture;
import be.bitbox.traindelay.belgian.tracker.nmbs.NMBSBoardRequester;
import be.bitbox.traindelay.belgian.tracker.nmbs.StationAvailabilityMonitor;
import be.bitbox.traindelay.belgian.tracker.station.Station;
import be.bitbox.traindelay.belgian.tracker.station.StationId;
import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static be.bitbox.traindelay.belgian.tracker.harvest.TrainDepartureEventBuilder.aTrainDepartureEvent;
import static java.util.stream.Collectors.toList;

@Component
@EnableScheduling
class BoardHarvester {
    private final static Logger LOGGER = LoggerFactory.getLogger(BoardHarvester.class);
    private final NMBSBoardRequester nmbsBoardRequester;
    private final StationAvailabilityMonitor stationAvailabilityMonitor;
    private final EventBus eventBus;
    private final Map<StationId, Board> lastBoards;

    @Autowired
    BoardHarvester(NMBSBoardRequester nmbsBoardRequester,
                   StationAvailabilityMonitor stationAvailabilityMonitor,
                   EventBus eventBus) {
        this.nmbsBoardRequester = nmbsBoardRequester;
        this.stationAvailabilityMonitor = stationAvailabilityMonitor;
        this.eventBus = eventBus;
        lastBoards = new HashMap<>();
    }

    @Scheduled(fixedDelay = 10000L)
    void harvest() {
        List<Station> trainStations = stationAvailabilityMonitor.getTrainStations();
        LOGGER.info("Start Harvest for {} stations", trainStations.size());

        trainStations
                .stream()
                .parallel()
                .forEach(station -> {
                    try {
                        Board board = nmbsBoardRequester.requestBoard(station.stationId());
                        if (lastBoards.containsKey(station.stationId())) {
                            compareBoards(lastBoards.get(station.stationId()), board);
                        }
                        lastBoards.put(station.stationId(), board);
                        stationAvailabilityMonitor.positiveFeedbackFor(station);
                    } catch (BoardNotFoundException ex) {
                        stationAvailabilityMonitor.negativeFeedbackFor(station);
                        LOGGER.warn(ex.getMessage());
                    } catch (BoardRequestException ex) {
                        stationAvailabilityMonitor.negativeFeedbackFor(station);
                        LOGGER.error("Message occured during request", ex);
                    }
                });
        LOGGER.info("Stop Harvest");
    }

    private void compareBoards(Board oldBoard, Board newBoard) {
        List<TrainDeparture> trainDepartures = oldBoard.getDepartures()
                .stream()
                .filter(trainDeparture -> !newBoard.getDepartures().contains(trainDeparture))
                .filter(trainDeparture -> trainDeparture.getTime().isBefore(newBoard.getTime().plusHours(1)))
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
            LOGGER.debug("Posting event {}", event);
            eventBus.post(event);
        });
    }
}
