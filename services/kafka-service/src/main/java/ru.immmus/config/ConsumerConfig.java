package ru.immmus.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

@Slf4j
@Configuration
@PropertySource("classpath:kafkaConsumer.properties")
public class ConsumerConfig {
    private final KafkaConfiguration kafkaConfiguration;
    @Value("${auto.offset.reset}")
    private String offsetReset;
    @Value("${auto.commit.interval.ms}")
    private String commitIntervalMs;
    @Value("${kafka.consumer.groupId}")
    private String groupId;
    @Value("${kafka.consumer.timeout.ms}")
    private int timeoutMs;

    @Autowired
    public ConsumerConfig(KafkaConfiguration kafkaConfiguration) {
        this.kafkaConfiguration = kafkaConfiguration;
    }

    @Bean(destroyMethod = "close")
    public Consumer<String, String> consumer() {
        Consumer<String, String> consumer = new KafkaConsumer<>(props());
        log.info("* Consumer created. *");
        return consumer;
    }

    public Map<String, Object> props() {
        Map<String, Object> props = new HashMap<>();
        props.put(BOOTSTRAP_SERVERS_CONFIG, kafkaConfiguration.getBrokerAddress());
        props.put(GROUP_ID_CONFIG, groupId);
        // Set this property, if auto commit should happen.
        //  props.put(ENABLE_AUTO_COMMIT_CONFIG, "true");
        // Auto commit interval, kafka would commit offset at this interval.
        //  props.put(AUTO_COMMIT_INTERVAL_MS_CONFIG, this.commitIntervalMs);
        // This is how to control number of records being read in each poll
        //  props.put(MAX_PARTITION_FETCH_BYTES_CONFIG, "135");
        // Set this if you want to always read from beginning.
        props.put(AUTO_OFFSET_RESET_CONFIG, offsetReset);
        //   props.put(HEARTBEAT_INTERVAL_MS_CONFIG, "3000");
        //   props.put(SESSION_TIMEOUT_MS_CONFIG, this.timeoutMs);
        props.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return Collections.unmodifiableMap(props);
    }
}
