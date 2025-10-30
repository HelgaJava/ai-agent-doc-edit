package ru.aialchemy.agent.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.aialchemy.agent.models.UserRq;
import ru.aialchemy.agent.models.WordDocContent;
import ru.aialchemy.agent.services.doc.edit.WordDocEditor;
import ru.aialchemy.agent.services.doc.edit.WordDocSaver;
import ru.aialchemy.agent.services.llm.GigaChatSrv;
import ru.aialchemy.agent.services.doc.read.WordDocValidator;

@RestController
@RequestMapping(path = "/api/v1")
@Slf4j
public class AiController {
    private static final Logger log = LoggerFactory.getLogger(AiController.class);
    private final GigaChatSrv gigaChatSrv;
    private final WordDocValidator wordDocValidator;

    @Autowired
    private WordDocEditor wordDocEditor;

    @Autowired
    private WordDocSaver wordDocSaver;

    @Value("${save.custom.path}")
    private String folder;

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
        WordDocContent wordDocContentNew = wordDocEditor.editContent(wordDocumentContent, "Маша придя домой поздним вечером быстро снила пальто бросила сумочку на деван",
                "Маша, придя домой поздним вечером, быстро сняла пальто бросила сумочку на диван");
        String rewritten = wordDocSaver.rewriteFile(wordDocContentNew, folder);

        var result = "Получен корректный файл для анализа:\n"+wordDocumentContent.content();
//                gigaChatSrv.editText(request, wordDocumentContent);
        return ResponseEntity.ok(result);
    }


}
