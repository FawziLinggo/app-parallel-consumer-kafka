package confluent.alldataint.com;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ConsumerRecordHandler<K, V> {

    private final AtomicInteger numRecordsProcessed;

    public ConsumerRecordHandler() {
        numRecordsProcessed = new AtomicInteger(0);
    }

    protected abstract void processRecordImpl(ConsumerRecord<K, V> consumerRecord);

    final void processRecord(final ConsumerRecord<K, V> consumerRecord) {
        processRecordImpl(consumerRecord);
        numRecordsProcessed.incrementAndGet();
    }

    final int getNumRecordsProcessed() {
        return numRecordsProcessed.get();
    }

}
