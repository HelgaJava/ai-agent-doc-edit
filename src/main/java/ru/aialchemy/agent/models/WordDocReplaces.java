package ru.aialchemy.agent.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record WordDocReplaces(
        @JsonProperty("wordDocReplaces")
        List<WordDocReplace> wordDocReplaces
) {
}
