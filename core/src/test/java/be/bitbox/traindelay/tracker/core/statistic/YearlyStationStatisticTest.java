package be.bitbox.traindelay.tracker.core.statistic;

import org.junit.Test;

import java.util.List;

import static be.bitbox.traindelay.tracker.core.station.StationId.aStationId;
import static be.bitbox.traindelay.tracker.core.statistic.StationStatistic.StationStatisticBuilder.aStationStatistic;
import static be.bitbox.traindelay.tracker.core.statistic.YearlyStationStatistic.yearlyStationStatisticFrom;
import static org.assertj.core.api.Assertions.assertThat;

public class YearlyStationStatisticTest {

    @Test(expected = NullPointerException.class)
    public void testWithNull() {
        yearlyStationStatisticFrom(null);
    }

    @Test
    public void testWithOneStation() {
        var stationStatistic = createStationStatistic("My Station", new int[]{40, 1, 2, 1, 3});

        var yearlyStationStatistic = yearlyStationStatisticFrom(List.of(stationStatistic));

        assertThat(yearlyStationStatistic.getNoDepartures()).hasSize(0);
        assertThat(yearlyStationStatistic.getTop5Popular()).hasSize(1);
        assertThat(yearlyStationStatistic.getTop5Popular().get(0).getStationId()).isEqualTo(aStationId("My Station"));
        assertThat(yearlyStationStatistic.getTop5Popular().get(0).getNumber()).isEqualTo(3);
        assertThat(yearlyStationStatistic.getBottom5Popular()).hasSize(1);
        assertThat(yearlyStationStatistic.getBottom5Popular().get(0).getStationId()).isEqualTo(aStationId("My Station"));
        assertThat(yearlyStationStatistic.getBottom5Popular().get(0).getNumber()).isEqualTo(3);
        assertThat(yearlyStationStatistic.getTop5Delays()).hasSize(1);
        assertThat(yearlyStationStatistic.getTop5Delays().get(0).getStationId()).isEqualTo(aStationId("My Station"));
        assertThat(yearlyStationStatistic.getTop5Delays().get(0).getNumber()).isEqualTo(2);
        assertThat(yearlyStationStatistic.getBottom5Delays()).hasSize(1);
        assertThat(yearlyStationStatistic.getBottom5Delays().get(0).getStationId()).isEqualTo(aStationId("My Station"));
        assertThat(yearlyStationStatistic.getBottom5Delays().get(0).getNumber()).isEqualTo(2);
        assertThat(yearlyStationStatistic.getTop5Cancellations()).hasSize(1);
        assertThat(yearlyStationStatistic.getTop5Cancellations().get(0).getStationId()).isEqualTo(aStationId("My Station"));
        assertThat(yearlyStationStatistic.getTop5Cancellations().get(0).getNumber()).isEqualTo(1);
        assertThat(yearlyStationStatistic.getBottom5Cancellations()).hasSize(1);
        assertThat(yearlyStationStatistic.getBottom5Cancellations().get(0).getStationId()).isEqualTo(aStationId("My Station"));
        assertThat(yearlyStationStatistic.getBottom5Cancellations().get(0).getNumber()).isEqualTo(1);
        assertThat(yearlyStationStatistic.getTop5AverageDelay()).hasSize(1);
        assertThat(yearlyStationStatistic.getTop5AverageDelay().get(0).getStationId()).isEqualTo(aStationId("My Station"));
        assertThat(yearlyStationStatistic.getTop5AverageDelay().get(0).getNumber()).isEqualTo(40);
        assertThat(yearlyStationStatistic.getBottom5AverageDelay()).hasSize(1);
        assertThat(yearlyStationStatistic.getBottom5AverageDelay().get(0).getStationId()).isEqualTo(aStationId("My Station"));
        assertThat(yearlyStationStatistic.getBottom5AverageDelay().get(0).getNumber()).isEqualTo(40);
    }

