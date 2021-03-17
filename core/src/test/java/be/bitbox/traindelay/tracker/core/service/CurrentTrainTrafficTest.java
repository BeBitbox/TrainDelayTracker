package be.bitbox.traindelay.tracker.core.service;

import be.bitbox.traindelay.tracker.core.traindeparture.CurrentTrainTraffic;
import be.bitbox.traindelay.tracker.core.traindeparture.JsonTrainDeparture;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static be.bitbox.traindelay.tracker.core.traindeparture.JsonTrainDeparture.Builder.aJsonTrainDeparture;
import static org.assertj.core.api.Assertions.assertThat;

public class CurrentTrainTrafficTest {

    @Test
    public void testTraffic_EveryMinuteOne() {
        List<JsonTrainDeparture> departureList = generateTrainDeparturesEachMinute(60, 0);

        var currentTrainTraffic = new CurrentTrainTraffic(departureList);
        assertThat(currentTrainTraffic.getTrainDepartures()).hasSize(50);
        assertThat(currentTrainTraffic.getFuss()).isEqualTo(CurrentTrainTraffic.Fuss.BUSY);
        assertThat(currentTrainTraffic.getAverageDelay()).isEqualTo(0);
    }

    @Test
    public void testTraffic_Every90SecondsOne() {
        List<JsonTrainDeparture> departureList = generateTrainDeparturesEachMinute(90, 123);

        var currentTrainTraffic = new CurrentTrainTraffic(departureList);
        assertThat(currentTrainTraffic.getTrainDepartures()).hasSize(50);
        assertThat(currentTrainTraffic.getFuss()).isEqualTo(CurrentTrainTraffic.Fuss.MEDIOCRE);
        assertThat(currentTrainTraffic.getAverageDelay()).isEqualTo(123);
    }

    @Test
    public void testTraffic_EveryTwoMinutesOne() {
        List<JsonTrainDeparture> departureList = generateTrainDeparturesEachMinute(120, 4000);

        var currentTrainTraffic = new CurrentTrainTraffic(departureList);
        assertThat(currentTrainTraffic.getTrainDepartures()).hasSize(50);
        assertThat(currentTrainTraffic.getFuss()).isEqualTo(CurrentTrainTraffic.Fuss.CALM);
        assertThat(currentTrainTraffic.getAverageDelay()).isEqualTo(4000);
    }

    private List<JsonTrainDeparture> generateTrainDeparturesEachMinute(int seconds, int delay) {
        var localDateTime = LocalDateTime.now();
        List<JsonTrainDeparture> departureList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            localDateTime = localDateTime.minusSeconds(seconds);
            JsonTrainDeparture jsonTrainDeparture = aJsonTrainDeparture()
                    .withExpectedDepartureTime(localDateTime)
                    .withDelay(delay)
                    .build();
            departureList.add(jsonTrainDeparture);
        }
        return departureList;
    }
}
