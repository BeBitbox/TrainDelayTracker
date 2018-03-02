package be.bitbox.traindelay.tracker.persistance;

import be.bitbox.traindelay.tracker.core.harvest.TrainDepartureEvent;
import be.bitbox.traindelay.tracker.core.harvest.TrainDepartureEventBuilder;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.google.common.eventbus.EventBus;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDateTime;

import static be.bitbox.traindelay.tracker.core.station.StationId.aStationId;
import static java.time.LocalDateTime.now;
import static java.time.LocalDateTime.of;
import static java.time.Month.JANUARY;

public class TrainDepartureEventToDynamoDBIntegrationTest {

    @Test
    @Ignore("Integration test to test the persistence in an AWS DynamoDB")
    public void subscribeTrainDepartureEvent() {

        LocalDateTime creationTime = now();
        LocalDateTime expectedTime = of(2018, JANUARY, 12, 7, 49, 0);

        TrainDepartureEvent event = TrainDepartureEventBuilder.aTrainDepartureEvent()
                .withEventCreationTime(creationTime)
                .withStationId(aStationId("myStation"))
                .withDelay(5)
                .withCanceled(false)
                .withExpectedDepartureTime(expectedTime)
                .withPlatform("C")
                .withPlatformChange(false)
                .withVehicule("MyTrain")
                .build();

        AWSCredentials credentials = getAwsCredentials();
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(getRegion())
                .build();
        TrainDepartureEventToDynamoDB persistent = new TrainDepartureEventToDynamoDB(client, new EventBus());
        persistent.subscribeTrainDepartureEvent(event);
    }

    private String getRegion() {
        return "";
    }

    private AWSCredentials getAwsCredentials() {
        return new AWSCredentials() {
                @Override
                public String getAWSAccessKeyId() {
                    return "";
                }

                @Override
                public String getAWSSecretKey() {
                    return "";
                }
            };
    }
}