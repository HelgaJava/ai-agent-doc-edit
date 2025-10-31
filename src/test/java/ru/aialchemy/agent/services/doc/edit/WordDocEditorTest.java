package ru.aialchemy.agent.services.doc.edit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import ru.aialchemy.agent.models.WordDocContent;
import ru.aialchemy.agent.models.WordDocReplace;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.aialchemy.agent.HelperTest.getFileContentFromResources;

@ExtendWith(MockitoExtension.class)
class WordDocEditorTest {

    @InjectMocks
    private WordDocEditor wordDocEditor;

    @TempDir
    private Path tempDir;

    @Test
    void rewriteFileDocxSuccess() throws IOException, URISyntaxException {
        WordDocContent docContent = new WordDocContent(
                "document.docx", "Content", 1, List.of("Content"), "docx"
        );

        WordDocReplace wordDocReplace = new WordDocReplace("test content",
                "test content new");

        byte[] fileContent = getFileContentFromResources("TestWord.docx");

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", fileContent);

        String filePath = tempDir.resolve(docContent.fileName()).toString();
        String result = wordDocEditor.rewriteFile(file, wordDocReplace, tempDir + "\\" + docContent.fileName(), docContent.fileExtension());

        assertEquals("Файл успешно сохранен по пути " + filePath, result);
        assertTrue(Files.exists(Path.of(filePath)));
    }


    @Test
    void rewriteFileDocSuccess() throws IOException, URISyntaxException {
        WordDocContent docContent = new WordDocContent(
                "document.doc", "DOC Content", 1, List.of("DOC Content"), "doc"
        );
        WordDocReplace wordDocReplace = new WordDocReplace("test content",
                "test content new");

        byte[] fileContent = getFileContentFromResources("TestWord.doc");

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", fileContent);
        String filePath = tempDir.resolve("document.doc").toString();
        String result = wordDocEditor.rewriteFile(file, wordDocReplace, tempDir + "\\" + docContent.fileName(), docContent.fileExtension());

        assertEquals("Файл успешно сохранен по пути " + filePath, result);
        assertTrue(Files.exists(Path.of(filePath)));

    }

    @Test
    void rewriteFileInvalidPathError() throws IOException, URISyntaxException {
        WordDocContent docContent = new WordDocContent(
                "test.docx", "Test content", 1, List.of("Test content"), "docx"
        );

        byte[] fileContent = getFileContentFromResources("TestWord.docx");

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
               fileContent
        );
        WordDocReplace wordDocReplace = new WordDocReplace("", "");

        String filePath = "/invalidPath/test.docx";

        String result = wordDocEditor.rewriteFile(file, wordDocReplace, filePath, docContent.fileExtension());

        assertTrue(result.startsWith("Не удалось сохранить файл:"));
        assertFalse(Files.exists(Path.of(filePath)));
    }

    @Test
    void rewriteFileInvalidFileNameError() throws IOException, URISyntaxException {
        WordDocContent docContent = new WordDocContent(
                "invalid<?>.docx", "Test content", 1, List.of("Test content"), "docx"
        );
        byte[] fileContent = getFileContentFromResources("TestWord.docx");
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", fileContent
        );
        WordDocReplace wordDocReplace = new WordDocReplace("", "");

        String result = wordDocEditor.rewriteFile(file, wordDocReplace, tempDir + "/" + docContent.fileName(), docContent.fileExtension());

        assertTrue(result.startsWith("Не удалось сохранить файл:"));
    }

}