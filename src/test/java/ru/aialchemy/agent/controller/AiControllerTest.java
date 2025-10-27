package ru.aialchemy.agent.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.aialchemy.agent.models.WordDocumentContent;
import ru.aialchemy.agent.service.GigaChatSrv;
import ru.aialchemy.agent.service.ValidateRqSrv;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AiController.class)
class AiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ValidateRqSrv validateRqSrv;

    @MockitoBean
    private GigaChatSrv gigaChatSrv;

    @Test
    void editDocSuccess() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "test content".getBytes()
        );

        // Создаем JSON тело для RequestEntity
        String requestBody = """
        {
            "userQuestion": "test question"
        }
        """;

        var validResponse = new WordDocumentContent("test.docx", "Test content", 1, java.util.List.of("Test content"));
        when(validateRqSrv.validate(any())).thenReturn(validResponse);

        // When & Then
        mockMvc.perform(multipart("/api/v1/documents/edit")
                        .file(file)
                        .content(requestBody.getBytes())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Получен корректный файл для анализа"));
    }

    @Test
    void editDocBadRequest() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.pdf", "application/pdf", "test content".getBytes()
        );

        // Создаем JSON тело для RequestEntity
        String requestBody = """
        {
            "userQuestion": "test question"
        }
        """;

        var errorResponse = WordDocumentContent.error("Неподдерживаемый тип файла");
        when(validateRqSrv.validate(any())).thenReturn(errorResponse);

        // When & Then
        mockMvc.perform(multipart("/api/v1/documents/edit")
                        .file(file)
                        .content(requestBody.getBytes())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Неподдерживаемый тип файла"));
    }


}