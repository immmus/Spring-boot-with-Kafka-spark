package ru.immmus.service;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.immmus.AbstractMessageService;
import ru.immmus.config.SparkRDDConfig;
import ru.immmus.profiles.Spark;

@ActiveProfiles(Spark.profile)
@SpringBootTest(classes = {SparkMessageService.class, SparkRDDConfig.class})
class SparkMessageServiceTest extends AbstractMessageService { }
