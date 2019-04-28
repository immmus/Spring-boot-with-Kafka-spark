package ru.immmus.config;

import kafka.server.KafkaConfig;
import kafka.server.KafkaServerStartable;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.zookeeper.server.NIOServerCnxnFactory;
import org.apache.zookeeper.server.ServerCnxnFactory;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import ru.immmus.profiles.EmbeddedKafka;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.CLIENT_ID_CONFIG;

@Configuration
@EmbeddedKafka
@PropertySource("classpath:kafkaEmbedded.properties")
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
    private int zookeeperPort;
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
        factory.configure(new InetSocketAddress(zookeeperHost, zookeeperPort), 16);
        factory.startup(server);
        return factory;
    }

    @Bean(initMethod = "startup", destroyMethod = "shutdown")
    @DependsOn("zooKeeperServer")
    public KafkaServerStartable kafkaServerStartable() throws IOException {
        File logDir = Files.createTempDirectory("kafka").toFile();
        logDir.deleteOnExit();
        Properties properties = new Properties();
        properties.put(KafkaConfig.ZkConnectProp(), zookeeperAddress);
        properties.put(KafkaConfig.BrokerIdProp(), "1");
        properties.put(KafkaConfig.ListenersProp(),
                String.format("PLAINTEXT://%s:%s", brokerHost, brokerPort));
        properties.put(KafkaConfig.OffsetsTopicReplicationFactorProp(), String.valueOf(1));
        properties.put(KafkaConfig.LogDirProp(), logDir.getAbsolutePath());
        properties.put(KafkaConfig.LogFlushIntervalMessagesProp(), String.valueOf(1));
        return new KafkaServerStartable(new KafkaConfig(properties));
    }

    @Bean(name = "adminClient")
    @DependsOn("kafkaServerStartable")
    public String createTopic() {
        final short replicationFactor = 1;
        try(final AdminClient adminClient = KafkaAdminClient.create(buildDefaultClientConfig())) {
            final NewTopic newTopic = new NewTopic(topic, 1, replicationFactor);
            // Create topic, which is async call.
            CreateTopicsResult topicsResult  = adminClient.createTopics(Collections.singleton(newTopic));

            // Since the call is Async, Lets wait for it to complete.
            topicsResult.values().get(topic).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return topic;
    }

    private Map<String, Object> buildDefaultClientConfig() {
        Map<String, Object> defaultClientConfig = new HashMap<>();
        defaultClientConfig.put(BOOTSTRAP_SERVERS_CONFIG, getBrokerAddress());
        defaultClientConfig.put(CLIENT_ID_CONFIG, "test-consumer-id");
        return defaultClientConfig;
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
