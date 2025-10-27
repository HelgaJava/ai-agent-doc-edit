package ru.aialchemy.agent.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.aialchemy.agent.models.WordDocumentContent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidateRqSrvTest {
    @Mock
    private WordDocSrv wordDocSrv;

    @InjectMocks
    private ValidateRqSrv validateRqSrv;

    @Test
    void validateDocxSuccess() {
        // Given
        MultipartFile file = new MockMultipartFile(
                "test.docx", "test.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "test content".getBytes()
        );

        var expectedResult = new WordDocumentContent("test.docx", "Test content", 1, java.util.List.of("Test content"));
        when(wordDocSrv.readWordDocument(any(MultipartFile.class), eq("docx"), eq("test.docx")))
                .thenReturn(expectedResult);

        // When
        var result = validateRqSrv.validate(file);

        // Then
        assertNotNull(result);
        assertEquals("test.docx", result.fileName());
        assertNull(result.errorMessage());
    }

    @Test
    void validateDocSuccess() {
        // Given
        MultipartFile file = new MockMultipartFile(
                "test.doc", "test.doc", "application/msword", "test content".getBytes()
        );

        var expectedResult = new WordDocumentContent("test.doc", "Test content", 1, java.util.List.of("Test content"));
        when(wordDocSrv.readWordDocument(any(MultipartFile.class), eq("doc"), eq("test.doc")))
                .thenReturn(expectedResult);

        // When
        var result = validateRqSrv.validate(file);

        // Then
        assertNotNull(result);
        assertEquals("test.doc", result.fileName());
        assertNull(result.errorMessage());
    }

    @Test
    void validateUnsupportedType() {
        // Given
        MultipartFile file = new MockMultipartFile(
                "test.pdf", "test.pdf", "application/pdf", "test content".getBytes()
        );

        // When
        var result = validateRqSrv.validate(file);

        // Then
        assertNotNull(result);
        assertNotNull(result.errorMessage());
        assertTrue(result.errorMessage().contains("Неподдерживаемый тип файла"));
    }

    @Test
    void validateNullFileName() {
        // Given
        MultipartFile file = new MockMultipartFile(
                "test", null, "application/pdf", "test content".getBytes()
        );

        // When
        var result = validateRqSrv.validate(file);

        // Then
        assertNotNull(result);
        assertNotNull(result.errorMessage());
    }

    @Test
    void validateEmptyFile() {
        // Given
        MultipartFile file = new MockMultipartFile(
                "empty.docx", "empty.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", new byte[0]
        );

        // When
        var result = validateRqSrv.validate(file);

        // Then
        assertNotNull(result);
        assertNotNull(result.errorMessage());
        assertTrue(result.errorMessage().contains("пуст"));
    }

    @Test
    void validateWordDocSrvThrowsException() {
        // Given
        MultipartFile file = new MockMultipartFile(
                "test.docx", "test.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "test content".getBytes()
        );

        when(wordDocSrv.readWordDocument(any(MultipartFile.class), eq("docx"), eq("test.docx")))
                .thenThrow(new RuntimeException("Processing failed"));

        // When
        var result = validateRqSrv.validate(file);

        // Then
        assertNotNull(result);
        assertNotNull(result.errorMessage());
        assertTrue(result.errorMessage().contains("Внутренняя ошибка сервера"));
    }
}