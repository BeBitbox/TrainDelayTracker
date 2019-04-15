package be.bitbox.traindelay.tracker.core.service;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static be.bitbox.traindelay.tracker.core.service.JsonTrainDeparture.Builder.aJsonTrainDeparture;
import static org.assertj.core.api.Assertions.assertThat;

public class CurrentTrainTrafficTest {

    @Test
    public void testTraffic_EveryMinuteOne() {
        List<JsonTrainDeparture> departureList = generateTrainDeparturesEachMinute(1, 0);

        var currentTrainTraffic = new CurrentTrainTraffic(departureList);
        assertThat(currentTrainTraffic.getTrainDepartures()).hasSize(50);
        assertThat(currentTrainTraffic.getFuss()).isEqualTo(CurrentTrainTraffic.Fuss.BUSY);
        assertThat(currentTrainTraffic.getAverageDelay()).isEqualTo(0);
    }

    @Test
    public void testTraffic_EveryTwoMinutesOne() {
        List<JsonTrainDeparture> departureList = generateTrainDeparturesEachMinute(2, 123);

        var currentTrainTraffic = new CurrentTrainTraffic(departureList);
        assertThat(currentTrainTraffic.getTrainDepartures()).hasSize(50);
        assertThat(currentTrainTraffic.getFuss()).isEqualTo(CurrentTrainTraffic.Fuss.MEDIOCRE);
        assertThat(currentTrainTraffic.getAverageDelay()).isEqualTo(123);
    }

    @Test
    public void testTraffic_EveryThreeMinutesOne() {
        List<JsonTrainDeparture> departureList = generateTrainDeparturesEachMinute(3, 4000);

        var currentTrainTraffic = new CurrentTrainTraffic(departureList);
        assertThat(currentTrainTraffic.getTrainDepartures()).hasSize(50);
        assertThat(currentTrainTraffic.getFuss()).isEqualTo(CurrentTrainTraffic.Fuss.CALM);
        assertThat(currentTrainTraffic.getAverageDelay()).isEqualTo(4000);
    }

    private List<JsonTrainDeparture> generateTrainDeparturesEachMinute(int minutes, int delay) {
        var localDateTime = LocalDateTime.now();
        List<JsonTrainDeparture> departureList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            localDateTime = localDateTime.minusMinutes(minutes);
            JsonTrainDeparture jsonTrainDeparture = aJsonTrainDeparture()
                    .withExpectedDepartureTime(localDateTime)
                    .withDelay(delay)
                    .build();
            departureList.add(jsonTrainDeparture);
        }
        return departureList;
    }
}
