package ru.aialchemy.agent.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record WordDocumentContent(
        String fileName,
        String content,
        int paragraphCount,
        List<String> paragraphs,
        String errorMessage
) {
    public WordDocumentContent(String fileName, String content, int paragraphCount, List<String> paragraphs) {
        this(fileName, content, paragraphCount, paragraphs, null);
    }

    public static WordDocumentContent error(String fileName, String errorMessage) {
        return new WordDocumentContent(fileName, null, 0, null, errorMessage);
    }

    public static WordDocumentContent error(String errorMessage) {
        return new WordDocumentContent(null, null, 0, null, errorMessage);
    }
}
