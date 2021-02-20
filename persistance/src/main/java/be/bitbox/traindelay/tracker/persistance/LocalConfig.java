package be.bitbox.traindelay.tracker.persistance;

import be.bitbox.traindelay.tracker.core.LockingDao;
import be.bitbox.traindelay.tracker.core.board.BoardDao;
import be.bitbox.traindelay.tracker.core.statistic.DailyStatisticDao;
import be.bitbox.traindelay.tracker.core.statistic.StationStatisticDao;
import be.bitbox.traindelay.tracker.persistance.local.LocalBoardDao;
import be.bitbox.traindelay.tracker.persistance.local.LocalDailyStatisticDao;
import be.bitbox.traindelay.tracker.persistance.local.LocalLockingDao;
import be.bitbox.traindelay.tracker.persistance.local.LocalStationStatisticDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
public class LocalConfig implements Config {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalConfig.class);

    public LocalConfig() {
        LOGGER.info("Loading local persistance config");
    }

    @Bean
    @Override
    public DailyStatisticDao getDailyStatisticDao() {
        return new LocalDailyStatisticDao();
    }

    @Bean
    @Override
    public StationStatisticDao getStationStatisticDao() {
        return new LocalStationStatisticDao();
    }

    @Bean
    @Override
    public LockingDao getLockingDao() {
        return new LocalLockingDao();
    }

    @Bean
    @Override
    public BoardDao getBoardDao() {
        return new LocalBoardDao();
    }
}
