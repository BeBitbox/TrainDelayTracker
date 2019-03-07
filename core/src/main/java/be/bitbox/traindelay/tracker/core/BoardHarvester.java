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
package be.bitbox.traindelay.tracker.core;

import be.bitbox.traindelay.tracker.core.traindeparture.TrainDeparture;
import be.bitbox.traindelay.tracker.core.board.*;
import be.bitbox.traindelay.tracker.core.station.Station;
import be.bitbox.traindelay.tracker.core.station.StationAvailabilityMonitor;
import be.bitbox.traindelay.tracker.core.station.StationId;
import be.bitbox.traindelay.tracker.core.traindeparture.TrainDepartureEvent;
import be.bitbox.traindelay.tracker.core.traindeparture.TrainDepartureRepository;
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

import static java.util.stream.Collectors.toList;

@Component
@EnableScheduling
class BoardHarvester {
    private final static Logger LOGGER = LoggerFactory.getLogger(BoardHarvester.class);
    private final BoardRequester boardRequester;
    private final StationAvailabilityMonitor stationAvailabilityMonitor;
    private final EventBus eventBus;
    private final Map<StationId, Board> lastBoards;
    private final BoardDao boardDao;
    private final LockingDao lockingDao;
    private final TrainDepartureRepository trainDepartureRepository;

    @Autowired
    BoardHarvester(BoardRequester boardRequester,
                   StationAvailabilityMonitor stationAvailabilityMonitor,
                   EventBus eventBus,
                   BoardDao boardDao,
                   LockingDao lockingDao,
                   TrainDepartureRepository trainDepartureRepository) {
        this.boardRequester = boardRequester;
        this.stationAvailabilityMonitor = stationAvailabilityMonitor;
        this.eventBus = eventBus;
        this.boardDao = boardDao;
        this.lockingDao = lockingDao;
        this.trainDepartureRepository = trainDepartureRepository;
        lastBoards = new HashMap<>();
    }
    
    @Scheduled(fixedDelay = 10000L)
    void lockAndHarvest() {
        if (lockingDao.obtainedLock()) {
            harvest();
        } else {
            LOGGER.info("Lock not obtained");
            trainDepartureRepository.updateLatestTrainDepartures();
        }
    }

    private void harvest() {
        List<Station> trainStations = stationAvailabilityMonitor.getTrainStations();
        LOGGER.info("Start Harvest for {} stations", trainStations.size());

        trainStations.forEach(station -> {
            try {
                Board board = boardRequester.requestBoardFor(station);

                Board lastBoard = getLastBoard(station);
                if (lastBoard != null) {
                    compareBoards(lastBoard, board);
                }

                boardDao.saveBoard(board);
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

    private Board getLastBoard(Station station) {
        Board lastBoard;
        if (lastBoards.containsKey(station.stationId())) {
            lastBoard = lastBoards.get(station.stationId());
        } else {
            lastBoard = boardDao.getLastBoardFor(station.stationId());
        }
        return lastBoard;
    }

    private void compareBoards(Board oldBoard, Board newBoard) {
        var trainDepartures = oldBoard.getDepartures()
                .stream()
                .filter(trainDeparture -> !newBoard.getDepartures().contains(trainDeparture))
                .filter(trainDeparture -> trainDeparture.getTime().isBefore(newBoard.getTime().plusHours(1)))
                .collect(toList());
        spawnEventsForNewTrainDepartures(newBoard, trainDepartures);
    }

    private void spawnEventsForNewTrainDepartures(Board newBoard, List<TrainDeparture> trainDepartures) {
        trainDepartures.forEach(trainDeparture -> {
            TrainDepartureEvent event = TrainDepartureEvent.Builder.createTrainDepartureEvent()
                    .withEventCreationTime(newBoard.getTime())
                    .withStationId(newBoard.getStationId())
                    .withExpectedDepartureTime(trainDeparture.getTime())
                    .withDelay(trainDeparture.getDelay())
                    .withCanceled(trainDeparture.isCanceled())
                    .withVehicle(trainDeparture.getVehicle())
                    .withPlatform(trainDeparture.getPlatform())
                    .withPlatformChange(trainDeparture.isPlatformChange())
                    .build();
            LOGGER.debug("Posting event {}", event);
            eventBus.post(event);
        });
    }
}
