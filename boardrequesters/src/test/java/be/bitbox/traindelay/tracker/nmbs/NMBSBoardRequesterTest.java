package be.bitbox.traindelay.tracker.nmbs;

import be.bitbox.traindelay.tracker.core.board.Board;
import be.bitbox.traindelay.tracker.core.station.Country;
import be.bitbox.traindelay.tracker.nmbs.response.ResponseToBoardTranslator;
import org.junit.Test;

import static be.bitbox.traindelay.tracker.core.station.Station.aStation;
import static be.bitbox.traindelay.tracker.core.station.StationId.aStationId;
import static org.assertj.core.api.Assertions.assertThat;

public class NMBSBoardRequesterTest {

    @Test
    public void requestBoard() {
        String url = "http://www.belgianrail.be/jp/sncb-nmbs-routeplanner/mgate.exe";

        NMBSBoardRequester requester = new NMBSBoardRequester(url);
        Board board = requester.requestBoardFor(aStation(aStationId("8892106"), "Deinze", Country.BE));

        assertThat(board.getDepartures()).hasSize(50);
    }
}