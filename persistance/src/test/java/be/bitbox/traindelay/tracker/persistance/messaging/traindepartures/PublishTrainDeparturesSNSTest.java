package be.bitbox.traindelay.tracker.persistance.messaging.traindepartures;

import be.bitbox.traindelay.tracker.core.station.Country;
import be.bitbox.traindelay.tracker.core.traindeparture.TrainDepartureEvent;
import com.amazonaws.services.sns.AmazonSNS;
import com.google.common.eventbus.EventBus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static be.bitbox.traindelay.tracker.core.station.Station.aStation;
import static be.bitbox.traindelay.tracker.core.station.StationId.aStationId;
import static be.bitbox.traindelay.tracker.core.traindeparture.TrainDepartureEvent.Builder.createTrainDepartureEvent;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PublishTrainDeparturesSNSTest {

    @Mock
    private AmazonSNS amazonSNS;
    
    @Test
    public void subscribeDepartureEvent() {
        var publishTrainDeparturesSNS = new PublishTrainDeparturesSNS(amazonSNS, new EventBus(), "topic");
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
        publishTrainDeparturesSNS.subscribeDepartureEvent(traindeparture);
        var json = "{\"station\":\"myStation\",\"expectedDepartureTime\":\"2018-04-11T11:56:34Z\",\"delay\":2,\"canceled\":false,\"vehicle\":\"TSJOEK\",\"platform\":\"3\",\"platformChange\":false}"; 
        verify(amazonSNS).publish("topic", json);
    }
}