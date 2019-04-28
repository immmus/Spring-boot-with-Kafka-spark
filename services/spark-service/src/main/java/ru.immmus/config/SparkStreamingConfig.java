package ru.immmus.config;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import ru.immmus.profiles.SparkStreaming;

@Configuration
@SparkStreaming
@PropertySource("classpath:spark-streaming.properties")
public class SparkStreamingConfig {
    @Value("${spark.app.name}")
    private String appName;
    @Value("${spark.master}")
    private String masterUri;
    @Value("${batch.duration.interval.ms}")
    private long duration_interval_ms;
    private JavaStreamingContext jsc;

    @Bean(name = "streamingConf")
    public SparkConf conf() {
        SparkConf sparkConf = new SparkConf();
        sparkConf.setAppName(appName);
        sparkConf.setMaster(masterUri);
        return sparkConf;
    }

    @Bean(name = "streamingCntx")
    public JavaStreamingContext jsc() {
        this.jsc = new JavaStreamingContext(conf(), Duration.apply(duration_interval_ms));
        return this.jsc;
    }
}
