package ru.aialchemy.agent.models.docIn;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CellData(
        @JsonProperty("rowIndex")
        int rowIndex,

        @JsonProperty("colIndex")
        int colIndex,

        @JsonProperty("text")
        String text
) {

}
