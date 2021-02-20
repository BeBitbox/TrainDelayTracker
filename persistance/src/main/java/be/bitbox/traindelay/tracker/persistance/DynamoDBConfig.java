package be.bitbox.traindelay.tracker.persistance;

import be.bitbox.traindelay.tracker.core.LockingDao;
import be.bitbox.traindelay.tracker.core.board.BoardDao;
import be.bitbox.traindelay.tracker.core.statistic.DailyStatisticDao;
import be.bitbox.traindelay.tracker.core.statistic.StationStatisticDao;
import be.bitbox.traindelay.tracker.persistance.dynamodb.board.DynamoBoardDao;
import be.bitbox.traindelay.tracker.persistance.dynamodb.locking.DynamoLockingDao;
import be.bitbox.traindelay.tracker.persistance.dynamodb.statistic.DynamoDailyStatisticDao;
import be.bitbox.traindelay.tracker.persistance.dynamodb.statistic.DynamoStationStatisticDao;
import be.bitbox.traindelay.tracker.persistance.dynamodb.traindepartures.DepartureEventToDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.IDynamoDBMapper;
import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!local")
public class DynamoDBConfig implements Config {
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamoDBConfig.class);
    private final IDynamoDBMapper dynamoDBMapper;
    private final EventBus eventBus;

    @Autowired
    public DynamoDBConfig(IDynamoDBMapper dynamoDBMapper, EventBus eventBus) {
        LOGGER.info("Loading DynamoDB persistance config");
        this.dynamoDBMapper = dynamoDBMapper;
        this.eventBus = eventBus;
    }

    @Bean
    public DepartureEventToDynamoDB getDepartureEventToDynamoDB() {
        return new DepartureEventToDynamoDB(dynamoDBMapper, eventBus);
    }

    @Bean
    @Override
    public DailyStatisticDao getDailyStatisticDao() {
        return new DynamoDailyStatisticDao(dynamoDBMapper);
    }

    @Bean
    @Override
    public StationStatisticDao getStationStatisticDao() {
        return new DynamoStationStatisticDao(dynamoDBMapper);
    }

    @Bean
    @Override
    public LockingDao getLockingDao() {
        return new DynamoLockingDao(dynamoDBMapper);
    }

    @Bean
    @Override
    public BoardDao getBoardDao() {
        return new DynamoBoardDao(dynamoDBMapper);
    }
}
