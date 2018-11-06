/*
 * Copyright 2018 Bitbox : TrainDelayTracker
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
import be.bitbox.traindelay.tracker.core.board.BoardRequester;
import be.bitbox.traindelay.tracker.core.station.Station;
import be.bitbox.traindelay.tracker.nmbs.response.Response;
import be.bitbox.traindelay.tracker.nmbs.response.ResponseToBoardTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class NMBSBoardRequester implements BoardRequester {

    private final String nmbsBaseUrl;
    private final ResponseToBoardTranslator translator;

    @Autowired
    public NMBSBoardRequester(@Value("${nmbs.base.url}") String nmbsBaseUrl) {
        this.nmbsBaseUrl = nmbsBaseUrl;
        this.translator = ResponseToBoardTranslator.INSTANCE;
    }

    @Override
    public Board requestBoardFor(Station station) {
        ResponseEntity<Response> responseEntity = new NMBSRequestCommand(station, nmbsBaseUrl).execute();
        return translator.translateFrom(responseEntity.getBody());
    }
}