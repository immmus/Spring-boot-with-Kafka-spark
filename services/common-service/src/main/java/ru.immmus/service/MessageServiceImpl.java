package ru.immmus.service;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.immmus.AggregateMessages;
import ru.immmus.Message;
import ru.immmus.MessageService;
import ru.immmus.Messages;
import ru.immmus.profiles.Common;

import java.util.*;

@Common
@Service("messageService")
public class MessageServiceImpl implements MessageService {

  /**
     * @param messages - json inbox
     * @return aggregated by message location, with the estimated number of breakdowns
     */
    public AggregateMessages aggregate(Messages messages) {
        Assert.notNull(messages, "This object is null");

        List<Message> mgs = messages.getMessages();
        Map<String, List<Message>> aggregateMap = new HashMap<>();
        int count = 0;
        for (Message mg : mgs) {
            if ("Nan".equals(mg.getIdDetected())) count++;
            String idLocation = mg.getIdLocation();
            List<Message> list = aggregateMap.computeIfAbsent(idLocation, that -> new ArrayList<>());
            list.add(mg);
        }
        return AggregateMessages.builder()
                .countEventsWithBreakdowns(count)
                .countEventsWithoutBreakdowns(mgs.size() - count)
                .aggregateMap(aggregateMap)
                .build();
    }
}