    @Test
    public void testWithLotsOfStations() {
        // All the stations                                            avgDelay, canc, delays, platCh, depart
        var station01 = createStationStatistic("station01", new int[]{    20,    12,     22,      1, 76});
        var station02 = createStationStatistic("station02", new int[]{    10,    11,     33,      1, 99});
        var station03 = createStationStatistic("station03", new int[]{    70,    10,     11,      1,  8});
        var station04 = createStationStatistic("station04", new int[]{    30,     9,     52,      1, 80});
        var station05 = createStationStatistic("station05", new int[]{     5,     8,      2,      1, 40});
        var station06 = createStationStatistic("station06", new int[]{     0,     7,     12,      1, 10});
        var station07 = createStationStatistic("station07", new int[]{     0,     0,      0,      0,  0});
        var station08 = createStationStatistic("station08", new int[]{    90,     5,      0,      1, 13});
        var station09 = createStationStatistic("station09", new int[]{    80,     4,      1,      1,  3});
        var station10 = createStationStatistic("station10", new int[]{    35,     3,      4,      5, 21});
        var station11 = createStationStatistic("station11", new int[]{    40,     2,      2,      1, 91});
        var station12 = createStationStatistic("station12", new int[]{    20,     1,      3,      9,  9});

        var yearlyStationStatistic = yearlyStationStatisticFrom(List.of(station01, station02, station03, station04,
                station05, station06, station07, station08, station09, station10, station11, station12));

        //Non-Active trains
        assertThat(yearlyStationStatistic.getNoDepartures()).containsOnly(aStationId("station07"));

        //POPULARS
        assertThat(yearlyStationStatistic.getTop5Popular()).hasSize(5);
        assertThat(yearlyStationStatistic.getTop5Popular().get(0).getStationId()).isEqualTo(aStationId("station02"));
        assertThat(yearlyStationStatistic.getTop5Popular().get(0).getNumber()).isEqualTo(99);
        assertThat(yearlyStationStatistic.getTop5Popular().get(1).getStationId()).isEqualTo(aStationId("station11"));
        assertThat(yearlyStationStatistic.getTop5Popular().get(1).getNumber()).isEqualTo(91);
        assertThat(yearlyStationStatistic.getTop5Popular().get(2).getStationId()).isEqualTo(aStationId("station04"));
        assertThat(yearlyStationStatistic.getTop5Popular().get(2).getNumber()).isEqualTo(80);
        assertThat(yearlyStationStatistic.getTop5Popular().get(3).getStationId()).isEqualTo(aStationId("station01"));
        assertThat(yearlyStationStatistic.getTop5Popular().get(3).getNumber()).isEqualTo(76);
        assertThat(yearlyStationStatistic.getTop5Popular().get(4).getStationId()).isEqualTo(aStationId("station05"));
        assertThat(yearlyStationStatistic.getTop5Popular().get(4).getNumber()).isEqualTo(40);

        assertThat(yearlyStationStatistic.getBottom5Popular()).hasSize(5);
        assertThat(yearlyStationStatistic.getBottom5Popular().get(0).getStationId()).isEqualTo(aStationId("station09"));
        assertThat(yearlyStationStatistic.getBottom5Popular().get(0).getNumber()).isEqualTo(3);
        assertThat(yearlyStationStatistic.getBottom5Popular().get(1).getStationId()).isEqualTo(aStationId("station03"));
        assertThat(yearlyStationStatistic.getBottom5Popular().get(1).getNumber()).isEqualTo(8);
        assertThat(yearlyStationStatistic.getBottom5Popular().get(2).getStationId()).isEqualTo(aStationId("station12"));
        assertThat(yearlyStationStatistic.getBottom5Popular().get(2).getNumber()).isEqualTo(9);
        assertThat(yearlyStationStatistic.getBottom5Popular().get(3).getStationId()).isEqualTo(aStationId("station06"));
        assertThat(yearlyStationStatistic.getBottom5Popular().get(3).getNumber()).isEqualTo(10);
        assertThat(yearlyStationStatistic.getBottom5Popular().get(4).getStationId()).isEqualTo(aStationId("station08"));
        assertThat(yearlyStationStatistic.getBottom5Popular().get(4).getNumber()).isEqualTo(13);

        //DELAYS
        assertThat(yearlyStationStatistic.getTop5Delays()).hasSize(5);
        assertThat(yearlyStationStatistic.getTop5Delays().get(0).getStationId()).isEqualTo(aStationId("station04"));
        assertThat(yearlyStationStatistic.getTop5Delays().get(0).getNumber()).isEqualTo(52);
        assertThat(yearlyStationStatistic.getTop5Delays().get(1).getStationId()).isEqualTo(aStationId("station02"));
        assertThat(yearlyStationStatistic.getTop5Delays().get(1).getNumber()).isEqualTo(33);
        assertThat(yearlyStationStatistic.getTop5Delays().get(2).getStationId()).isEqualTo(aStationId("station01"));
        assertThat(yearlyStationStatistic.getTop5Delays().get(2).getNumber()).isEqualTo(22);
        assertThat(yearlyStationStatistic.getTop5Delays().get(3).getStationId()).isEqualTo(aStationId("station06"));
        assertThat(yearlyStationStatistic.getTop5Delays().get(3).getNumber()).isEqualTo(12);
        assertThat(yearlyStationStatistic.getTop5Delays().get(4).getStationId()).isEqualTo(aStationId("station03"));
        assertThat(yearlyStationStatistic.getTop5Delays().get(4).getNumber()).isEqualTo(11);

        assertThat(yearlyStationStatistic.getBottom5Delays()).hasSize(5);
        assertThat(yearlyStationStatistic.getBottom5Delays().get(0).getStationId()).isEqualTo(aStationId("station08"));
        assertThat(yearlyStationStatistic.getBottom5Delays().get(0).getNumber()).isEqualTo(0);
        assertThat(yearlyStationStatistic.getBottom5Delays().get(1).getStationId()).isEqualTo(aStationId("station09"));
        assertThat(yearlyStationStatistic.getBottom5Delays().get(1).getNumber()).isEqualTo(1);
        assertThat(yearlyStationStatistic.getBottom5Delays().get(2).getStationId()).isEqualTo(aStationId("station05"));
        assertThat(yearlyStationStatistic.getBottom5Delays().get(2).getNumber()).isEqualTo(2);
        assertThat(yearlyStationStatistic.getBottom5Delays().get(3).getStationId()).isEqualTo(aStationId("station11"));
        assertThat(yearlyStationStatistic.getBottom5Delays().get(3).getNumber()).isEqualTo(2);
        assertThat(yearlyStationStatistic.getBottom5Delays().get(4).getStationId()).isEqualTo(aStationId("station12"));
        assertThat(yearlyStationStatistic.getBottom5Delays().get(4).getNumber()).isEqualTo(3);

        //CANCELLATIONS
        assertThat(yearlyStationStatistic.getTop5Cancellations()).hasSize(5);
        assertThat(yearlyStationStatistic.getTop5Cancellations().get(0).getStationId()).isEqualTo(aStationId("station01"));
        assertThat(yearlyStationStatistic.getTop5Cancellations().get(0).getNumber()).isEqualTo(12);
        assertThat(yearlyStationStatistic.getTop5Cancellations().get(1).getStationId()).isEqualTo(aStationId("station02"));
        assertThat(yearlyStationStatistic.getTop5Cancellations().get(1).getNumber()).isEqualTo(11);
        assertThat(yearlyStationStatistic.getTop5Cancellations().get(2).getStationId()).isEqualTo(aStationId("station03"));
        assertThat(yearlyStationStatistic.getTop5Cancellations().get(2).getNumber()).isEqualTo(10);
        assertThat(yearlyStationStatistic.getTop5Cancellations().get(3).getStationId()).isEqualTo(aStationId("station04"));
        assertThat(yearlyStationStatistic.getTop5Cancellations().get(3).getNumber()).isEqualTo(9);
        assertThat(yearlyStationStatistic.getTop5Cancellations().get(4).getStationId()).isEqualTo(aStationId("station05"));
        assertThat(yearlyStationStatistic.getTop5Cancellations().get(4).getNumber()).isEqualTo(8);

        assertThat(yearlyStationStatistic.getBottom5Cancellations()).hasSize(5);
        assertThat(yearlyStationStatistic.getBottom5Cancellations().get(0).getStationId()).isEqualTo(aStationId("station12"));
        assertThat(yearlyStationStatistic.getBottom5Cancellations().get(0).getNumber()).isEqualTo(1);
        assertThat(yearlyStationStatistic.getBottom5Cancellations().get(1).getStationId()).isEqualTo(aStationId("station11"));
        assertThat(yearlyStationStatistic.getBottom5Cancellations().get(1).getNumber()).isEqualTo(2);
        assertThat(yearlyStationStatistic.getBottom5Cancellations().get(2).getStationId()).isEqualTo(aStationId("station10"));
        assertThat(yearlyStationStatistic.getBottom5Cancellations().get(2).getNumber()).isEqualTo(3);
        assertThat(yearlyStationStatistic.getBottom5Cancellations().get(3).getStationId()).isEqualTo(aStationId("station09"));
        assertThat(yearlyStationStatistic.getBottom5Cancellations().get(3).getNumber()).isEqualTo(4);
        assertThat(yearlyStationStatistic.getBottom5Cancellations().get(4).getStationId()).isEqualTo(aStationId("station08"));
        assertThat(yearlyStationStatistic.getBottom5Cancellations().get(4).getNumber()).isEqualTo(5);

        //AVERAGE DELAYS
        assertThat(yearlyStationStatistic.getTop5AverageDelay()).hasSize(5);
        assertThat(yearlyStationStatistic.getTop5AverageDelay().get(0).getStationId()).isEqualTo(aStationId("station08"));
        assertThat(yearlyStationStatistic.getTop5AverageDelay().get(0).getNumber()).isEqualTo(90);
        assertThat(yearlyStationStatistic.getTop5AverageDelay().get(1).getStationId()).isEqualTo(aStationId("station09"));
        assertThat(yearlyStationStatistic.getTop5AverageDelay().get(1).getNumber()).isEqualTo(80);
        assertThat(yearlyStationStatistic.getTop5AverageDelay().get(2).getStationId()).isEqualTo(aStationId("station03"));
        assertThat(yearlyStationStatistic.getTop5AverageDelay().get(2).getNumber()).isEqualTo(70);
        assertThat(yearlyStationStatistic.getTop5AverageDelay().get(3).getStationId()).isEqualTo(aStationId("station11"));
        assertThat(yearlyStationStatistic.getTop5AverageDelay().get(3).getNumber()).isEqualTo(40);
        assertThat(yearlyStationStatistic.getTop5AverageDelay().get(4).getStationId()).isEqualTo(aStationId("station10"));
        assertThat(yearlyStationStatistic.getTop5AverageDelay().get(4).getNumber()).isEqualTo(35);

        assertThat(yearlyStationStatistic.getBottom5AverageDelay()).hasSize(5);
        assertThat(yearlyStationStatistic.getBottom5AverageDelay().get(0).getStationId()).isEqualTo(aStationId("station06"));
        assertThat(yearlyStationStatistic.getBottom5AverageDelay().get(0).getNumber()).isEqualTo(0);
        assertThat(yearlyStationStatistic.getBottom5AverageDelay().get(1).getStationId()).isEqualTo(aStationId("station05"));
        assertThat(yearlyStationStatistic.getBottom5AverageDelay().get(1).getNumber()).isEqualTo(5);
        assertThat(yearlyStationStatistic.getBottom5AverageDelay().get(2).getStationId()).isEqualTo(aStationId("station02"));
        assertThat(yearlyStationStatistic.getBottom5AverageDelay().get(2).getNumber()).isEqualTo(10);
        assertThat(yearlyStationStatistic.getBottom5AverageDelay().get(3).getStationId()).isEqualTo(aStationId("station01"));
        assertThat(yearlyStationStatistic.getBottom5AverageDelay().get(3).getNumber()).isEqualTo(20);
        assertThat(yearlyStationStatistic.getBottom5AverageDelay().get(4).getStationId()).isEqualTo(aStationId("station12"));
        assertThat(yearlyStationStatistic.getBottom5AverageDelay().get(4).getNumber()).isEqualTo(20);
    }

    private static StationStatistic createStationStatistic(String id, int[] numbers) {
        return aStationStatistic()
                .withStationId(aStationId(id))
                .withAverageDelay(numbers[0])
                .withCancellations(numbers[1])
                .withDelays(numbers[2])
                .withPlatformChanges(numbers[3])
                .withDepartures(numbers[4])
                .build();
    }

}