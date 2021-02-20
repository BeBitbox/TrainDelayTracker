package be.bitbox.traindelay.tracker.persistance.local;

import be.bitbox.traindelay.tracker.core.board.Board;
import be.bitbox.traindelay.tracker.core.board.BoardDao;
import be.bitbox.traindelay.tracker.core.station.StationId;

public class LocalBoardDao implements BoardDao {

    @Override
    public void saveBoard(Board board) {

    }

    @Override
    public Board getLastBoardFor(StationId stationId) {
        return null;
    }
}
