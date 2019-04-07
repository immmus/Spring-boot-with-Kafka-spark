package ru.immmus;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.immmus.MessagesTestData.messageList;

public abstract class AbstractMessageService {
    @Autowired
    private MessageService service;

    @Test
    void aggregate() {
        Messages messages = new Messages(messageList);
        AggregateMessages aggregate = service.aggregate(messages);
        assertThat(aggregate.getAggregateMap().size()).isEqualTo(2);
        assertThat(aggregate.getCountEventsWithBreakdowns()).isEqualTo(2);
        assertThat(aggregate.getCountEventsWithoutBreakdowns()).isEqualTo(2);
    }
}
