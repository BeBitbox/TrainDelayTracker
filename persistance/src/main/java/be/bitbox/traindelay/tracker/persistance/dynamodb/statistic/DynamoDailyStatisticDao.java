package be.bitbox.traindelay.tracker.persistance.dynamodb.statistic;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.IDynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.bitbox.traindelay.tracker.core.statistic.DailyStatistic;
import be.bitbox.traindelay.tracker.core.statistic.DailyStatisticDao;
import static java.util.stream.Collectors.toList;

public class DynamoDailyStatisticDao implements DailyStatisticDao {

    private final IDynamoDBMapper dynamoDBMapper;

    public DynamoDailyStatisticDao(IDynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    @Override
    public List<DailyStatistic> getDayStatistic(LocalDate from, LocalDate to) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":id", new AttributeValue().withS("*"));
        eav.put(":from", new AttributeValue().withS(from.toString()));
        eav.put(":to", new AttributeValue().withS(to.toString()));

        var queryExpression = new DynamoDBQueryExpression<DynamoDailyStatistic>()
                .withKeyConditionExpression("station = :id and local_date between :from and :to")
                .withExpressionAttributeValues(eav);
        var query = dynamoDBMapper.query(DynamoDailyStatistic.class, queryExpression);
        if (query == null) {
            return Lists.newArrayList();
        } else {
            return query.stream()
                    .map(DynamoDailyStatistic::toDailyStatistic)
                    .collect(toList());
        }
    }

    @Override
    public DailyStatistic getDayStatistic(LocalDate date) {
        var dailyStatistic = dynamoDBMapper.load(DynamoDailyStatistic.class, "*", date.toString());
        if (dailyStatistic == null) {
            return null;
        }
        return dailyStatistic.toDailyStatistic();
    }

    @Override
    public void save(DailyStatistic dailyStatistic) {
        dynamoDBMapper.save(new DynamoDailyStatistic(dailyStatistic));
    }
}
