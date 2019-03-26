package ru.test.controllers;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.util.NestedServletException;
import ru.test.domain.AggregateMessages;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.test.TestUtils.readFromJsonResultActions;

class MessageControllerTest extends AbstractControllerTest {
    private static final String REST_URL = MessageController.REST_URL + '/';
    private Map<String, String> jsonFilesMap;

    private Pair<String, String> apply(Path path) {
        try {
            byte[] bytes = Files.readAllBytes(path);
            String json = new String(bytes, StandardCharsets.UTF_8);
            String fileName = path.toFile().getName();
            return Pair.of(fileName, json);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @BeforeEach
    void setUp() throws IOException {
        Path path = Paths.get("src/test/resources/");
        this.jsonFilesMap = Files.walk(path)
                .filter(file -> file.toFile().getName().matches(".*\\.json"))
                .map(this::apply)
                .collect(toMap(Pair::getFirst, Pair::getSecond));
    }

    /**
     * input 100k messages and output validation
     */
    @Test
    void aggregateMessagesTest() throws Exception {
        ResultActions resultActions = mockMvc.perform(post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.jsonFilesMap.get("requestBody.json")))
                .andExpect(status().isOk());

        AggregateMessages aggregateMessages = readFromJsonResultActions(resultActions, AggregateMessages.class);
        Assertions.assertThat(aggregateMessages).isNotNull();
        Assertions.assertThat(aggregateMessages.getCountEventsWithBreakdowns()).isEqualTo(50_000);
        Assertions.assertThat(aggregateMessages.getCountEventsWithoutBreakdowns()).isEqualTo(50_000);
        Assertions.assertThat(aggregateMessages.getAggregateMap().size()).isEqualTo(3);
    }

    /**
     * test for empty json objects
     * */
    @Test
    void emptyJsonTest() throws Exception {
        mockMvc.perform(post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.jsonFilesMap.get("messagesEmpty.json")))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void notNullTest() {
        assertThrows(NestedServletException.class, () ->
                mockMvc.perform(post(REST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.jsonFilesMap.get("messagesNullable.json"))));
    }
}
