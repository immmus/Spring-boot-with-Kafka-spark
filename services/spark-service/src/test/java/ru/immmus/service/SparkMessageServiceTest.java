package ru.immmus.service;

import org.apache.spark.HashPartitioner;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import ru.immmus.AbstractMessageService;
import ru.immmus.config.SparkRDDConfig;
import ru.immmus.profiles.SparkRDD;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ActiveProfiles(SparkRDD.profile)
@TestPropertySource("classpath:spark-rdd-test.properties")
@SpringBootTest(classes = {SparkMessageService.class, SparkRDDConfig.class})
class SparkMessageServiceTest extends AbstractMessageService {

    @Autowired
    private JavaSparkContext sc;

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
