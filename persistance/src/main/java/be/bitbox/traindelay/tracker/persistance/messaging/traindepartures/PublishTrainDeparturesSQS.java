/*
 * Copyright 2019 Bitbox : TrainDelayTracker
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
package be.bitbox.traindelay.tracker.persistance.messaging.traindepartures;

import be.bitbox.traindelay.tracker.core.service.JsonTrainDeparture;
import be.bitbox.traindelay.tracker.core.traindeparture.TrainDepartureEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;

@Component
class PublishTrainDeparturesSQS {
    private static final Logger LOGGER = LoggerFactory.getLogger(PublishTrainDeparturesSQS.class);
    private final AmazonSQS amazonSQS;
    private final String queueUrl;
    private final RecentTrainDepartures recentTrainDepartures;

    @Autowired
    PublishTrainDeparturesSQS(AmazonSQS amazonSQS,
                              EventBus eventBus,
                              @Value("${traindeparture.queue.url}") String queueUrl,
                              RecentTrainDepartures recentTrainDepartures) {
        this.amazonSQS = amazonSQS;
        this.queueUrl = queueUrl;
        this.recentTrainDepartures = recentTrainDepartures;
        eventBus.register(this);
    }

    @Subscribe
    void subscribeDepartureEvent(TrainDepartureEvent trainDepartureEvent) {
        var objectMapper = new ObjectMapper();
        var stringWriter = new StringWriter();
        var jsonTrainDeparture = new JsonTrainDeparture(trainDepartureEvent);

        recentTrainDepartures.addTrainDeparture(jsonTrainDeparture);
        try {
            objectMapper.writeValue(stringWriter, jsonTrainDeparture);
        } catch (IOException e) {
            LOGGER.error("Failed to transform TraindepartureEvent to JSON.", e);
        }
        SendMessageRequest request = new SendMessageRequest(queueUrl, stringWriter.toString());
        amazonSQS.sendMessage(request);
    }
}