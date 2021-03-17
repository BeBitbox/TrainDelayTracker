package be.bitbox.traindelay.tracker.persistance.messaging.statistic;

import be.bitbox.traindelay.tracker.core.statistic.MissingDailyStatisticEvent;
import be.bitbox.traindelay.tracker.core.statistic.MissingStationStatisticEvent;
import be.bitbox.traindelay.tracker.core.service.MissingStatisticHandler;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
class PublishMissingStatistics {
    private static final Logger LOGGER = LoggerFactory.getLogger(PublishMissingStatistics.class);
    private final Queue<MissingStationStatisticEvent> stationQueue;
    private final Queue<MissingDailyStatisticEvent> dailyQueue;
    private final MissingStatisticHandler missingStatisticHandler;
    private final AtomicBoolean running;

    @Autowired
    PublishMissingStatistics(EventBus eventBus, MissingStatisticHandler missingStatisticHandler) {
        eventBus.register(this);
        this.missingStatisticHandler = missingStatisticHandler;
        stationQueue = new ConcurrentLinkedQueue<>();
        dailyQueue = new ConcurrentLinkedQueue<>();
        running = new AtomicBoolean(false);
    }

    @Subscribe
    void subscribeMissingStationStatisticEvent(MissingStationStatisticEvent missingStationStatisticEvent) {
        stationQueue.add(missingStationStatisticEvent);
        tick();
    }

    @Subscribe
    void subscribeMissingDailyStatisticEvent(MissingDailyStatisticEvent missingDailyStatisticEvent) {
        dailyQueue.add(missingDailyStatisticEvent);
        tick();
    }

    public void tick() {
        if(running.compareAndSet(false, true)) {
            LOGGER.info("Tick triggered {} {}", stationQueue.size(), dailyQueue.size());
            while(stationQueue.peek() != null) {
                missingStatisticHandler.onCommandFor(stationQueue.poll());
            }
            while(dailyQueue.peek() != null) {
                missingStatisticHandler.onCommandFor(dailyQueue.poll());
            }
            running.set(false);
        }
    }
}
