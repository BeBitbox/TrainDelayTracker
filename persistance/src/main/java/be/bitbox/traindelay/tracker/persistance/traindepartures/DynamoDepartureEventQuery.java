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
package be.bitbox.traindelay.tracker.persistance.traindepartures;

import be.bitbox.traindelay.tracker.core.station.StationId;
import be.bitbox.traindelay.tracker.core.traindeparture.TrainDepartureEvent;
import be.bitbox.traindelay.tracker.core.traindeparture.TrainDepartureQuery;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Component
class DynamoDepartureEventQuery implements TrainDepartureQuery {
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    DynamoDepartureEventQuery(AmazonDynamoDB client) {
        dynamoDBMapper = new DynamoDBMapper(client);
    }

    @Override
    public List<TrainDepartureEvent> listTrainDepartureFor(StationId stationId, LocalDate date) {

        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":id", new AttributeValue().withS(stationId.getId()));
        eav.put(":date", new AttributeValue().withS(date.toString()));
        eav.put(":nextdate", new AttributeValue().withS(date.plusDays(1).toString()));

        DynamoDBQueryExpression<DynamoDepartureEvent> queryExpression = new DynamoDBQueryExpression<DynamoDepartureEvent>()
                .withKeyConditionExpression("station = :id and expected between :date and :nextdate")
                .withExpressionAttributeValues(eav);

        return dynamoDBMapper
                .query(DynamoDepartureEvent.class, queryExpression)
                .stream()
                .map(DynamoDepartureEvent::asTrainDepartureEvent)
                .collect(toList());
    }
}