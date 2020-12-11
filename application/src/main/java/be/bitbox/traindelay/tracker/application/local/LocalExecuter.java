package be.bitbox.traindelay.tracker.application.local;

import be.bitbox.traindelay.tracker.persistance.db.board.DynamoBoard;
import be.bitbox.traindelay.tracker.persistance.db.locking.DynamoLocking;
import be.bitbox.traindelay.tracker.persistance.db.statistic.DynamoDailyStatistic;
import be.bitbox.traindelay.tracker.persistance.db.statistic.DynamoStationStatistic;
import be.bitbox.traindelay.tracker.persistance.db.traindepartures.DynamoDepartureEvent;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperTableModel;
import com.amazonaws.services.dynamodbv2.datamodeling.IDynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
public class LocalExecuter implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalExecuter.class);
    private final IDynamoDBMapper dynamoDBMapper;
    private final AmazonDynamoDB amazonDynamoDB;

    public LocalExecuter(IDynamoDBMapper dynamoDBMapper, AmazonDynamoDB amazonDynamoDB) {
        this.dynamoDBMapper = dynamoDBMapper;
        this.amazonDynamoDB = amazonDynamoDB;
    }

    @Override
    public void run(String... args) {
        checkIfTableExists(DynamoBoard.class);
        checkIfTableExists(DynamoLocking.class);
        checkIfTableExists(DynamoStationStatistic.class);
        checkIfTableExists(DynamoDailyStatistic.class);
        checkIfTableExists(DynamoDepartureEvent.class);
    }

    private void checkIfTableExists(Class t) {
        DynamoDBMapperTableModel<DynamoBoard> tableModel = dynamoDBMapper.getTableModel(t);
        if (tableModel == null) {
            CreateTableRequest createTableRequest = dynamoDBMapper.generateCreateTableRequest(t);
            CreateTableResult table = amazonDynamoDB.createTable(createTableRequest);
            LOGGER.info("Created " + t.getSimpleName() + ": " + table.getSdkHttpMetadata().getHttpStatusCode());
        } else {
            LOGGER.info("Found " + t.getSimpleName());
        }
    }
}
