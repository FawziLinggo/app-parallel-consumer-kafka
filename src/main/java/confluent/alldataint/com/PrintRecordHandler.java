package confluent.alldataint.com;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Record handler that writes the Kafka message value to a file.
 */
public class PrintRecordHandler extends ConsumerRecordHandler<String, String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParallelConsumerKafka.class.getName());


    public PrintRecordHandler() {
    }

    @Override
    protected void processRecordImpl(final ConsumerRecord<String, String> consumerRecord) {
        LOGGER.info("|value|"+consumerRecord.value()+"|offset|"+consumerRecord.offset()+"|PARTITION|"+consumerRecord.partition()+"|");
    }

}

