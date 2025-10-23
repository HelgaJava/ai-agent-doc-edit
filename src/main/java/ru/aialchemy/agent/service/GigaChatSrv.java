package ru.aialchemy.agent.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import ru.aialchemy.agent.models.UserRq;

@Service
@Slf4j
public class GigaChatSrv {
    private final ChatClient chatClient;

    public GigaChatSrv(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String editText(UserRq userRq) {
        String response = chatClient
                .prompt()
                .user(userRq.userQuestion())
                .call()
                .content();
        log.info(response);
        return response;
    }
}
