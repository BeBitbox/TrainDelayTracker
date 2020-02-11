package be.bitbox.traindelay.tracker.persistance.db.statistic;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.IDynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.bitbox.traindelay.tracker.core.station.StationId;
import be.bitbox.traindelay.tracker.core.statistic.StationStatistic;
import be.bitbox.traindelay.tracker.core.statistic.StationStatisticDao;
import be.bitbox.traindelay.tracker.core.statistic.YearlyStationStatistic;
import static be.bitbox.traindelay.tracker.core.statistic.YearlyStationStatistic.yearlyStationStatisticFrom;
import static java.util.stream.Collectors.toList;

@Component
class DynamoStationStatisticDao implements StationStatisticDao {

    private final IDynamoDBMapper dynamoDBMapper;

    @Autowired
    DynamoStationStatisticDao(IDynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }


    @Override
    public StationStatistic getStationStatistic(StationId stationId, LocalDate date) {
        var dynamoStationStatistic = dynamoDBMapper.load(DynamoStationStatistic.class, stationId.getId(), date.toString());
        if (dynamoStationStatistic == null) {
            return null;
        }
        return dynamoStationStatistic.toStationStatistic();
    }

    @Override
    public void save(StationStatistic stationStatistic) {
        dynamoDBMapper.save(new DynamoStationStatistic(stationStatistic));
    }

    @Override
    public void saveYearly(StationStatistic stationStatistic) {
        dynamoDBMapper.save(new DynamoYearlyStationStatistic(stationStatistic));
    }

    @Override
    public List<StationStatistic> getStationStatistic(StationId stationId, LocalDate from, LocalDate to) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":id", new AttributeValue().withS(stationId.getId()));
        eav.put(":from", new AttributeValue().withS(from.toString()));
        eav.put(":to", new AttributeValue().withS(to.toString()));

        var queryExpression = new DynamoDBQueryExpression<DynamoStationStatistic>()
                .withKeyConditionExpression("station = :id and local_date between :from and :to")
                .withExpressionAttributeValues(eav);
        return dynamoDBMapper
                .query(DynamoStationStatistic.class, queryExpression)
                .stream()
                .map(DynamoStationStatistic::toStationStatistic)
                .collect(toList());
    }

    @Override
    public YearlyStationStatistic getYearlyStationStatistics() {
        return yearlyStationStatisticFrom(dynamoDBMapper
                .query(DynamoYearlyStationStatistic.class, new DynamoDBQueryExpression<>())
                .stream()
                .map(DynamoYearlyStationStatistic::toStationStatistic)
                .collect(toList()));
    }
}
