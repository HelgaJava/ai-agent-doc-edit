package ru.aialchemy.agent.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.aialchemy.agent.models.WordDocContent;
import ru.aialchemy.agent.services.doc.edit.WordDocSaver;

@Component
@Slf4j
public class ContentTools {
    private final ObjectMapper mapper = new ObjectMapper();
    private final String folderSavingPath;
    private final WordDocSaver wordDocSaver;

    public ContentTools(@Value("${save.custom.path}") String folderSavingPath, WordDocSaver wordDocSaver) {
        this.folderSavingPath = folderSavingPath;
        this.wordDocSaver = wordDocSaver;
    }

    @Tool(description = "Получить текст для редактирования")
    public String getText(ToolContext toolContext) {
        WordDocContent wordDocContent = (WordDocContent) toolContext.getContext().get("fileContent");
        return wordDocContent.content();

    }

//    @Tool(description = "Получить имя файла для сохранения")
//    public String getFileName(ToolContext toolContext){
//        return String.valueOf(toolContext.getContext().get("fileName"));
//
//    }

    @Tool(description = "Внести изменения в текст")
    public String editText(ToolContext toolContext, @ToolParam(description = "значение из jobResult") String jobResult) {
        WordDocContent wordDocContent = (WordDocContent) toolContext.getContext().get("fileContent");
        return wordDocSaver.rewriteFile(wordDocContent, folderSavingPath);

    }
}
