package ru.aialchemy.agent.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import ru.aialchemy.agent.models.UserRq;
import ru.aialchemy.agent.models.WordDocumentContent;

import java.util.Map;

@Service
@Slf4j
public class GigaChatSrv {
    private static final Logger log = LoggerFactory.getLogger(GigaChatSrv.class);
    private final ChatClient chatClient;

    public GigaChatSrv(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String editText(UserRq userRq, WordDocumentContent wordDocumentContent) {
        String response = chatClient
                .prompt()
                .toolContext(Map.of(
                        "fileContent", wordDocumentContent.content(),
                        "fileName", wordDocumentContent.fileName()
                ))
                .user(userRq.userQuestion())
                .call()
                .content();
        log.info(response);
        return response;
    }
}
