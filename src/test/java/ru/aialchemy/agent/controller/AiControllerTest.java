package ru.aialchemy.agent.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.aialchemy.agent.models.WordDocContent;
import ru.aialchemy.agent.services.llm.GigaChatSrv;
import ru.aialchemy.agent.services.doc.read.WordDocValidator;

import java.util.List;

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
    private WordDocValidator wordDocValidator;

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

        MockMultipartFile jsonPart = new MockMultipartFile(
                "userRq", "", "application/json", requestBody.getBytes()
        );

        var validResponse = new WordDocContent("test.docx", "Test content", 1, List.of("Test content"), "docx");
        when(wordDocValidator.validate(any())).thenReturn(validResponse);

        // When & Then
        mockMvc.perform(multipart("/api/v1/documents/edit")
                        .file(file)
                        .file(jsonPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
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

        MockMultipartFile jsonPart = new MockMultipartFile(
                "userRq", "", "application/json", requestBody.getBytes()
        );

        var errorResponse = WordDocContent.error("Неподдерживаемый тип файла");
        when(wordDocValidator.validate(any())).thenReturn(errorResponse);

        // When & Then
        mockMvc.perform(multipart("/api/v1/documents/edit")
                        .file(file)
                        .file(jsonPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Неподдерживаемый тип файла"));
    }

}