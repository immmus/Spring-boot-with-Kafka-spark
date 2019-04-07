package ru.immmus.config;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import ru.immmus.profiles.Spark;

import javax.annotation.PreDestroy;

@Spark
@Configuration
@PropertySource("classpath:spark-rdd.properties")
public class SparkRDDConfig {

    @Value("${spark.app.name}")
    private String appName;
    @Value("${spark.master}")
    private String masterUri;

    private JavaSparkContext sc;
    @Bean
    public SparkConf conf() {
        return new SparkConf().setAppName(appName).setMaster(masterUri);
    }

    @Bean
    public JavaSparkContext sc() {
        this.sc = new JavaSparkContext(conf());
        return sc;
    }

    @PreDestroy
    private void destroy(){
        if (sc != null) {
            sc.stop();
            sc.close();
        }
    }
}
