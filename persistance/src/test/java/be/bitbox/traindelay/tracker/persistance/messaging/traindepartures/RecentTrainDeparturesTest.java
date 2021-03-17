package be.bitbox.traindelay.tracker.persistance.messaging.traindepartures;

import be.bitbox.traindelay.tracker.core.traindeparture.JsonTrainDeparture;
import org.junit.Test;

import java.time.LocalDateTime;

import static be.bitbox.traindelay.tracker.core.traindeparture.JsonTrainDeparture.Builder.aJsonTrainDeparture;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class RecentTrainDeparturesTest {

    @Test
    public void list() {
        var recentTrainDepartures = new RecentTrainDepartures(2);

        recentTrainDepartures.addTrainDeparture(create("TOO OLD", LocalDateTime.now().minusMonths(3)));
        recentTrainDepartures.addTrainDeparture(create("NOW", LocalDateTime.now()));
        recentTrainDepartures.addTrainDeparture(create("YESTERDAY", LocalDateTime.now().minusDays(1)));
        recentTrainDepartures.addTrainDeparture(create("TOO OLD", LocalDateTime.now().minusDays(2)));

        var jsonTrainDepartures = recentTrainDepartures.list();
        assertThat(jsonTrainDepartures.size(), is(2));
        assertThat(jsonTrainDepartures.get(0).getStation(), is("NOW"));
        assertThat(jsonTrainDepartures.get(1).getStation(), is("YESTERDAY"));
    }

    private JsonTrainDeparture create(String stationName, LocalDateTime time) {
        return aJsonTrainDeparture()
                .withStation(stationName)
                .withExpectedDepartureTime(time)
                .build();
    }
}