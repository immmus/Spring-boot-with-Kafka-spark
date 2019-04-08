package ru.immmus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AggregateMessages {
    @JsonProperty("count_with_breakdowns")
    private long countEventsWithBreakdowns;
    @JsonProperty("count_without_breakdowns")
    private long countEventsWithoutBreakdowns;
    @JsonProperty("messages")
    private Map<String, ? extends Iterable<Message>> aggregateMap;
}
