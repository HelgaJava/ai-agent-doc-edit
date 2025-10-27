package ru.aialchemy.agent.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.aialchemy.agent.models.WordDocumentContent;

import static ru.aialchemy.agent.util.DocUtil.getFileExtension;

/**
 * Сервис валидации корректности полученного файла
 */
@Service
@Slf4j
public class ValidateRqSrv {

    private static final Logger log = LoggerFactory.getLogger(ValidateRqSrv.class);
    private final WordDocSrv wordDocSrv;

    public ValidateRqSrv(WordDocSrv wordDocSrv) {
        this.wordDocSrv = wordDocSrv;
    }

    public WordDocumentContent validate(MultipartFile file) {
        var fileName = file.getOriginalFilename();
        var fileExtension = getFileExtension(fileName);

        // Валидация на входе
        if (fileExtension == null) {
            log.warn("Неподдерживаемый тип файла: {}", fileName);
            return WordDocumentContent.error("Неподдерживаемый тип файла +" + fileName + " Поддерживаются типы .doc и .docx");
        }

        if (file.isEmpty()) {
            log.warn("Получен пустой файл: {}", fileName);
            return WordDocumentContent.error("Файл " + fileName + " для анализа пуст");
        }

        try {
            return wordDocSrv.readWordDocument(file, fileExtension, fileName);

        } catch (Exception e) {
            log.error("Ошибка при обработке файла {}: {}", file.getOriginalFilename(), e.getMessage());
            return WordDocumentContent.error("Внутренняя ошибка сервера: " + e.getMessage());
        }

    }

}
