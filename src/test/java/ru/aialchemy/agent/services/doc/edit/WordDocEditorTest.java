package ru.aialchemy.agent.services.doc.edit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import ru.aialchemy.agent.models.docIn.WordDocContent;
import ru.aialchemy.agent.models.docRepl.WordDocReplaces;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.aialchemy.agent.HelperTest.*;

@ExtendWith(MockitoExtension.class)
class WordDocEditorTest {

    @InjectMocks
    private WordDocEditor wordDocEditor;

    @TempDir
    private Path tempDir;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void rewriteFileDocxSuccess() throws IOException, URISyntaxException {
        WordDocContent docContent = createDocContent();

        String testStr = "{\n" +
                "      \"wordDocReplaces\": [\n" +
                "        {\n" +
                "          \"searchText\": \"112\",\n" +
                "          \"replacementText\": \"34\",\n" +
                "          \"isTableValue\": true\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "}";


        WordDocReplaces wordDocReplaces = MAPPER.readValue(testStr, WordDocReplaces.class);

        byte[] fileContent = getFileContentFromResources("demoReport.docx");

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", fileContent);

        String filePath = tempDir.resolve(docContent.fileName()).toString();
        String result = wordDocEditor.rewriteFile(file, wordDocReplaces.wordDocReplaces(), filePath, docContent.fileExtension());

        assertEquals("Файл успешно сохранен по пути " + filePath, result);
        assertTrue(Files.exists(Path.of(filePath)));
    }

    @Test
    void rewriteFileInvalidPathError() throws IOException, URISyntaxException, JSONException {
        byte[] fileContent = getFileContentFromResources("TestWord.docx");

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                fileContent
        );

        WordDocReplaces wordDocReplaces = MAPPER.readValue(createEmptyJsonStr(), WordDocReplaces.class);
        String filePath = "/invalidPath/test.docx";
        String result = wordDocEditor.rewriteFile(file, wordDocReplaces.wordDocReplaces(), filePath, createDocContent().fileExtension());

        assertTrue(result.startsWith("Не удалось сохранить файл:"));
        assertFalse(Files.exists(Path.of(filePath)));
    }

    @Test
    void rewriteFileInvalidFileNameError() throws IOException, URISyntaxException, JSONException {

        WordDocContent docContent = new WordDocContent(
                "invalid<?>.docx", "Content", 1, List.of("Content"), "docx", null
        );
        byte[] fileContent = getFileContentFromResources("TestWord.docx");
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", fileContent
        );

        WordDocReplaces wordDocReplaces = MAPPER.readValue(createEmptyJsonStr(), WordDocReplaces.class);
        String result = wordDocEditor.rewriteFile(file, wordDocReplaces.wordDocReplaces(), tempDir + "/" + docContent.fileName(), docContent.fileExtension());

        assertTrue(result.startsWith("Не удалось сохранить файл:"));
    }

}