package ru.aialchemy.agent.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record WordDocContent(
        String fileName,
        String content,
        int paragraphCount,
        List<String> paragraphs,
        String errorMessage,
        String fileExtension
) {
    public WordDocContent(String fileName, String content, int paragraphCount, List<String> paragraphs, String fileExtension) {
        this(fileName, content, paragraphCount, paragraphs, null, fileExtension);
    }

    public static WordDocContent error(String fileName, String errorMessage) {
        return new WordDocContent(fileName, null, 0, null, errorMessage, null);
    }

    public static WordDocContent error(String errorMessage) {
        return new WordDocContent(null, null, 0, null, errorMessage, null);
    }
}
