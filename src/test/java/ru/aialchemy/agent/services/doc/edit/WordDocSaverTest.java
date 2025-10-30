package ru.aialchemy.agent.services.doc.edit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import ru.aialchemy.agent.models.WordDocContent;
import ru.aialchemy.agent.models.WordDocReplace;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WordDocSaverTest {

    @InjectMocks
    private WordDocSaver wordDocSaver;

    @TempDir
    private Path tempDir;

    @Test
    void rewriteFileDocxSuccess() {
        WordDocContent docContent = new WordDocContent(
                "document.docx", "Content", 1, List.of("Content"), "docx"
        );

        WordDocReplace wordDocReplace = new WordDocReplace("Маша придя домой поздним вечером быстро снила пальто бросила сумочку на деван",
                "Маша, придя домой поздним вечером, быстро сняла пальто бросила сумочку на диван");

        String filePath = tempDir.resolve(docContent.fileName()).toString();
        String result = wordDocSaver.rewriteFile(null, docContent, wordDocReplace, tempDir.toString());

        assertEquals("Файл успешно сохранен по пути " + filePath, result);
        assertTrue(Files.exists(Path.of(filePath)));
    }


    @Test
    void rewriteFileDocSuccess() {
        WordDocContent docContent = new WordDocContent(
                "document.doc", "DOC Content", 1, List.of("DOC Content"), "doc"
        );
        WordDocReplace wordDocReplace = new WordDocReplace("Маша придя домой поздним вечером быстро снила пальто бросила сумочку на деван",
                "Маша, придя домой поздним вечером, быстро сняла пальто бросила сумочку на диван");
        String filePath = tempDir.resolve("document.doc").toString();
        String result = wordDocSaver.rewriteFile(null, docContent, wordDocReplace, tempDir.toString());

        assertEquals("Файл успешно сохранен по пути " + filePath, result);
        assertTrue(Files.exists(Path.of(filePath)));

    }

    @Test
    void rewriteFileInvalidPathError() {
        WordDocContent docContent = new WordDocContent(
                "test.docx", "Test content", 1, List.of("Test content"), "docx"
        );
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "test content".getBytes()
        );
        WordDocReplace wordDocReplace = new WordDocReplace("", "");

        String filePath = "/invalidPath/test.docx";

        String result = wordDocSaver.rewriteFile(file, docContent, wordDocReplace, "/invalidPath");

        assertTrue(result.startsWith("Не удалось сохранить файл:"));
        assertFalse(Files.exists(Path.of(filePath)));
    }

    @Test
    void rewriteFileInvalidFileNameError() {
        WordDocContent docContent = new WordDocContent(
                "invalid<?>.docx", "Test content", 1, List.of("Test content"), "docx"
        );
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "test content".getBytes()
        );
        WordDocReplace wordDocReplace = new WordDocReplace("", "");

        String result = wordDocSaver.rewriteFile(file, docContent, wordDocReplace, tempDir.toString());

        assertTrue(result.startsWith("Не удалось сохранить файл:"));
    }

}