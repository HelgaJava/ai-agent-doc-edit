package ru.aialchemy.agent.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ContentTools {

    @Tool(description = "Получить текст для редактирования")
    public String getEditContent(ToolContext toolContext){
        return String.valueOf(toolContext.getContext().get("fileContent"));

    }

//    @Tool(description = "Получить имя файла для сохранения")
//    public String getFileName(ToolContext toolContext){
//        return String.valueOf(toolContext.getContext().get("fileName"));
//
//    }
}
