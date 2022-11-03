package confluent.alldataint.com;

import io.confluent.parallelconsumer.ParallelConsumerOptions;
import io.confluent.parallelconsumer.ParallelStreamProcessor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Properties;

import static io.confluent.parallelconsumer.ParallelConsumerOptions.CommitMode.PERIODIC_CONSUMER_ASYNCHRONOUS;
import static io.confluent.parallelconsumer.ParallelConsumerOptions.ProcessingOrder.KEY;
import static io.confluent.parallelconsumer.ParallelStreamProcessor.createEosStreamProcessor;

public class ParallelConsumerKafka {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParallelConsumerKafka.class.getName());
    private final ParallelStreamProcessor<String, String> parallelConsumer;
    private final ConsumerRecordHandler<String, String> recordHandler;

    public ParallelConsumerKafka(final ParallelStreamProcessor<String, String> parallelConsumer,
                                 final ConsumerRecordHandler<String, String> recordHandler) {
        this.parallelConsumer = parallelConsumer;
        this.recordHandler = recordHandler;
    }
    /**
     * Close the parallel consumer on application shutdown
     */
    public void shutdown() {
        LOGGER.info("shutting down");
        if (parallelConsumer != null) {
            parallelConsumer.close();
        }
    }
    public void runConsume(final Properties appProperties) {
        String topic = appProperties.getProperty("input.topic.name");

        LOGGER.info("Subscribing Parallel Consumer to consume from {} topic", topic);
        parallelConsumer.subscribe(Collections.singletonList(topic));

        LOGGER.info("Polling for records. This method blocks {}", topic);
        parallelConsumer.poll(context -> recordHandler.processRecord(context.getSingleConsumerRecord()));




    }
    public static void main(String[] args) throws Exception {

        InputStream inputStream=new FileInputStream("src/main/resources/config.properties");
        final Properties appProperties = new Properties();
        appProperties.load(inputStream);

        // construct parallel consumer
        final Consumer<String, String> consumer = new KafkaConsumer<>(appProperties);

        final ParallelConsumerOptions<String, String> options = ParallelConsumerOptions.<String, String>builder()
                .ordering(KEY)
                .maxConcurrency(16)
                .consumer(consumer)
                .commitMode(PERIODIC_CONSUMER_ASYNCHRONOUS)
                .build();
        ParallelStreamProcessor<String, String> eosStreamProcessor = createEosStreamProcessor(options);



        final ConsumerRecordHandler<String, String> recordHandler = new PrintRecordHandler();


        // run the consumer!
        final ParallelConsumerKafka consumerApplication = new ParallelConsumerKafka(eosStreamProcessor, recordHandler);


        Runtime.getRuntime().addShutdownHook(new Thread(consumerApplication::shutdown));
        consumerApplication.runConsume(appProperties);

    }
}
