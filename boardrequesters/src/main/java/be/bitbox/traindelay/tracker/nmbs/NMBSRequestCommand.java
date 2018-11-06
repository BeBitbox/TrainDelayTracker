package be.bitbox.traindelay.tracker.nmbs;

import be.bitbox.traindelay.tracker.core.station.Station;
import be.bitbox.traindelay.tracker.nmbs.request.Request;
import be.bitbox.traindelay.tracker.nmbs.response.Response;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static be.bitbox.traindelay.tracker.core.board.BoardRequestException.aBoardRequestException;
import static be.bitbox.traindelay.tracker.nmbs.request.RequestFactory.aRequest;

public class NMBSRequestCommand extends HystrixCommand<ResponseEntity<Response>> {
    private final Station station;
    private final String nmbsBaseUrl;
    private final RestTemplate restTemplate;
    
    NMBSRequestCommand(Station station, String nmbsBaseUrl) {
        super(HystrixCommandGroupKey.Factory.asKey("NMBSRequest"), 2000);
        this.station = station;
        this.nmbsBaseUrl = nmbsBaseUrl;
        restTemplate = new RestTemplate();
    }

    @Override
    protected ResponseEntity<Response> run() {
        Request request = aRequest()
                .withStationId(station.stationId().getId())
                .withStationName(station.name())
                .withDate(LocalDate.now())
                .build();

        return restTemplate.postForEntity(nmbsBaseUrl, request, Response.class);
    }

    @Override
    protected ResponseEntity<Response> getFallback() {
        throw aBoardRequestException("Error during NMBSrequest for " + station, getExecutionException() == null ? getFailedExecutionException() : getExecutionException());
    }
}
