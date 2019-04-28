package ru.immmus.config;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import ru.immmus.profiles.SparkRDD;

import javax.annotation.PreDestroy;

@SparkRDD
@Configuration
@PropertySource("classpath:spark-rdd.properties")
public class SparkRDDConfig {

    @Value("${spark.app.name}")
    private String appName;
    @Value("${spark.master}")
    private String masterUri;
    @Value("${spark.ui.enabled}")
    private String sparkUi;

    private JavaSparkContext sc;

    @Bean(name = "rddConf")
    public SparkConf conf() {
        SparkConf sparkConf = new SparkConf();
        sparkConf.setAppName(appName);
        sparkConf.setMaster(masterUri);
        sparkConf.set("spark.ui.enabled", sparkUi);
        return sparkConf;
    }

    @Bean(name = "rddSparkContext")
    public JavaSparkContext sc() {
        this.sc = new JavaSparkContext(conf());
        return this.sc;
    }

    @PreDestroy
    private void destroy(){
        if (sc != null) {
            sc.stop();
            sc.close();
        }
    }
}
