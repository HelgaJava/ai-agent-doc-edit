package ru.aialchemy.agent.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.aialchemy.agent.models.WordDocumentContent;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис чтения содержимого файла
 */
@Service
@Slf4j
public class WordDocSrv {
    private static final Logger log = LoggerFactory.getLogger(WordDocSrv.class);

    public WordDocumentContent readWordDocument(MultipartFile file, String extension, String fileName) {
        try {
            String content;
            List<String> paragraphs;
            var inputStream = file.getInputStream();

            if ("docx".equalsIgnoreCase(extension)) {
                // Чтение .docx файлов
                content = readDocxContent(inputStream);
                paragraphs = extractParagraphsFromDocx(inputStream);
            } else {
                // Чтение .doc файлов
                content = readDocContent(inputStream);
                paragraphs = extractParagraphsFromDoc(inputStream);
            }

            return new WordDocumentContent(fileName, content, paragraphs.size(), paragraphs);

        } catch (Exception e) {
            log.error("Ошибка чтения Word документа: {}", e.getMessage());
            throw new RuntimeException("Не удалось прочитать документ", e);
        }
    }

    /**
     * Чтение содержимого .docx файла
     */
    private String readDocxContent(InputStream inputStream) throws IOException {
        XWPFDocument document = new XWPFDocument(inputStream);
        StringBuilder content = new StringBuilder();

        // 1. Чтение всех параграфов
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            String text = paragraph.getText();
            if (!text.trim().isEmpty()) {
                content.append(text).append("\n");
            }
        }

        // 2. Чтение таблиц (опционально)
        for (XWPFTable table : document.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    content.append(cell.getText()).append("\t");
                }
                content.append("\n");
            }
        }

        document.close();
        return content.toString().trim();
    }

    /**
     * Извлечение параграфов из .docx для структурированного представления
     */
    private List<String> extractParagraphsFromDocx(InputStream inputStream) throws IOException {
        XWPFDocument document = new XWPFDocument(inputStream);
        List<String> paragraphs = document.getParagraphs().stream()
                .map(XWPFParagraph::getText)
                .filter(text -> !text.trim().isEmpty())
                .collect(Collectors.toList());
        document.close();
        return paragraphs;
    }

    /**
     * Чтение содержимого .doc файла
     */
    private String readDocContent(InputStream inputStream) throws IOException {
        HWPFDocument document = new HWPFDocument(inputStream);
        WordExtractor extractor = new WordExtractor(document);
        String content = extractor.getText();
        extractor.close();
        document.close();
        return content.trim();
    }

    /**
     * Извлечение параграфов из .doc
     */
    private List<String> extractParagraphsFromDoc(InputStream inputStream) throws IOException {
        HWPFDocument document = new HWPFDocument(inputStream);
        WordExtractor extractor = new WordExtractor(document);
        String[] paragraphArray = extractor.getParagraphText();
        extractor.close();
        document.close();

        return Arrays.stream(paragraphArray)
                .map(text -> text.replace("\r", "").replace("\u0007", "").trim())
                .filter(text -> !text.isEmpty())
                .collect(Collectors.toList());
    }


}
