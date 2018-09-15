package be.bitbox.traindelay.tracker;

import be.bitbox.traindelay.tracker.core.board.Board;
import be.bitbox.traindelay.tracker.core.board.BoardRequestException;
import be.bitbox.traindelay.tracker.core.station.Country;
import be.bitbox.traindelay.tracker.core.station.Station;
import be.bitbox.traindelay.tracker.irail.IRailBoardRequester;
import be.bitbox.traindelay.tracker.nmbs.NMBSBoardRequester;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;

import static be.bitbox.traindelay.tracker.core.board.Board.aBoardForStation;
import static be.bitbox.traindelay.tracker.core.board.BoardRequestException.aBoardRequestException;
import static be.bitbox.traindelay.tracker.core.station.Station.aStation;
import static be.bitbox.traindelay.tracker.core.station.StationId.aStationId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GeneralBoardRequesterTest {

    @Mock
    private NMBSBoardRequester nmbsBoardRequester;

    @Mock
    private IRailBoardRequester iRailBoardRequester;

    private Station station = aStation(aStationId("id"), "name", Country.BE);

    @Test
    public void normalBehavior() {
        Board board = aBoardForStation(station.stationId(), LocalDateTime.now());
        when(nmbsBoardRequester.requestBoardFor(station)).thenReturn(board);

        GeneralBoardRequester boardRequester = new GeneralBoardRequester(nmbsBoardRequester, iRailBoardRequester);
        Board returnedBoard = boardRequester.requestBoardFor(station);

        assertThat(returnedBoard).isNotNull();
        verify(nmbsBoardRequester).requestBoardFor(station);
        verify(iRailBoardRequester, never()).requestBoardFor(station);
    }

    @Test
    public void nmbsThrowsErrors() {
        Board board = aBoardForStation(station.stationId(), LocalDateTime.now());
        when(nmbsBoardRequester.requestBoardFor(station)).thenThrow(aBoardRequestException("nmbs error"));
        when(iRailBoardRequester.requestBoardFor(station)).thenReturn(board);

        GeneralBoardRequester boardRequester = new GeneralBoardRequester(nmbsBoardRequester, iRailBoardRequester);
        Board returnedBoard = boardRequester.requestBoardFor(station);

        assertThat(returnedBoard).isNotNull();
        verify(nmbsBoardRequester).requestBoardFor(station);
        verify(iRailBoardRequester).requestBoardFor(station);
    }

    @Test (expected = BoardRequestException.class)
    public void bothFail() {
        when(nmbsBoardRequester.requestBoardFor(station)).thenThrow(aBoardRequestException("nmbs error"));
        when(iRailBoardRequester.requestBoardFor(station)).thenThrow(aBoardRequestException("iRail error"));

        GeneralBoardRequester boardRequester = new GeneralBoardRequester(nmbsBoardRequester, iRailBoardRequester);
        boardRequester.requestBoardFor(station);
    }
}