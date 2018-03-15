package be.bitbox.traindelay.tracker.persistance;

import be.bitbox.traindelay.tracker.core.harvest.TrainDepartureEvent;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static be.bitbox.traindelay.tracker.core.station.StationId.aStationId;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class DynamoTrainDepartureEventQueryTest {

    @Test
    @Ignore("Requires a real DB")
    public void testQuery() throws Exception {
        AmazonDynamoDB client = AWSTestClient.create();
        DynamoTrainDepartureEventQuery queryStuff = new DynamoTrainDepartureEventQuery(client);
        LocalDate date = LocalDate.of(2018, Month.MARCH, 6);
        List<TrainDepartureEvent> events = queryStuff.listTrainDepartureFor(aStationId("BE.NMBS.008892106"), date);
        assertThat(events, hasSize(88));
    }
}