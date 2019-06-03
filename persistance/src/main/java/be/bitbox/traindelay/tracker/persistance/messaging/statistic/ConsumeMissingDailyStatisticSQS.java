package be.bitbox.traindelay.tracker.persistance.messaging.statistic;

import be.bitbox.traindelay.tracker.core.statistic.DailyStatisticDao;
import be.bitbox.traindelay.tracker.core.statistic.MissingDailyStatisticEvent;
import be.bitbox.traindelay.tracker.core.statistic.MissingStatisticHandler;
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
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@Component
@EnableScheduling
public class ConsumeMissingDailyStatisticSQS {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumeMissingDailyStatisticSQS.class);
    private final AmazonSQS amazonSQS;
    private final String queueUrl;
    private final ObjectMapper objectMapper;
    private final DailyStatisticDao dailyStatisticDao;
    private final MissingStatisticHandler missingStatisticHandler;

    @Autowired
    public ConsumeMissingDailyStatisticSQS(AmazonSQS amazonSQS,
                                           @Value("${missingdailystatistic.queue.url}") String queueUrl,
                                           DailyStatisticDao dailyStatisticDao,
                                           MissingStatisticHandler missingStatisticHandler) {
        this.amazonSQS = amazonSQS;
        this.queueUrl = queueUrl;
        this.dailyStatisticDao = dailyStatisticDao;
        this.missingStatisticHandler = missingStatisticHandler;
        objectMapper = new ObjectMapper();
    }

    @Scheduled(fixedDelay = 2000)
    public void resolveMissingStatistic() {
        var missingDailyStatisticEvent = fetchMissingDailyStatisticEvent();
        missingDailyStatisticEvent.ifPresent(missingStatisticHandler::onCommandFor);
    }

    private Optional<MissingDailyStatisticEvent> fetchMissingDailyStatisticEvent() {
        var receiveMessageRequest = new ReceiveMessageRequest(queueUrl).withMaxNumberOfMessages(1);
        var receiveMessageResult = amazonSQS.receiveMessage(receiveMessageRequest);

        return receiveMessageResult.getMessages()
                .stream()
                .map(Message::getBody)
                .map(deserialize())
                .filter(Objects::nonNull)
                .filter(event -> dailyStatisticDao.getDayStatistic(event.getDate()) == null)
                .findAny();
    }

    private Function<String, MissingDailyStatisticEvent> deserialize() {
        return body -> {
            try {
                return objectMapper.readValue(body, MissingDailyStatisticEvent.class);
            } catch (IOException e) {
                LOGGER.error("Error while parsing missing statistic event from the queue", e);
                return null;
            }
        };
    }
}
