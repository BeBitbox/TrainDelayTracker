package be.bitbox.traindelay.tracker.ui.gridgenerators;

import be.bitbox.traindelay.tracker.core.statistic.Statistic;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.server.VaadinService;

import java.util.Locale;

import static be.bitbox.traindelay.tracker.core.statistic.DummyStatistic.aDummyStatistic;

public class StationStatisticGrid extends Grid<StationStatisticGrid.Entry> {

    private final Locale locale;

    public StationStatisticGrid(Statistic statistic) {
        super();
        locale = UI.getCurrent().getLocale();
        addColumn(Entry::getLabel);
        addColumn(Entry::getValue);

        Statistic existingStatistic = statistic == null ? aDummyStatistic() : statistic;
        int departures = existingStatistic.getDepartures();
        var departuresEntry = new Entry(translate("general.departures.number"), String.format("%,d", departures));
        int delayPercentage = departures == 0 ? 0 : existingStatistic.getDelays() * 100 / departures;
        var delaysEntry = new Entry(translate("general.delay.percentage"), delayPercentage + "%");
        var averageDelayEntry = new Entry(translate("general.delay.average"), String.format("%d %s", existingStatistic.getAverageDelay(), translate("general.seconds")));
        var cancellationsEntry = new Entry(translate("general.cancellations"), String.format("%,d %s", existingStatistic.getCancellations(), translate("general.trains")));
        var platformChangesEntry = new Entry(translate("general.platform.changes"), String.format("%,d %s", existingStatistic.getPlatformChanges(), translate("general.times")));

        setItems(departuresEntry, delaysEntry, averageDelayEntry, cancellationsEntry, platformChangesEntry);
    }

    static class Entry {
        private final String label;
        private final String value;

        private Entry(String label, String value) {
            this.label = label;
            this.value = value;
        }

        String getLabel() {
            return label;
        }

        String getValue() {
            return value;
        }
    }

    private String translate(String key) {
        return VaadinService.getCurrent().getInstantiator().getI18NProvider().getTranslation(key, locale);
    }
}
