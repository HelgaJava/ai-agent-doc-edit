package ru.aialchemy.agent.services.doc.edit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aialchemy.agent.models.WordDocContent;

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

        String filePath = tempDir.resolve(docContent.fileName()).toString();
        String result = wordDocSaver.rewriteFile(docContent, tempDir.toString());

        assertEquals("Файл успешно сохранен по пути " + filePath, result);
        assertTrue(Files.exists(Path.of(filePath)));
    }


    @Test
    void rewriteFileDocSuccess() {
        WordDocContent docContent = new WordDocContent(
                "document.doc", "DOC Content", 1, List.of("DOC Content"), "doc"
        );
        String filePath = tempDir.resolve("document.doc").toString();
        String result = wordDocSaver.rewriteFile(docContent, tempDir.toString());

        assertEquals("Файл успешно сохранен по пути " + filePath, result);
        assertTrue(Files.exists(Path.of(filePath)));

    }

    @Test
    void rewriteFileInvalidPathError() {
        WordDocContent docContent = new WordDocContent(
                "test.docx", "Test content", 1, List.of("Test content"), "docx"
        );
        String filePath = "/invalidPath/test.docx";
        String result = wordDocSaver.rewriteFile(docContent, "/invalidPath");

        assertTrue(result.startsWith("Не удалось сохранить файл:"));
        assertFalse(Files.exists(Path.of(filePath)));
    }

    @Test
    void rewriteFileInvalidFileNameError() {
        WordDocContent docContent = new WordDocContent(
                "invalid<?>.docx", "Test content", 1, List.of("Test content"), "docx"
        );

        String result = wordDocSaver.rewriteFile(docContent, tempDir.toString());

        assertTrue(result.startsWith("Не удалось сохранить файл:"));
    }

}