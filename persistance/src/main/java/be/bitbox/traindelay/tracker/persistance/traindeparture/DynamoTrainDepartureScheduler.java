package be.bitbox.traindelay.tracker.persistance.traindeparture;

import be.bitbox.traindelay.tracker.core.LockingDao;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.google.common.eventbus.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
@EnableScheduling
public class DynamoTrainDepartureScheduler {
    private final DynamoDBMapper dynamoDBMapper;
    private final EventBus eventBus;
    private final LockingDao lockingDao;
    private static final LocalDate START_DATE = LocalDate.of(2018, Month.FEBRUARY, 23);

    @Autowired
    public DynamoTrainDepartureScheduler(AmazonDynamoDB amazonDynamoDB, EventBus eventBus, LockingDao lockingDao) {
        dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
        this.eventBus = eventBus;
        this.lockingDao = lockingDao;
    }

    @Scheduled(fixedDelay = 10000L)
    public void startrunning() throws InterruptedException {
        if (!lockingDao.obtainedLock()) {
            return;
        }
        for (int i = 0; i < 345; i++) {
            for (int j = 1; j <= 200; j++) {
                String value = START_DATE.plusDays(i).toString() + "." + j;
                System.out.println(value);
                Map<String, AttributeValue> eav = new HashMap<>();
                eav.put(":myId", new AttributeValue().withS(value));

                DynamoDBQueryExpression<DynamoTrainDepartureEvent> queryExpression = new DynamoDBQueryExpression<DynamoTrainDepartureEvent>()
                        .withKeyConditionExpression("id = :myId")
                        .withExpressionAttributeValues(eav);
                ArrayList<DynamoTrainDepartureEvent> events = new ArrayList<>(dynamoDBMapper.query(DynamoTrainDepartureEvent.class, queryExpression));

                events.stream()
                        .map(DynamoTrainDepartureEvent::asTrainDepartureEvent)
                        .forEach(eventBus::post);

                events.forEach(dynamoDBMapper::delete);
                Thread.sleep(1000);
            }
        }
    }
}
