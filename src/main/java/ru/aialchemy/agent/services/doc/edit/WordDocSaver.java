package ru.aialchemy.agent.services.doc.edit;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.aialchemy.agent.models.WordDocContent;
import ru.aialchemy.agent.models.WordDocReplace;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class WordDocSaver {
    private static final Logger LOG = LoggerFactory.getLogger(WordDocSaver.class);
    private static final String SUCCESS_MESSAGE = "Файл успешно сохранен по пути ";
    private static final String ERROR_MESSAGE = "Не удалось сохранить файл: ";

    /**
     * Перезаписывает файл на основе модифицированного WordDocContent
     */
    public String rewriteFile(MultipartFile originalFile, WordDocContent docContent, WordDocReplace wordDocReplace, String folder) {
        var filePath = folder + "/" + docContent.fileName();
        if (docContent.fileExtension().equalsIgnoreCase("docx")) {
            return rewriteDocxFile(originalFile, wordDocReplace, filePath);
        }
        return rewriteDocFile(originalFile, wordDocReplace, filePath);
    }

    private String rewriteDocxFile(MultipartFile originalFile, WordDocReplace wordDocReplace, String filePath) {
        try (InputStream originalStream = originalFile.getInputStream();
             XWPFDocument document = new XWPFDocument(originalStream); // ← ЧИТАЕМ ИСХОДНИК!
             FileOutputStream out = new FileOutputStream(filePath)) {

            createFolder(filePath);

            // РЕДАКТИРУЕМ исходный документ, а не создаем новый
            editDocxContent(document, wordDocReplace.searchText(), wordDocReplace.replacementText());

            document.write(out);

        } catch (IOException e) {
            LOG.error("Не удалось сохранить файл", e);
            return ERROR_MESSAGE + e.getMessage();
        }

        LOG.info("DOCX файл перезаписан: {}", filePath);
        return SUCCESS_MESSAGE + filePath;
    }

    private void editDocxContent(XWPFDocument document, String searchText, String replacement) {
        // Редактируем параграфы
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            editParagraph(paragraph, searchText, replacement);
        }

        // Редактируем таблицы
        for (XWPFTable table : document.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        editParagraph(paragraph, searchText, replacement);
                    }
                }
            }
        }
    }

    private void editParagraph(XWPFParagraph paragraph, String searchText, String replacement) {
        List<XWPFRun> runs = paragraph.getRuns();

        // Собираем весь текст параграфа для поиска
        StringBuilder paragraphText = new StringBuilder();
        for (XWPFRun run : runs) {
            String runText = run.getText(0);
            if (runText != null) {
                paragraphText.append(runText);
            }
        }

        // Если нашли текст для замены
        if (paragraphText.toString().contains(searchText)) {
            // Очищаем все runs
            for (int i = runs.size() - 1; i >= 0; i--) {
                paragraph.removeRun(i);
            }

            // Разбиваем текст на части до, включая и после searchText
            String fullText = paragraphText.toString();
            int searchIndex = fullText.indexOf(searchText);

            String beforeText = fullText.substring(0, searchIndex);
            String afterText = fullText.substring(searchIndex + searchText.length());

            // Создаем runs для разных частей текста
            if (!beforeText.isEmpty()) {
                XWPFRun beforeRun = paragraph.createRun();
                beforeRun.setText(beforeText);
                if (!runs.isEmpty()) copyRunFormatting(runs.get(0), beforeRun);
            }

            // Создаем run для searchText с особым форматированием
            if (!searchText.isEmpty()) {
                XWPFRun searchRun = paragraph.createRun();
                searchRun.setText(searchText);
                searchRun.setBold(true); // Жирный
                searchRun.setStrike(true); // Зачеркнутый
                if (!runs.isEmpty()) {
                    copyRunFormatting(runs.get(0), searchRun);
                    searchRun.setBold(true); // Переопределяем на жирный
                    searchRun.setStrike(true); // Переопределяем на зачеркнутый
                }
            }

            // Добавляем перенос строки и replacement
            if (!replacement.isEmpty()) {
                XWPFRun newLineRun = paragraph.createRun();
                newLineRun.addBreak(); // Перенос строки

                XWPFRun replacementRun = paragraph.createRun();
                replacementRun.setText(replacement);
            }

            // Добавляем оставшийся текст после searchText
            if (!afterText.isEmpty()) {
                XWPFRun afterRun = paragraph.createRun();
                afterRun.setText(afterText);
            }
        }
    }

    private void copyRunFormatting(XWPFRun source, XWPFRun target) {
        if (source.isBold()) target.setBold(true);
        if (source.isItalic()) target.setItalic(true);
        if (source.getFontSize() != -1) target.setFontSize(source.getFontSize());
        if (source.getColor() != null) target.setColor(source.getColor());
        if (source.getFontFamily() != null) target.setFontFamily(source.getFontFamily());
    }


    private String rewriteDocFile(MultipartFile originalFile, WordDocReplace wordDocReplace, String filePath) {
        try (InputStream originalStream = originalFile.getInputStream();
             HWPFDocument document = new HWPFDocument(originalStream); // ← Читаем исходный файл
             FileOutputStream out = new FileOutputStream(filePath)) {

            createFolder(filePath);

            // Редактируем содержимое документа
            editDocContent(document, wordDocReplace.searchText(), wordDocReplace.replacementText());

            document.write(out);

        } catch (IOException e) {
            LOG.error("Ошибка перезаписи DOC файла", e);
            return ERROR_MESSAGE + e.getMessage();
        }
        LOG.info("DOC файл перезаписан: {}", filePath);
        return SUCCESS_MESSAGE + filePath;
    }

    private void editDocContent(HWPFDocument document, String searchText, String replacement) {
        Range range = document.getRange();

        // Собираем параграфы для обработки (в обратном порядке)
        List<Paragraph> paragraphsToProcess = new ArrayList<>();
        for (int i = 0; i < range.numParagraphs(); i++) {
            Paragraph paragraph = range.getParagraph(i);
            if (paragraph.text().contains(searchText)) {
                paragraphsToProcess.add(paragraph);
            }
        }

        // Обрабатываем в обратном порядке
        for (int i = paragraphsToProcess.size() - 1; i >= 0; i--) {
            Paragraph paragraph = paragraphsToProcess.get(i);
            processDocParagraph(range, paragraph, searchText, replacement);
        }
    }

    private void processDocParagraph(Range range, Paragraph paragraph, String searchText, String replacement) {
        String paragraphText = paragraph.text();
        int searchIndex = paragraphText.indexOf(searchText);

        if (searchIndex != -1) {
            String beforeText = paragraphText.substring(0, searchIndex);
            String afterText = paragraphText.substring(searchIndex + searchText.length());

            // Формируем новый текст с форматированием
            // Используем текстовые маркеры
            String newText = beforeText +
                    "【 " + searchText + " 】" +
                    "\r" + replacement +
                    afterText;

            // Заменяем текст в параграфе
            range.replaceText(paragraphText, newText);
        }
    }

    private void createFolder(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());
    }
}
