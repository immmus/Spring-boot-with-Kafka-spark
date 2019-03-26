package ru.test.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.test.domain.AggregateMessages;
import ru.test.domain.Messages;

@RestController
@RequestMapping(value = MessageController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class MessageController extends AbstractMessageController {
     static final String REST_URL ="/api/messages";

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AggregateMessages> aggregateMessages(@RequestBody Messages messages) {
        AggregateMessages aggregate = super.aggregate(messages);
        return ResponseEntity.ok(aggregate);
    }
}
