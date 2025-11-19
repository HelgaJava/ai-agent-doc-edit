package ru.aialchemy.agent.services.doc.read;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.aialchemy.agent.models.docIn.CellData;
import ru.aialchemy.agent.models.docIn.TableData;
import ru.aialchemy.agent.models.docIn.WordDocContent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис чтения содержимого файла
 */
@Service
@Slf4j
public class WordDocReader {

    public WordDocContent readWordDocument(MultipartFile file, String extension, String fileName) {
        try {
            String content;
            List<String> paragraphs;
            List<TableData> tables = null;

            if ("docx".equalsIgnoreCase(extension)) {
                // Чтение .docx файлов
                var document = new XWPFDocument(file.getInputStream());
                content = readDocxContent(document);
                paragraphs = extractParagraphsFromDocx(document);
                tables = extractTablesFromDocx(document);
                document.close();
            } else {
                // Чтение .doc файлов
                var document = new HWPFDocument(file.getInputStream());
                content = readDocContent(document);
                paragraphs = extractParagraphsFromDoc(document);
                document.close();
            }

            return new WordDocContent(fileName, content, paragraphs.size(), paragraphs, extension, tables);

        } catch (Exception e) {
            log.error("Ошибка чтения Word документа: {}", e.getMessage());
            throw new RuntimeException("Не удалось прочитать документ", e);
        }
    }

    /**
     * Чтение содержимого .docx файла
     */
    private String readDocxContent(XWPFDocument document) {
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
        return content.toString().trim();
    }

    /**
     * Извлечение параграфов из .docx для структурированного представления
     */
    private List<String> extractParagraphsFromDocx(XWPFDocument document) {
        List<String> paragraphs = document.getParagraphs().stream()
                .map(XWPFParagraph::getText)
                .filter(text -> !text.trim().isEmpty())
                .collect(Collectors.toList());
        return paragraphs;
    }

    /**
     * Извлечение таблиц из .docx с индексами строк и столбцов
     */
    private List<TableData> extractTablesFromDocx(XWPFDocument document) {
        List<TableData> tables = new ArrayList<>();

        for (int tableIndex = 0; tableIndex < document.getTables().size(); tableIndex++) {
            XWPFTable table = document.getTables().get(tableIndex);
            List<CellData> cells = new ArrayList<>();

            for (int rowIndex = 0; rowIndex < table.getRows().size(); rowIndex++) {
                XWPFTableRow row = table.getRows().get(rowIndex);

                for (int colIndex = 0; colIndex < row.getTableCells().size(); colIndex++) {
                    XWPFTableCell cell = row.getTableCells().get(colIndex);
                    String cellText = getCellText(cell);

                    if (!cellText.trim().isEmpty()) {
                        CellData cellData = new CellData(rowIndex, colIndex, cellText);
                        cells.add(cellData);
                    }
                }
            }

            tables.add(new TableData(tableIndex, cells));
        }

        return tables;
    }

    /**
     * Получение текста ячейки (учитывает все параграфы в ячейке)
     */
    private String getCellText(XWPFTableCell cell) {
        return cell.getParagraphs().stream()
                .map(XWPFParagraph::getText)
                .filter(text -> !text.trim().isEmpty())
                .collect(Collectors.joining(" "));
    }

    /**
     * Чтение содержимого .doc файла
     */
    private String readDocContent(HWPFDocument document) throws IOException {
        WordExtractor extractor = new WordExtractor(document);
        String content = extractor.getText();
        extractor.close();
        return content.trim();
    }

    /**
     * Извлечение параграфов из .doc
     */
    private List<String> extractParagraphsFromDoc(HWPFDocument document) throws IOException {
        WordExtractor extractor = new WordExtractor(document);
        String[] paragraphArray = extractor.getParagraphText();
        extractor.close();

        return Arrays.stream(paragraphArray)
                .map(text -> text.replace("\r", "").replace("\u0007", "").trim())
                .filter(text -> !text.isEmpty())
                .collect(Collectors.toList());
    }


}
