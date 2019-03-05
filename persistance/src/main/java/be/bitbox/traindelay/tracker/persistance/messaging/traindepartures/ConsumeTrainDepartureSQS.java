package be.bitbox.traindelay.tracker.persistance.messaging.traindepartures;

import be.bitbox.traindelay.tracker.core.service.JsonTrainDeparture;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;

import static com.google.common.collect.Lists.newArrayList;

@EnableScheduling
@Component
public class ConsumeTrainDepartureSQS {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumeTrainDepartureSQS.class);
    private final AmazonSQS amazonSQS;
    private final String queueUrl;
    private final ObjectMapper objectMapper;
    private final SortedSet<JsonTrainDeparture> recentTrainDepartures;
    private final int maxTrainDepartures;
    
    @Autowired
    public ConsumeTrainDepartureSQS(AmazonSQS amazonSQS,
                                    @Value("${queue.name}") String queueName,
                                    @Value("${traindepartures.recent.max}") int maxTrainDepartures) {
        this.amazonSQS = amazonSQS;
        queueUrl = amazonSQS.getQueueUrl(queueName).getQueueUrl();
        objectMapper = new ObjectMapper();
        recentTrainDepartures = new TreeSet<>();
        this.maxTrainDepartures = maxTrainDepartures;
    }
    
    @Scheduled(fixedDelay = 10000L)
    public void readMessages() {
        var receiveMessageRequest = new ReceiveMessageRequest(queueUrl).withWaitTimeSeconds(5);
        var receiveMessageResult = amazonSQS.receiveMessage(receiveMessageRequest);
        receiveMessageResult.getMessages()
                .stream()
                .map(Message::getBody)
                .map(getStringJsonTrainDepartureFunction())
                .filter(Objects::nonNull)
                .forEach(this::addToSet);
        
    }
    
    private synchronized void addToSet(JsonTrainDeparture jsonTrainDeparture) {
        recentTrainDepartures.add(jsonTrainDeparture);
        while (recentTrainDepartures.size() > maxTrainDepartures) {
            recentTrainDepartures.remove(recentTrainDepartures.last());
        }
    }

    private Function<String, JsonTrainDeparture> getStringJsonTrainDepartureFunction() {
        return body -> {
            try {
                return objectMapper.readValue(body, JsonTrainDeparture.class);
            } catch (IOException e) {
                LOGGER.error("Error while parsing latest traindeparture from the queue", e);
                return null;
            }
        };
    }

    public List<JsonTrainDeparture> getRecentTrainDepartures() {
        return newArrayList(recentTrainDepartures);
    }
}
