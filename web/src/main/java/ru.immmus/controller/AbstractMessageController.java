package ru.immmus.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.immmus.AggregateMessages;
import ru.immmus.MessageService;
import ru.immmus.Messages;

@Slf4j
public abstract class AbstractMessageController {
    @Autowired
    private MessageService messageService;

     AggregateMessages aggregate(Messages messages) {
        log.info("Messages received");
        return this.messageService.aggregate(messages);
    }
}
