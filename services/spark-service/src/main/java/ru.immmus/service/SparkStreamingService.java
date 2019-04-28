package ru.immmus.service;

import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.immmus.AggregateMessages;
import ru.immmus.MessageService;
import ru.immmus.Messages;
import ru.immmus.profiles.SparkStreaming;

@Service
@SparkStreaming
public class SparkStreamingService implements MessageService {
    private final JavaStreamingContext jsc;



    @Autowired
    public SparkStreamingService(@Qualifier(value = "streamingCntx") JavaStreamingContext jsc) {
        this.jsc = jsc;
    }

    @Override
    public AggregateMessages aggregate(Messages messages) {
     /*   KafkaUtils.createDirectStream(jsc, LocationStrategies.PreferConsistent(),
              ConsumerStrategies.<String, Object>Subscribe(
                      Collections.singleton("my-stream"),
                      Collections.unmodifiableMap()));*/
        return null;
    }
}
