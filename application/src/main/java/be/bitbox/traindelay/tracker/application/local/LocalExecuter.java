package be.bitbox.traindelay.tracker.application.local;

import be.bitbox.traindelay.tracker.core.station.Country;
import be.bitbox.traindelay.tracker.core.station.StationRetriever;
import be.bitbox.traindelay.tracker.core.statistic.StationStatistic;
import be.bitbox.traindelay.tracker.persistance.db.board.DynamoBoard;
import be.bitbox.traindelay.tracker.persistance.db.locking.DynamoLocking;
import be.bitbox.traindelay.tracker.persistance.db.statistic.DynamoDailyStatistic;
import be.bitbox.traindelay.tracker.persistance.db.statistic.DynamoStationStatistic;
import be.bitbox.traindelay.tracker.persistance.db.traindepartures.DynamoDepartureEvent;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.IDynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Profile("local")
public class LocalExecuter implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalExecuter.class);
    private final IDynamoDBMapper dynamoDBMapper;
    private final AmazonDynamoDB amazonDynamoDB;
    private final StationRetriever stationRetriever;
    private final StationStatistic stationStatistic;

    public LocalExecuter(IDynamoDBMapper dynamoDBMapper, AmazonDynamoDB amazonDynamoDB, StationRetriever stationRetriever, StationStatistic stationStatistic) {
        this.dynamoDBMapper = dynamoDBMapper;
        this.amazonDynamoDB = amazonDynamoDB;
        this.stationRetriever = stationRetriever;
        this.stationStatistic = stationStatistic;
    }

    @Override
    public void run(String... args) {
        var tableNames = amazonDynamoDB.listTables().getTableNames();
        if (tableNames.isEmpty()) {
            createLocallyTables();
            insertMockData();
        } else {
            LOGGER.info("found tables {}", tableNames);
        }
    }

    private void insertMockData() {
        var stations = stationRetriever.getStationsFor(Country.BE);
        
        new DynamoStationStatistic(StationStatistic.StationStatisticBuilder.aStationStatistic()
                .withStationId(stations.get(0).stationId())
                .withAverageDelay(100)
                .withCancellations(5)
                .withDay(LocalDate.now())
                .withDelays(4)
                .withDepartures(10)
                .withPlatformChanges(4)
                .build());
    }

    private void createLocallyTables() {
        createTable(DynamoBoard.class);
        createTable(DynamoLocking.class);
        createTable(DynamoStationStatistic.class);
        createTable(DynamoDailyStatistic.class);
        createTable(DynamoDepartureEvent.class);
    }

    private void createTable(Class t) {
        CreateTableRequest createTableRequest = dynamoDBMapper.generateCreateTableRequest(t);
        createTableRequest.setProvisionedThroughput(new ProvisionedThroughput(5L, 5L));
        CreateTableResult table = amazonDynamoDB.createTable(createTableRequest);
        LOGGER.info("Created " + t.getSimpleName() + ": " + table.getSdkHttpMetadata().getHttpStatusCode());
    }
}
