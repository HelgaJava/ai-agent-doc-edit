package ru.aialchemy.agent.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.aialchemy.agent.models.UserRq;
import ru.aialchemy.agent.service.GigaChatSrv;
import ru.aialchemy.agent.service.ValidateRqSrv;

@RestController
@RequestMapping(path = "/api/v1")
@Slf4j
public class AiController {
    private static final Logger log = LoggerFactory.getLogger(AiController.class);
    private final GigaChatSrv gigaChatSrv;
    private final ValidateRqSrv validateRqSrv;

    public AiController(GigaChatSrv gigaChatSrv, ValidateRqSrv validateRqSrv) {
        this.gigaChatSrv = gigaChatSrv;
        this.validateRqSrv = validateRqSrv;
    }

    @PostMapping(value = "/documents/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> editDoc(@RequestParam(name = "userRq") RequestEntity<UserRq> request, @RequestPart("file") MultipartFile file) {
        var body = request.getBody();
        log.info("Получен запрос пользователя: {} на работу с файлом {}", body, file.getOriginalFilename());

        var wordDocumentContent = validateRqSrv.validate(file);
        if (wordDocumentContent.errorMessage() != null) {
            return ResponseEntity.badRequest().body(wordDocumentContent.errorMessage());
        }

        var result = "Получен корректный файл для анализа:\n\n"+wordDocumentContent.content();
//                gigaChatSrv.editText(body);
        return ResponseEntity.ok(result);
    }


}
