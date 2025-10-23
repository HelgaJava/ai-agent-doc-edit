package ru.aialchemy.agent.config;

import chat.giga.springai.GigaChatOptions;
import chat.giga.springai.api.chat.GigaChatApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
@Slf4j
public class ClientsConfig {
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, @Value("${giga.system.propmt.path}") String promptPath) {
        try {
            var systemPrompt = new String(Files.readAllBytes(Path.of(promptPath)));

            return builder
                    .defaultSystem(systemPrompt)
                    .defaultAdvisors(new SimpleLoggerAdvisor())
                    .defaultOptions(GigaChatOptions.builder()
                            .model(GigaChatApi.ChatModel.GIGA_CHAT)
                            .build())
                    .build();
        } catch (IOException e) {
            log.error("Не удалось инициализировать клиента GigaChat: {}", e.getMessage());
            throw new RuntimeException(e);
        }

    }
}
