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
import be.bitbox.traindelay.tracker.core.board.BoardRequester;
import be.bitbox.traindelay.tracker.core.station.Station;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static be.bitbox.traindelay.tracker.core.board.BoardNotFoundException.aBoardNotFoundException;
import static be.bitbox.traindelay.tracker.core.board.BoardRequestException.aBoardRequestException;

@Component
public class IRailBoardRequester implements BoardRequester {

    private final String iRailBaseUrl;
    private final IRailTranslator translator;

    @Autowired
    public IRailBoardRequester(@Value("${irail.base.url}") String iRailBaseUrl) {
        this.translator = IRailTranslator.INSTANCE;
        this.iRailBaseUrl = iRailBaseUrl;
    }

    @Override
    public Board requestBoardFor(Station station) {
        RestTemplate restTemplate = new RestTemplate();
        String stationId = station.stationId().getId();
        String url = iRailBaseUrl + stationId + "&format=json";
        try {
            LiveBoard liveBoard = restTemplate.getForObject(url, LiveBoard.class);
            return translator.translateFrom(liveBoard);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw aBoardNotFoundException("Board not found for " + stationId);
            } else {
                throw aBoardRequestException("HttpClientError during request " + url, ex);
            }
        } catch (Exception ex) {
            throw aBoardRequestException("Error during request " + url, ex);
        }
    }
}
