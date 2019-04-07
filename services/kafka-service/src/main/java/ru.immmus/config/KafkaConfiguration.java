package ru.immmus.config;

public interface KafkaConfiguration {
       String getTopic();

       String getBrokerAddress();

       String getZookeeperAddress();
}
