package ru.aialchemy.agent.config;

import chat.giga.springai.GigaChatOptions;
import chat.giga.springai.api.chat.GigaChatApi;
import jakarta.servlet.MultipartConfigElement;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import ru.aialchemy.agent.tools.ContentTools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
@Slf4j
public class Config {
    private static final Logger log = LoggerFactory.getLogger(Config.class);

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, @Value("${giga.system.propmt.path}") String promptPath, ContentTools contentTools) {
        try {
            var systemPrompt = new String(Files.readAllBytes(Path.of(promptPath)));

            return builder
                    .defaultSystem(systemPrompt)
                    .defaultAdvisors(new SimpleLoggerAdvisor())
                    .defaultOptions(GigaChatOptions.builder()
                            .model(GigaChatApi.ChatModel.GIGA_CHAT)
                            .build())
                    .defaultTools(contentTools)
                    .build();
        } catch (IOException e) {
            log.error("Не удалось инициализировать клиента GigaChat: {}", e.getMessage());
            throw new RuntimeException(e);
        }

    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofMegabytes(50));
        factory.setMaxRequestSize(DataSize.ofMegabytes(50));
        return factory.createMultipartConfig();
    }
}
