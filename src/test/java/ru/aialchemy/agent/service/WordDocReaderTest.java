package ru.aialchemy.agent.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.aialchemy.agent.services.doc.read.WordDocReader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class WordDocReaderTest {
    @InjectMocks
    private WordDocReader wordDocReader;

    @Test
    void readDocxCorrectContent() throws Exception {
        // Given
        byte[] fileContent = getFileContentFromResources("TestWord.docx");
        MultipartFile file = new MockMultipartFile(
                "test.docx", "test.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", fileContent
        );

        // When
        var result = wordDocReader.readWordDocument(file, "docx", "test.docx");

        // Then
        assertNotNull(result);
        assertEquals("test.docx", result.fileName());
        assertNotNull(result.content());
        assertFalse(result.content().isEmpty());
        assertTrue(result.paragraphCount() > 0);
        assertFalse(result.paragraphs().isEmpty());
        assertNull(result.errorMessage());
    }

    @Test
    void readDocCorrectContent() throws Exception {
        // Given
        byte[] fileContent = getFileContentFromResources("TestWord.doc");
        MultipartFile file = new MockMultipartFile(
                "test.doc", "test.doc", "application/msword", fileContent
        );

        // When
        var result = wordDocReader.readWordDocument(file, "doc", "test.doc");

        // Then
        assertNotNull(result);
        assertEquals("test.doc", result.fileName());
        assertNotNull(result.content());
        assertFalse(result.content().isEmpty());
        assertTrue(result.paragraphCount() > 0);
        assertFalse(result.paragraphs().isEmpty());
        assertNull(result.errorMessage());
    }

    @Test
    void readDocWithInvalidExtension() {
        // Given
        MultipartFile file = new MockMultipartFile(
                "test.pdf", "test.pdf", "application/pdf", "test content".getBytes()
        );

        // When & Then
        assertThrows(RuntimeException.class, () ->
                wordDocReader.readWordDocument(file, "pdf", "test.pdf")
        );
    }

    @Test
    void readDocEmpty() {
        // Given
        MultipartFile file = new MockMultipartFile(
                "empty.docx", "empty.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", new byte[0]
        );

        // When & Then
        assertThrows(RuntimeException.class, () ->
                wordDocReader.readWordDocument(file, "docx", "empty.docx")
        );
    }

    private byte[] getFileContentFromResources(String fileName) throws IOException, URISyntaxException {
        var resource = getClass().getClassLoader().getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("File not found in resources: " + fileName);
        }
        return Files.readAllBytes(Paths.get(resource.toURI()));
    }
}
