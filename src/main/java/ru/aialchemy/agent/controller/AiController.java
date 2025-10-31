package ru.aialchemy.agent.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.aialchemy.agent.models.UserRq;
import ru.aialchemy.agent.services.doc.read.WordDocValidator;
import ru.aialchemy.agent.services.llm.GigaChatSrv;

@RestController
@RequestMapping(path = "/api/v1")
@Slf4j
public class AiController {
    private final GigaChatSrv gigaChatSrv;
    private final WordDocValidator wordDocValidator;

    public AiController(GigaChatSrv gigaChatSrv, WordDocValidator wordDocValidator) {
        this.gigaChatSrv = gigaChatSrv;
        this.wordDocValidator = wordDocValidator;
    }

    @PostMapping(value = "/documents/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> editDoc(@RequestPart("userRq") UserRq request, @RequestPart("file") MultipartFile file) {
        log.info("Получен запрос пользователя: {} на работу с файлом {}", request, file.getOriginalFilename());

        var wordDocumentContent = wordDocValidator.validate(file);
        if (wordDocumentContent.errorMessage() != null) {
            return ResponseEntity.badRequest().body(wordDocumentContent.errorMessage());
        }

        var result = gigaChatSrv.editText(request, wordDocumentContent, file);
        return ResponseEntity.ok(result);
    }

}
