package ru.test.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AggregateMessages {
    @JsonProperty("count_with_breakdowns")
    private int countEventsWithBreakdowns;
    @JsonProperty("count_without_breakdowns")
    private int countEventsWithoutBreakdowns;
    @JsonProperty("messages")
    private Map<String, List<Message>> aggregateMap;
}
