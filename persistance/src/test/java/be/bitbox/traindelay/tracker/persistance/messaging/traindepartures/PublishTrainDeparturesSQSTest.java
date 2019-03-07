package be.bitbox.traindelay.tracker.persistance.messaging.traindepartures;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.google.common.eventbus.EventBus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static be.bitbox.traindelay.tracker.core.station.StationId.aStationId;
import static be.bitbox.traindelay.tracker.core.traindeparture.TrainDepartureEvent.Builder.createTrainDepartureEvent;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PublishTrainDeparturesSQSTest {

    @Mock
    private AmazonSQS amazonSQS;
    
    @Test
    public void subscribeDepartureEvent() throws IOException {
        var recentTrainDepartures = new RecentTrainDepartures(5);
        var publishTrainDeparturesSQS = new PublishTrainDeparturesSQS(amazonSQS, new EventBus(), "queueURL", recentTrainDepartures);
        var stationId = aStationId("myStation");
        var localDate = LocalDate.ofYearDay(2018, 101);
        var localTime = LocalTime.of(11, 56, 34);
        var traindeparture = createTrainDepartureEvent()
                .withStationId(stationId)
                .withPlatform("3")
                .withDelay(120)
                .withVehicle("BE.NMBS.TSJOEK")
                .withExpectedDepartureTime(LocalDateTime.of(localDate, localTime))
                .build();
        publishTrainDeparturesSQS.subscribeDepartureEvent(traindeparture);
        var json = TestJsonFileReader.readJson();
        var expectedSendMessage = new SendMessageRequest("queueURL", json);
        verify(amazonSQS).sendMessage(expectedSendMessage);
    }
}