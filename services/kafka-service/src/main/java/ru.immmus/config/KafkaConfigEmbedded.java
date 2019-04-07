package ru.immmus.config;

import kafka.admin.ConsumerGroupCommand;
import kafka.admin.TopicCommand;
import kafka.server.KafkaConfig;
import kafka.server.KafkaServerStartable;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.zookeeper.server.NIOServerCnxnFactory;
import org.apache.zookeeper.server.ServerCnxnFactory;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import ru.immmus.profiles.EmbeddedKafka;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.Properties;

@Configuration
@EmbeddedKafka
@PropertySource("classpath:kafkaEmbedded.properties")
@EnableAutoConfiguration
public class KafkaConfigEmbedded implements KafkaConfiguration {

    @Value("${kafka.topic}")
    private String topic;
    @Value("${kafka.host}")
    private String brokerHost;
    @Value("${kafka.port}")
    private String brokerPort;
    @Value("#{'${kafka.host}'+':'+'${kafka.port}'}")
    private String brokerAddress;
    @Value("${zookeeper.host}")
    private String zookeeperHost;
    @Value("${zookeeper.port}")
    private int zookeeperPost;
    @Value("#{'${zookeeper.host}'+':'+'${zookeeper.port}'}")
    private String zookeeperAddress;

    @Bean(destroyMethod = "shutdown")
    public ServerCnxnFactory zooKeeperServer() throws IOException, InterruptedException {
        File snapshotDir = Files.createTempDirectory("zookeeper-snapshot").toFile();
        File logDir = Files.createTempDirectory("zookeeper-logs").toFile();
        snapshotDir.deleteOnExit();
        logDir.deleteOnExit();

        int tickTime = 500;
        ZooKeeperServer server = new ZooKeeperServer(snapshotDir, logDir, tickTime);
        ServerCnxnFactory factory = NIOServerCnxnFactory.createFactory();
        factory.configure(new InetSocketAddress(zookeeperHost, zookeeperPost), 16);
        factory.startup(server);
        return factory;
    }

    @Bean(initMethod = "startup", destroyMethod = "shutdown")
    @DependsOn("zooKeeperServer")
    public KafkaServerStartable kafkaServerStartable() throws IOException {
        File logDir = Files.createTempDirectory("kafka").toFile();
        logDir.deleteOnExit();
        Properties properties = new Properties();
        properties.put("zookeeper.connect", zookeeperAddress);
        properties.put("broker.id", "1");
        properties.put("host.name", brokerHost);
        properties.put("port", brokerPort);
        properties.setProperty("log.dir", logDir.getAbsolutePath());
        properties.setProperty("log.flush.interval.messages", String.valueOf(1));

        return new KafkaServerStartable(new KafkaConfig(properties));
    }

    @Bean(name = "topic")
    @DependsOn("kafkaServerStartable")
    public String crateTopic() {
        String[] arguments = new String[9];
        arguments[0] = "--create";
        arguments[1] = "--zookeeper";
        arguments[2] = zookeeperAddress;
        arguments[3] = "--replication-factor";
        arguments[4] = "1";
        arguments[5] = "--partitions";
        arguments[6] = "1";
        arguments[7] = "--topic";
        arguments[8] = topic;
        TopicCommand.main(arguments);
        return topic;
    }

    @Override
    public String getTopic() {
        return this.topic;
    }

    @Override
    public String getBrokerAddress() {
        return this.brokerAddress;
    }

    @Override
    public String getZookeeperAddress() {
        return this.zookeeperAddress;
    }
}
