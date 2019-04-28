package ru.immmus.config;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.*;

@Configuration
@PropertySource("classpath:kafkaProducer.properties")
public class ProducerConfig {
    private final KafkaConfiguration kafkaConfiguration;
    private final static String SERIALIZER = "org.apache.kafka.common.serialization.StringSerializer";
    @Value("${kafka.producer.batchSizeMessages}")
    private int batchSize;
    @Value("${kafka.producer.retries}")
    private int retries;
    @Value("${kafka.producer.lingerMs}")
    private int lingerMs;

    @Autowired
    public ProducerConfig(KafkaConfiguration kafkaConfiguration) {
        this.kafkaConfiguration = kafkaConfiguration;
    }

    @Bean(destroyMethod = "close")
    public Producer<String, String> producer() {
        Map<String, Object> props = new HashMap<>();
        props.put(BOOTSTRAP_SERVERS_CONFIG, this.kafkaConfiguration.getBrokerAddress());
        props.put(ACKS_CONFIG, "all");
        props.put(RETRIES_CONFIG, this.retries);
        // Controls how much bytes sender would wait to batch up before publishing to Kafka.
        props.put(BATCH_SIZE_CONFIG, this.batchSize);
        props.put(LINGER_MS_CONFIG, this.lingerMs);
        props.put(KEY_SERIALIZER_CLASS_CONFIG, SERIALIZER);
        props.put(VALUE_SERIALIZER_CLASS_CONFIG, SERIALIZER);
        return new KafkaProducer<>(Collections.unmodifiableMap(props));
    }
}
