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
package be.bitbox.traindelay.tracker.persistance;

import be.bitbox.traindelay.tracker.core.TrainDepartureQuery;
import be.bitbox.traindelay.tracker.core.harvest.TrainDepartureEvent;
import be.bitbox.traindelay.tracker.core.station.StationId;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@Component
class DynamoTrainDepartureEventQuery implements TrainDepartureQuery {
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    DynamoTrainDepartureEventQuery(AmazonDynamoDB client) {
        dynamoDBMapper = new DynamoDBMapper(client);
    }

    @Override
    public List<TrainDepartureEvent> listTrainDepartureFor(StationId stationId, LocalDate date) {
        String dateAsString = DynamoTrainDepartureEvent.DATE_FORMATTER.format(date);
        return IntStream.range(0, 200)
                .parallel()
                .mapToObj(i -> {
                    String partitionKey = dateAsString + "." + i;
                    Map<String, AttributeValue> eav = new HashMap<>();
                    eav.put(":id", new AttributeValue().withS(partitionKey));
                    eav.put(":station", new AttributeValue().withS(stationId.getId()));

                    DynamoDBQueryExpression<DynamoTrainDepartureEvent> queryExpression = new DynamoDBQueryExpression<DynamoTrainDepartureEvent>()
                            .withKeyConditionExpression("id = :id")
                            .withFilterExpression("station = :station")
                            .withExpressionAttributeValues(eav);
                    return dynamoDBMapper.query(DynamoTrainDepartureEvent.class, queryExpression);
                })
                .flatMap(Collection::stream)
                .map(DynamoTrainDepartureEvent::asTrainDepartureEvent)
                .collect(toList());
    }
}