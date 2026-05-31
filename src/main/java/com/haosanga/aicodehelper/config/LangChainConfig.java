package com.haosanga.aicodehelper.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class LangChainConfig {

    @Value("${ai.dashscope.api-key}")
    private String apiKey;

    @Value("${ai.dashscope.model-name}")
    private String modelName;

    @Bean
    public ChatModel chatModel() {
        return OpenAiChatModel.builder()
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .apiKey(apiKey)
                .modelName(modelName)
                .maxRetries(2)
                .timeout(Duration.ofSeconds(120))
                .build();
    }
}
