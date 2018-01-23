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
import be.bitbox.traindelay.belgian.tracker.BoardRequestException;
import be.bitbox.traindelay.belgian.tracker.BoardRequester;
import be.bitbox.traindelay.belgian.tracker.station.StationId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class NMBSBoardRequester implements BoardRequester {

    private final String nmbsBaseUrl;
    private final NMBSTranslator translator;

    @Autowired
    public NMBSBoardRequester(@Value("${tracker.base.url}") String nmbsBaseUrl) {
        this.translator = new NMBSTranslator();
        this.nmbsBaseUrl = nmbsBaseUrl;
    }

    public Board requestBoard(StationId stationId) {
        RestTemplate restTemplate = new RestTemplate();
        String url = nmbsBaseUrl + stationId.getId() + "&format=json";
        try {
            LiveBoard liveBoard = restTemplate.getForObject(url, LiveBoard.class);
            return translator.translateFrom(liveBoard);
        } catch (Exception ex) {
            throw new BoardRequestException("Error during request " + url, ex);
        }
    }
}
