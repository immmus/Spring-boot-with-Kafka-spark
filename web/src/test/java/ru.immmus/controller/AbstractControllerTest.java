package ru.immmus.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.web.servlet.MockMvc;
import ru.immmus.profiles.Spark;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AbstractControllerTest {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    private Environment env;

    boolean isSpark(){
        return env.acceptsProfiles(org.springframework.core.env.Profiles.of(Spark.profile));
    }
}
