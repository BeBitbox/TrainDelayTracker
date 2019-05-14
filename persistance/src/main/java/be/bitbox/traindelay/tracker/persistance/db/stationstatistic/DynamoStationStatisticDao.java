package be.bitbox.traindelay.tracker.persistance.db.stationstatistic;

import be.bitbox.traindelay.tracker.core.station.StationId;
import be.bitbox.traindelay.tracker.core.stationstatistic.StationStatistic;
import be.bitbox.traindelay.tracker.core.stationstatistic.StationStatisticDao;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DynamoStationStatisticDao implements StationStatisticDao {

    private final DynamoDBMapper dynamoDBMapper;

    @Autowired
    DynamoStationStatisticDao(AmazonDynamoDB dynamoDB) {
        this.dynamoDBMapper = new DynamoDBMapper(dynamoDB);
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
}
