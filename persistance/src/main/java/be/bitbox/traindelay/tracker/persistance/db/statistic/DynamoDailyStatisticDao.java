package be.bitbox.traindelay.tracker.persistance.db.statistic;

import be.bitbox.traindelay.tracker.core.statistic.DailyStatistic;
import be.bitbox.traindelay.tracker.core.statistic.DailyStatisticDao;
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
class DynamoDailyStatisticDao implements DailyStatisticDao {

    private final DynamoDBMapper dynamoDBMapper;

    @Autowired
    DynamoDailyStatisticDao(AmazonDynamoDB dynamoDB) {
        this.dynamoDBMapper = new DynamoDBMapper(dynamoDB);
    }

    @Override
    public List<DailyStatistic> getDayStatistic(LocalDate from, LocalDate to) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":from", new AttributeValue().withS(from.toString()));
        eav.put(":to", new AttributeValue().withS(to.toString()));

        var queryExpression = new DynamoDBQueryExpression<DynamoDailyStatistic>()
                .withKeyConditionExpression("local_date between :from and :to")
                .withExpressionAttributeValues(eav);
        return dynamoDBMapper
                .query(DynamoDailyStatistic.class, queryExpression)
                .stream()
                .map(DynamoDailyStatistic::toDailyStatistic)
                .collect(toList());
    }

    @Override
    public DailyStatistic getDayStatistic(LocalDate date) {
        var dailyStatistic = dynamoDBMapper.load(DynamoDailyStatistic.class, date.toString());
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
