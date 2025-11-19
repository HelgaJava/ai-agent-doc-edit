package ru.aialchemy.agent.models.docRepl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record WordDocReplace(
        @JsonProperty("searchText")
        String searchText,
        @JsonProperty("replacementText")
        String replacementText,
        @JsonProperty("isTableValue")
        boolean isTableValue,
        @JsonProperty("rowIndex")
        int indexRow,
        @JsonProperty("colIndex")
        int indexColumn
) {
}
