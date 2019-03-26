package ru.test.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.test.domain.AggregateMessages;
import ru.test.domain.Messages;
import ru.test.service.MessageService;

@Slf4j
public abstract class AbstractMessageController {
    @Autowired
    private MessageService messageService;

     AggregateMessages aggregate(Messages messages) {
        log.info("Messages received");
        return this.messageService.aggregate(messages);
    }
}
