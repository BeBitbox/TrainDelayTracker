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
package be.bitbox.traindelay.tracker.persistance.dynamodb.traindepartures;

import be.bitbox.traindelay.tracker.core.station.StationId;
import be.bitbox.traindelay.tracker.core.traindeparture.TrainDepartureEvent;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.IDynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class DynamoDepartureEventQuery {
    private final IDynamoDBMapper dynamoDBMapper;

    public DynamoDepartureEventQuery(IDynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    public List<TrainDepartureEvent> listTrainDepartureFor(StationId stationId, LocalDate date) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":id", new AttributeValue().withS(stationId.getId()));
        eav.put(":date", new AttributeValue().withS(date.toString()));
        eav.put(":nextdate", new AttributeValue().withS(date.plusDays(1).toString()));

        DynamoDBQueryExpression<DynamoDepartureEvent> queryExpression = new DynamoDBQueryExpression<DynamoDepartureEvent>()
                .withKeyConditionExpression("station = :id and expected between :date and :nextdate")
                .withExpressionAttributeValues(eav);
        var query = dynamoDBMapper.query(DynamoDepartureEvent.class, queryExpression);

        if (query == null || query.isEmpty()) {
            return List.of();
        } else {
            return query
                    .stream()
                    .map(DynamoDepartureEvent::asTrainDepartureEvent)
                    .collect(toList());
        }
    }
}