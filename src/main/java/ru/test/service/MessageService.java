package ru.test.service;

import ru.test.domain.AggregateMessages;
import ru.test.domain.Messages;

public interface MessageService {
    AggregateMessages aggregate(Messages messages);
}
