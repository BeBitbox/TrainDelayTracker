package be.bitbox.traindelay.tracker.nmbs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;

import static be.bitbox.traindelay.tracker.nmbs.RequestFactory.aRequest;
import static java.time.Month.AUGUST;
import static org.assertj.core.api.Assertions.assertThat;

public class RequestFactoryTest {

    @Test
    public void testJSONMarshalling() throws Exception {
        LocalDate date = LocalDate.of(2018, AUGUST, 22);
        Request request = aRequest()
                .withStationId("8892106")
                .withStationName("Deinze")
                .withDate(date)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(request);
        assertThat(json).contains("sncb-mobi");
        assertThat(json).contains("Android 5.0.2");
        assertThat(json).contains("\"meth\":\"StationBoard\"");
        assertThat(json).contains("A=1@O=Deinze@U=80@L=008892106@B=1@p=1429490515@");
        assertThat(json).contains("\"date\":\"20180822\"");
    }
}