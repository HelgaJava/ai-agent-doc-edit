package ru.aialchemy.agent.services.doc.edit;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.aialchemy.agent.models.WordDocContent;

import java.io.FileOutputStream;
import java.io.IOException;

@Service
@Slf4j
public class WordDocSaver {

    private static final Logger log = LoggerFactory.getLogger(WordDocSaver.class);

    /**
     * Перезаписывает файл на основе модифицированного WordDocContent
     */
    public void rewriteFile(WordDocContent docContent, String filePath) throws IOException {
        if (docContent.fileExtension().equalsIgnoreCase("docx")) {
            rewriteDocxFile(docContent, filePath);
        }
//        else if (docContent.fileExtension().equalsIgnoreCase("doc")) {
//            rewriteDocFile(docContent, filePath);
//        }
    }

    private void rewriteDocxFile(WordDocContent docContent, String filePath) throws IOException {
        try (XWPFDocument document = new XWPFDocument();
             FileOutputStream out = new FileOutputStream(filePath)) {

            // Воссоздаем параграфы из editedContent
            for (String paragraphText : docContent.paragraphs()) {
                XWPFParagraph paragraph = document.createParagraph();
                XWPFRun run = paragraph.createRun();
                run.setText(paragraphText);
            }

            document.write(out);
        }

        log.info("DOCX файл перезаписан: {}", filePath);
    }

//    private void rewriteDocFile(WordDocContent docContent, String filePath) throws IOException {
//        // Для .doc файлов - более сложная логика, но аналогичный подход
//        try (HWPFDocument document = new HWPFDocument();
//             FileOutputStream out = new FileOutputStream(filePath)) {
//
//            // Упрощенная реализация для .doc
//            // Здесь нужна более сложная логика для работы с Range
//            document.write(out);
//        }
//        log.info("DOC файл перезаписан: {}", filePath);
//    }




}
