package ru.aialchemy.agent.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.aialchemy.agent.models.WordDocContent;
import ru.aialchemy.agent.models.WordDocReplaces;
import ru.aialchemy.agent.services.doc.edit.WordDocEditor;

@Component
@Slf4j
public class ContentTools {
    private final ObjectMapper mapper = new ObjectMapper();
    private final String folderSavingPath;
    private final WordDocEditor wordDocEditor;

    public ContentTools(@Value("${save.custom.path}") String folderSavingPath, WordDocEditor wordDocEditor) {
        this.folderSavingPath = folderSavingPath;
        this.wordDocEditor = wordDocEditor;
    }

    @Tool(description = "Получить текст для редактирования")
    public String getText(ToolContext toolContext) {
        WordDocContent wordDocContent = (WordDocContent) toolContext.getContext().get("fileContent");
        return wordDocContent.content();

    }

    @Tool(description = "Внести изменения в текст")
    public String editText(ToolContext toolContext, @ToolParam(description = "значение из jobResult") String jobResult) {
        log.info("Результаты работы LLM: {}", jobResult);
        try {
            WordDocReplaces wordDocReplace = mapper.readValue(jobResult, WordDocReplaces.class);
            var docContent = (WordDocContent) toolContext.getContext().get("fileContent");
            var originalFile = (MultipartFile) toolContext.getContext().get("originalFile");
            return wordDocEditor.rewriteFile(originalFile, wordDocReplace.wordDocReplaces(),
                    folderSavingPath + "/" + docContent.fileName(), docContent.fileExtension());
        } catch (JsonProcessingException e) {
            log.error("Не удалось десериализовать результаты работы LLM", e);
            return "Ошибка при попытке внести изменения в файл " + e.getMessage();
        }
    }
}
