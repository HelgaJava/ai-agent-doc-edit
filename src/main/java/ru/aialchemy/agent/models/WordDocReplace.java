package ru.aialchemy.agent.models;

public record WordDocReplace(
        String searchText,
        String replacementText,
        boolean caseSensitive
) {
}
