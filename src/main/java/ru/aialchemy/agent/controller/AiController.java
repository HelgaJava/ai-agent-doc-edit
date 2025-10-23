package ru.aialchemy.agent.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.aialchemy.agent.models.UserRq;
import ru.aialchemy.agent.service.GigaChatSrv;

@RestController
@RequestMapping(path = "/api/v1")
@Slf4j
public class AiController {
    private final GigaChatSrv gigaChatSrv;

    public AiController(GigaChatSrv gigaChatSrv) {
        this.gigaChatSrv = gigaChatSrv;
    }

    @PostMapping("/documents/edit")
    public ResponseEntity<String> editDoc(RequestEntity<UserRq> request) {
        var body = request.getBody();
        log.info("Получен запрос пользователя: {}", body);
        var result = gigaChatSrv.editText(body);
        return ResponseEntity.ok(result);
    }
}
