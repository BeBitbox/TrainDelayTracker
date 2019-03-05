package be.bitbox.traindelay.tracker.persistance;

import be.bitbox.traindelay.tracker.core.service.JsonTrainDeparture;
import be.bitbox.traindelay.tracker.core.station.StationId;
import be.bitbox.traindelay.tracker.core.traindeparture.TrainDepartureEvent;
import be.bitbox.traindelay.tracker.core.traindeparture.TrainDepartureRepository;
import be.bitbox.traindelay.tracker.persistance.db.traindepartures.DynamoDepartureEventQuery;
import be.bitbox.traindelay.tracker.persistance.messaging.traindepartures.ConsumeTrainDepartureSQS;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class TrainDepartureRepositoryImpl implements TrainDepartureRepository {
    private final DynamoDepartureEventQuery databaseTrainDepartures;
    private final ConsumeTrainDepartureSQS queueTrainDepartures;
    
    @Autowired
    public TrainDepartureRepositoryImpl(AmazonDynamoDB amazonDynamoDB, ConsumeTrainDepartureSQS consumeTrainDepartureSQS) {
        databaseTrainDepartures = new DynamoDepartureEventQuery(amazonDynamoDB);
        queueTrainDepartures = consumeTrainDepartureSQS;
    }

    @Override 
    public List<TrainDepartureEvent> listTrainDepartureFor(StationId stationId, LocalDate date) {
        return databaseTrainDepartures.listTrainDepartureFor(stationId, date);
    }

    @Override
    public List<JsonTrainDeparture> listRecentTrainDepartures() {
        return queueTrainDepartures.getRecentTrainDepartures();
    }
}
