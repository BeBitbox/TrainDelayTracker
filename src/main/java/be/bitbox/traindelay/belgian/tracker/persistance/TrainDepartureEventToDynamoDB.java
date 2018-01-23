/*
 * Copyright 2018 Bitbox : BelgianTrainDelayTracker
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
package be.bitbox.traindelay.belgian.tracker.persistance;

import be.bitbox.traindelay.belgian.tracker.harvest.TrainDepartureEvent;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrainDepartureEventToDynamoDB {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainDepartureEventToDynamoDB.class);
    private final DynamoDBMapper dynamoDBMapper;

    public TrainDepartureEventToDynamoDB(AmazonDynamoDB client) {
        dynamoDBMapper = new DynamoDBMapper(client);
    }

    @Subscribe
    void subscribeTrainDepartureEvent(TrainDepartureEvent trainDepartureEvent) {
        try {
            DynamoTrainDepartureEvent dynamoItem = new DynamoTrainDepartureEvent(trainDepartureEvent);
            dynamoDBMapper.save(dynamoItem);
        }
        catch (Exception e) {
            LOGGER.error("Failed to save event " + trainDepartureEvent, e);
        }
    }
}