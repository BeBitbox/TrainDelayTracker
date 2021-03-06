package be.bitbox.traindelay.tracker.persistance.db.traindepartures;

import be.bitbox.traindelay.tracker.persistance.dynamodb.traindepartures.DynamoDepartureEventQuery;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import be.bitbox.traindelay.tracker.core.traindeparture.TrainDepartureEvent;
import be.bitbox.traindelay.tracker.persistance.db.AWSTestClient;
import static be.bitbox.traindelay.tracker.core.station.StationId.aStationId;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class DynamoDepartureEventQueryTest {

    @Test
    @Ignore("Requires a real DB")
    public void testQuery() {
        DynamoDBMapper client = AWSTestClient.create();
        DynamoDepartureEventQuery queryStuff = new DynamoDepartureEventQuery(client);
        LocalDate date = LocalDate.of(2018, Month.MARCH, 6);
        List<TrainDepartureEvent> events = queryStuff.listTrainDepartureFor(aStationId("BE.NMBS.008892106"), date);
        assertThat(events, hasSize(88));
    }
}