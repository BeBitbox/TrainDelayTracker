package be.bitbox.traindelay.tracker.nmbs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;

import be.bitbox.traindelay.tracker.nmbs.response.Response;

@RestController
@Profile("local")
public class LocalNMBSRestEndpoint {

    private final Response response;

    @Autowired
    public LocalNMBSRestEndpoint() throws IOException {
        response = getResponseFrom();
    }

    @RequestMapping(method = RequestMethod.POST, path = "/api/v1/mydummynmbs")
    public Response getStation() {
        return response;
    }

    private Response getResponseFrom() throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("local_nmbs_response.json");
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(inputStream, Response.class);
    }
}
