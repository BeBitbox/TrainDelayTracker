package be.bitbox.traindelay.tracker.persistance;

import be.bitbox.traindelay.tracker.core.LockingDao;
import be.bitbox.traindelay.tracker.core.board.BoardDao;
import be.bitbox.traindelay.tracker.core.statistic.DailyStatisticDao;
import be.bitbox.traindelay.tracker.core.statistic.StationStatisticDao;

interface Config {
    DailyStatisticDao getDailyStatisticDao();

    StationStatisticDao getStationStatisticDao();

    LockingDao getLockingDao();

    BoardDao getBoardDao();
}
