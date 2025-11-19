package ru.aialchemy.agent.services.doc.read;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.aialchemy.agent.models.docIn.WordDocContent;

import static ru.aialchemy.agent.util.DocUtil.getFileExtension;

/**
 * Сервис валидации корректности полученного файла
 */
@Service
@Slf4j
public class WordDocValidator {
    private final WordDocReader wordDocReader;

    public WordDocValidator(WordDocReader wordDocReader) {
        this.wordDocReader = wordDocReader;
    }

    public WordDocContent validate(MultipartFile file) {
        var fileName = file.getOriginalFilename();
        var fileExtension = getFileExtension(fileName);

        // Валидация на входе
        if (fileExtension == null) {
            log.warn("Неподдерживаемый тип файла: {}", fileName);
            return WordDocContent.error("Неподдерживаемый тип файла +" + fileName + " Поддерживаются типы .doc и .docx");
        }

        if (file.isEmpty()) {
            log.warn("Получен пустой файл: {}", fileName);
            return WordDocContent.error("Файл " + fileName + " для анализа пуст");
        }

        try {
            return wordDocReader.readWordDocument(file, fileExtension, fileName);

        } catch (Exception e) {
            log.error("Ошибка при обработке файла {}: {}", file.getOriginalFilename(), e.getMessage());
            return WordDocContent.error("Внутренняя ошибка сервера: " + e.getMessage());
        }

    }

}
