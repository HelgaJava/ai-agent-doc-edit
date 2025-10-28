package ru.aialchemy.agent.services.llm;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import ru.aialchemy.agent.models.UserRq;
import ru.aialchemy.agent.models.WordDocContent;

import java.util.Map;

@Service
@Slf4j
public class GigaChatSrv {
    private static final Logger log = LoggerFactory.getLogger(GigaChatSrv.class);
    private final ChatClient chatClient;

    public GigaChatSrv(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String editText(UserRq userRq, WordDocContent wordDocContent) {
        String response = chatClient
                .prompt()
                .toolContext(Map.of("fileContent", wordDocContent))
                .user(userRq.userQuestion())
                .call()
                .content();
        log.info(response);
        return response;
    }
}
