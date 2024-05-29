package com.securova.server;

import com.securova.server.llm.ModelSource;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class ETLConfig {

    private ETLConfig(){}

    private static volatile ETLConfig instance = null;

    public static ETLConfig getInstance() {
        if(instance == null) {
            synchronized(ETLConfig.class) {
                if(instance == null) {
                    instance = new ETLConfig();
                }
            }
        }
        return instance;
    }

    @Getter
    ChatLanguageModel model = null;

    public boolean isLLMEnabled() {
        return model != null;
    }
    public void enableLLM(@Nullable String baseUrl, @NotNull String secretKey, @NotNull ModelSource modelSource, @NotNull String modelName) {
        switch (modelSource) {
            case DASH_SCOPE -> {
//                model = QwenChatModel.builder()
//                        .apiKey(secretKey)
//                        .modelName(modelName)
//                        .seed(114514)
//                        .topP(0.8)
//                        .maxTokens(1500)
//                        .temperature(0f)
//                        .repetitionPenalty(1.0f)
//                        .build();
                throw new IllegalArgumentException("DashScope is not supported");
            }
            case OPEN_AI -> {
                model = OpenAiChatModel.builder()
                        .baseUrl(baseUrl)
                        .apiKey(secretKey)
                        .modelName(modelName)
                        .seed(114514)
                        .topP(0.8)
                        .maxTokens(1500)
                        .temperature(0d)
                        .logRequests(false)
                        .logResponses(false)
                        .maxRetries(3)
                        .timeout(Duration.of(30, ChronoUnit.SECONDS))
                        .build();
            }
        }
    }
}
