package ru.aialchemy.agent.models.docIn;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public record TableData(
        @JsonProperty("tableIndex")
        int tableIndex,

        @JsonProperty("cells")
        List<CellData> cells
) {
    public TableData(int tableIndex) {
        this(tableIndex, new ArrayList<>());
    }
}
