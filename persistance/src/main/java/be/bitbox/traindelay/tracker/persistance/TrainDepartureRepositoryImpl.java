package be.bitbox.traindelay.tracker.persistance;

import com.amazonaws.services.dynamodbv2.datamodeling.IDynamoDBMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

import be.bitbox.traindelay.tracker.core.service.JsonTrainDeparture;
import be.bitbox.traindelay.tracker.core.station.StationId;
import be.bitbox.traindelay.tracker.core.traindeparture.TrainDepartureEvent;
import be.bitbox.traindelay.tracker.core.traindeparture.TrainDepartureRepository;
import be.bitbox.traindelay.tracker.persistance.dynamodb.traindepartures.DynamoDepartureEventQuery;
import be.bitbox.traindelay.tracker.persistance.messaging.traindepartures.RecentTrainDepartures;

@Component
public class TrainDepartureRepositoryImpl implements TrainDepartureRepository {
    private final DynamoDepartureEventQuery databaseTrainDepartures;
    private final RecentTrainDepartures recentTrainDepartures;

    @Autowired
    public TrainDepartureRepositoryImpl(IDynamoDBMapper dynamoDBMapper, RecentTrainDepartures recentTrainDepartures) {
        databaseTrainDepartures = new DynamoDepartureEventQuery(dynamoDBMapper);
        this.recentTrainDepartures = recentTrainDepartures;
    }

    @Override 
    public List<TrainDepartureEvent> listTrainDepartureFor(StationId stationId, LocalDate date) {
        return databaseTrainDepartures.listTrainDepartureFor(stationId, date);
    }

    @Override
    public List<JsonTrainDeparture> listRecentTrainDepartures() {
        return recentTrainDepartures.list();
    }
}
