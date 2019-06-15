package be.bitbox.traindelay.tracker.persistance.messaging.statistic;

import be.bitbox.traindelay.tracker.core.statistic.MissingStationStatisticEvent;
import be.bitbox.traindelay.tracker.core.statistic.MissingStatisticHandler;
import be.bitbox.traindelay.tracker.core.statistic.StationStatisticDao;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
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
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@Component
@EnableScheduling
public class ConsumeMissingStationStatisticSQS {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumeMissingStationStatisticSQS.class);
    private final AmazonSQS amazonSQS;
    private final String queueUrl;
    private final ObjectMapper objectMapper;
    private final StationStatisticDao stationStatisticDao;
    private final MissingStatisticHandler missingStatisticHandler;

    @Autowired
    public ConsumeMissingStationStatisticSQS(AmazonSQS amazonSQS,
                                             @Value("${missingstationstatistic.queue.url}") String queueUrl,
                                             StationStatisticDao stationStatisticDao,
                                             MissingStatisticHandler missingStatisticHandler) {
        this.amazonSQS = amazonSQS;
        this.queueUrl = queueUrl;
        this.stationStatisticDao = stationStatisticDao;
        this.missingStatisticHandler = missingStatisticHandler;
        objectMapper = new ObjectMapper();
    }

    @Scheduled(fixedDelay = 2000L)
    public void resolveMissingStatistic() {
        var missingStationStatisticEvent = fetchMissingStationStatisticEvent();
        missingStationStatisticEvent.ifPresent(missingStatisticHandler::onCommandFor);
    }

    private Optional<MissingStationStatisticEvent> fetchMissingStationStatisticEvent() {
        var receiveMessageRequest = new ReceiveMessageRequest(queueUrl).withMaxNumberOfMessages(1);
        var receiveMessageResult = amazonSQS.receiveMessage(receiveMessageRequest);

        receiveMessageResult.getMessages()
                .stream()
                .map(Message::getReceiptHandle)
                .forEach(receipt -> amazonSQS.deleteMessage(new DeleteMessageRequest(queueUrl, receipt)));

        return receiveMessageResult.getMessages()
                .stream()
                .map(Message::getBody)
                .map(deserialize())
                .filter(Objects::nonNull)
                .map(MissingStationStatisticMessage::asMissingStationStatisticEvent)
                .filter(event -> stationStatisticDao.getStationStatistic(event.getStationId(), event.getLocalDate()) == null)
                .findAny();
    }

    private Function<String, MissingStationStatisticMessage> deserialize() {
        return body -> {
            try {
                return objectMapper.readValue(body, MissingStationStatisticMessage.class);
            } catch (IOException e) {
                LOGGER.error("Error while parsing missing statistic event from the queue", e);
                return null;
            }
        };
    }
}
