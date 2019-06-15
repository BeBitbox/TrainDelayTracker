package be.bitbox.traindelay.tracker.persistance.messaging.traindepartures;

import be.bitbox.traindelay.tracker.core.service.JsonTrainDeparture;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.DeleteMessageBatchRequest;
import com.amazonaws.services.sqs.model.DeleteMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

@Component
public class ConsumeTrainDepartureSQS {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumeTrainDepartureSQS.class);
    private final AmazonSQS amazonSQS;
    private final String queueUrl;
    private final ObjectMapper objectMapper;
    private final RecentTrainDepartures recentTrainDepartures;

    @Autowired
    public ConsumeTrainDepartureSQS(AmazonSQS amazonSQS,
                                    @Value("${traindeparture.queue.url}") String queueUrl,
                                    RecentTrainDepartures recentTrainDepartures) {
        this.amazonSQS = amazonSQS;
        this.queueUrl = queueUrl;
        this.recentTrainDepartures = recentTrainDepartures;
        objectMapper = new ObjectMapper();
    }

    public void updateLatestTrainDepartures() {
        int teller;
        do {
            teller = updateLatestTenTrainDepartures();
        } while (teller >= 10);
    }

    private int updateLatestTenTrainDepartures() {
        var receiveMessageRequest = new ReceiveMessageRequest(queueUrl).withMaxNumberOfMessages(10);
        var receiveMessageResult = amazonSQS.receiveMessage(receiveMessageRequest);
        int size = receiveMessageResult.getMessages().size();
        LOGGER.info(String.format("Received %d messages", size));
        receiveMessageResult.getMessages()
                .stream()
                .map(Message::getBody)
                .map(getStringJsonTrainDepartureFunction())
                .filter(Objects::nonNull)
                .forEach(recentTrainDepartures::addTrainDeparture);

        if (size > 0) {
            var deletes = receiveMessageResult.getMessages()
                    .stream()
                    .map(message -> new DeleteMessageBatchRequestEntry(message.getMessageId(), message.getReceiptHandle()))
                    .collect(toList());
            amazonSQS.deleteMessageBatch(new DeleteMessageBatchRequest(queueUrl).withEntries(deletes));
        }
        return size;
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
}
