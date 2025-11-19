package ru.aialchemy.agent.models.docIn;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record WordDocContent(
        String fileName,
        String content,
        int paragraphCount,
        List<String> paragraphs,
        String errorMessage,
        String fileExtension,
        @JsonProperty("tables")
        List<TableData> tables
) {
    public WordDocContent(String fileName, String content, int paragraphCount, List<String> paragraphs, String fileExtension, List<TableData> tables) {
        this(fileName, content, paragraphCount, paragraphs, null, fileExtension, tables);
    }

    public static WordDocContent error(String errorMessage) {
        return new WordDocContent(null, null, 0, null, errorMessage, null, null);
    }
}
