package ru.immmus.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.*;

@Slf4j
@Configuration
@PropertySource("classpath:kafkaProducer.properties")
public class ProducerConfig {
    private final KafkaConfiguration kafkaConfiguration;
    private final static String SERIALIZER = "org.apache.kafka.common.serialization.StringSerializer";

    @Value("${kafka.producer.acks}")
    private String acks;
    @Value("${kafka.producer.retries}")
    private int retries;
    @Value("${kafka.producer.batchSizeMessages}")
    private int batchSize;
    @Value("${kafka.producer.lingerMs}")
    private int lingerMs;

    @Autowired
    public ProducerConfig(KafkaConfiguration kafkaConfiguration) {
        this.kafkaConfiguration = kafkaConfiguration;
    }

    @Bean(destroyMethod = "close")
    public Producer<String, String> producer() {
        Producer<String, String> producer = new KafkaProducer<>(props());
        log.info("* Producer created. *");
        return producer;
    }

    public Map<String, Object> props() {
        Map<String, Object> props = new HashMap<>();
        props.put(BOOTSTRAP_SERVERS_CONFIG, kafkaConfiguration.getBrokerAddress());
        props.put(ACKS_CONFIG, acks);
        props.put(RETRIES_CONFIG, retries);
        // Controls how much bytes sender would wait to batch up before publishing to Kafka.
        props.put(BATCH_SIZE_CONFIG, batchSize);
        props.put(LINGER_MS_CONFIG, lingerMs);
        props.put(KEY_SERIALIZER_CLASS_CONFIG, SERIALIZER);
        props.put(VALUE_SERIALIZER_CLASS_CONFIG, SERIALIZER);
        return Collections.unmodifiableMap(props);
    }

/*    @Bean
    public ThreadPoolTaskExecutor productTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor =  new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(20);
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        return taskExecutor;
    }*/
}
