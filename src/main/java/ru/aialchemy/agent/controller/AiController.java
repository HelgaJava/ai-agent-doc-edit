package ru.aialchemy.agent.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

    @PostMapping("/documents/edit")
    public ResponseEntity<String> editDoc(RequestEntity<UserRq> request, @RequestParam("file") MultipartFile file) {
        var body = request.getBody();
        log.info("Получен запрос пользователя: {} на работу с файлом {}", body, file.getOriginalFilename());

        var wordDocumentContent = validateRqSrv.validate(file);
        var errorMessage = wordDocumentContent.errorMessage();
        if (errorMessage != null) {
            return ResponseEntity.badRequest().body(errorMessage);
        }

        var result = "Получен корректный файл для анализа";
//                gigaChatSrv.editText(body);
        return ResponseEntity.ok(result);
    }


}
