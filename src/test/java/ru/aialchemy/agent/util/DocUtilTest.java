package ru.aialchemy.agent.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DocUtilTest {

    @Test
    void getFileExtensionDocx() {
        // Given
        String fileName = "document.docx";

        // When
        String result = DocUtil.getFileExtension(fileName);

        // Then
        assertEquals("docx", result);
    }

    @Test
    void getFileExtensionDoc() {
        // Given
        String fileName = "document.doc";

        // When
        String result = DocUtil.getFileExtension(fileName);

        // Then
        assertEquals("doc", result);
    }

    @Test
    void getFileExtensionLowerCase() {
        // Given
        String fileName = "document.DOCX";

        // When
        String result = DocUtil.getFileExtension(fileName);

        // Then
        assertEquals("docx", result);
    }

    @Test
    void getFileExtensionNull() {
        // Given
        String fileName = "document";

        // When
        String result = DocUtil.getFileExtension(fileName);

        // Then
        assertNull(result);
    }

    @Test
    void getFileExtensionNullName() {
        // When
        String result = DocUtil.getFileExtension(null);

        // Then
        assertNull(result);
    }
}