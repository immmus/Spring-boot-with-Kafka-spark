package ru.immmus.service;

import org.apache.spark.HashPartitioner;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.immmus.AbstractMessageService;
import ru.immmus.config.SparkRDDConfig;
import ru.immmus.profiles.SparkRDD;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ActiveProfiles(SparkRDD.profile)
@TestPropertySource("classpath:spark-rdd-test.properties")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {SparkMessageService.class, SparkRDDConfig.class})
class SparkMessageServiceTest extends AbstractMessageService {

    @Autowired
    private JavaSparkContext sc;

    @Test
    void orderedAndSample() {
        JavaRDD<Integer> parallelize = sc.parallelize(Arrays.asList(1, 4, 3));
        List<Integer> integers1 = parallelize.takeOrdered(2);
        System.out.println("Ordered:");
        integers1.forEach(System.out::println);
        List<Integer> integers = parallelize.takeSample(false, 4);
        System.out.println("Sample:");
        integers.forEach(System.out::println);
    }

    @Test
    void hashPartitionTest() {
        JavaPairRDD<Integer, String> pairRDD = getIntegerStringJavaPairRDD();
        JavaPairRDD<Integer, String> hashPartitioned = pairRDD.partitionBy(new HashPartitioner(2));
        JavaRDD<String> stringJavaRDD = hashPartitioned.mapPartitionsWithIndex(
                (idx, tupleIterator) -> {
                    List<String> list = new ArrayList<>();
                    while (tupleIterator.hasNext()) {
                        list.add(String.format("Part num: %d, key: %d", idx, tupleIterator.next()._1));
                    }
                    return list.iterator();
                }, true);
        System.out.println(stringJavaRDD.collect());
    }

    @Test
    void foldByKeyTest() {
        JavaRDD<String> stringRDD = sc.parallelize(Arrays.asList("Hello Spark", "Hello Java"));
        JavaPairRDD<String, Integer> flatMapToPair = stringRDD.flatMapToPair
                (
                        s -> Arrays.stream(s.split(" "))
                                .map(token -> Tuple2.apply(token, 1))
                                .collect(Collectors.toList())
                                .iterator()
                );
        Map<String, Integer> map = flatMapToPair.foldByKey(1, Integer::sum).collectAsMap();
        System.out.println(map);
    }

    @Test
    void aggregateByKey() {
        JavaPairRDD<String, String> pairRDD = sc.parallelizePairs(Arrays.asList(
                Tuple2.apply("key1", "Agan"),
                Tuple2.apply("key2", "Bgan"),
                Tuple2.apply("key3", "Agan"),
                Tuple2.apply("key2", "Cgan"),
                Tuple2.apply("key1", "Agan"),
                Tuple2.apply("key1", "Rgan")
        ), 3);
        JavaPairRDD<String, Integer> rdd = pairRDD.aggregateByKey(0,
                (v1, v2) -> v2.startsWith("A") ? v1 + 1 : v1, Integer::sum);
        System.out.println(rdd.collectAsMap());
    }

    private JavaPairRDD<Integer, String> getIntegerStringJavaPairRDD() {
        return sc.parallelizePairs(
                Arrays.asList(
                        Tuple2.apply(1, "A"),
                        Tuple2.apply(2, "B"),
                        Tuple2.apply(3, "C"),
                        Tuple2.apply(4, "D"),
                        Tuple2.apply(5, "E"),
                        Tuple2.apply(6, "F")
                ), 3);
    }
}
