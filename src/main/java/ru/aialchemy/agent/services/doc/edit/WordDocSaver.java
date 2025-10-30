package ru.aialchemy.agent.services.doc.edit;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.aialchemy.agent.models.WordDocContent;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class WordDocSaver {
    private static final Logger LOG = LoggerFactory.getLogger(WordDocSaver.class);
    private static final String SUCCESS_MESSAGE = "Файл успешно сохранен по пути ";
    private static final String ERROR_MESSAGE = "Не удалось сохранить файл: ";

    /**
     * Перезаписывает файл на основе модифицированного WordDocContent
     */
    public String rewriteFile(WordDocContent docContent, String folder) {
        var filePath = folder + "/" + docContent.fileName();
        if (docContent.fileExtension().equalsIgnoreCase("docx")) {
            return rewriteDocxFile(docContent, filePath);
        }
        return rewriteDocFile(docContent, filePath, folder);
    }

    private String rewriteDocxFile(WordDocContent docContent, String filePath) {
        try (XWPFDocument document = new XWPFDocument();
             FileOutputStream out = new FileOutputStream(filePath)) {
            // Создаем директорию если её нет
            createFolder(filePath);
            // Воссоздаем параграфы из editedContent
            for (String paragraphText : docContent.paragraphs()) {
                XWPFParagraph paragraph = document.createParagraph();
                XWPFRun run = paragraph.createRun();
                run.setText(paragraphText);
            }

            document.write(out);
        } catch (IOException e) {
            LOG.error("Не удалось сохранить файл", e);
            return ERROR_MESSAGE + e.getMessage();
        }

        LOG.info("DOCX файл перезаписан: {}", filePath);
        return SUCCESS_MESSAGE + filePath;
    }

    private String rewriteDocFile(WordDocContent docContent, String filePath, String folder) {
        try (InputStream templateStream = getClass().getClassLoader().getResourceAsStream("templates/empty.doc");
             FileOutputStream out = new FileOutputStream(filePath)) {

            // Открываем его как HWPFDocument
            HWPFDocument document = new HWPFDocument(templateStream);

            // Получаем основной Range документа
            Range range = document.getRange();
            // Добавляем параграфы из WordDocContent
            for (String paragraphText : docContent.paragraphs()) {
                if (!paragraphText.trim().isEmpty()) {
                    // Создаем новый параграф
                    range.insertAfter(paragraphText + "\r");
                }
            }

            document.write(out);
            document.close();

        } catch (IOException e) {
            LOG.error("Ошибка перезаписи DOC файла", e);
            return ERROR_MESSAGE + e.getMessage();
        }
        LOG.info("DOC файл перезаписан: {}", filePath);
        return SUCCESS_MESSAGE + filePath;
    }

    private void createFolder(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());
    }
}
