package ru.immmus;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.immmus.config.ConsumerConfig;
import ru.immmus.config.KafkaConfigEmbedded;
import ru.immmus.config.KafkaConfiguration;
import ru.immmus.config.ProducerConfig;
import ru.immmus.profiles.EmbeddedKafka;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles(EmbeddedKafka.profile)
@SpringBootTest(classes = {KafkaConfigEmbedded.class, ConsumerConfig.class, ProducerConfig.class})
class KafkaServiceTest {
    @Autowired
    KafkaConfiguration kafkaConfig;
    @Autowired
    Producer<String, String> producer;
    @Autowired
    Consumer<String, String> consumer;

    @Test
    void test() {
        for (int record = 0; record < 10;) {
            producer.send(
                    new ProducerRecord<>(kafkaConfig.getTopic(), 0,
                            Long.toString(record), Long.toString(record++)));
        }

        List<PartitionInfo> partitionInfos = consumer.partitionsFor(kafkaConfig.getTopic());
        List<TopicPartition> topicPartitionList = new ArrayList<>();
        for (PartitionInfo partitionInfo : partitionInfos) {
            topicPartitionList.add(new TopicPartition(partitionInfo.topic(), partitionInfo.partition()));
        }
        consumer.assign(topicPartitionList);
        consumer.seekToBeginning(topicPartitionList);
        processRecords(consumer);
    }

    private void processRecords(Consumer<String, String> consumer) {
        for (int expectedKey = 0, expectedValue = 0; expectedKey < 10 && expectedValue < 10; ) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            long lastOffset = 0;
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("\n\roffset = %d, key = %s, value = %s", record.offset(), record.key(), record.value());
                lastOffset = record.offset();
                assertThat(Integer.valueOf(record.key())).isEqualTo(expectedKey++);
                assertThat(Integer.valueOf(record.value())).isEqualTo(expectedValue++);
            }
            System.out.println("lastOffset read: " + lastOffset);
        }
    }
}
