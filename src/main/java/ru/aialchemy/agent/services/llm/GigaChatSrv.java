package ru.aialchemy.agent.services.llm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.aialchemy.agent.models.UserRq;
import ru.aialchemy.agent.models.docIn.WordDocContent;

import java.util.Map;

@Service
@Slf4j
public class GigaChatSrv {
    private final ChatClient chatClient;

    public GigaChatSrv(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String editText(UserRq userRq, WordDocContent wordDocContent, MultipartFile originalFile) {
        String response = chatClient
                .prompt()
                .toolContext(Map.of("fileContent", wordDocContent, "originalFile", originalFile))
                .user(userRq.userQuestion())
                .call()
                .content();
        log.info(response);
        return response;
    }
}
