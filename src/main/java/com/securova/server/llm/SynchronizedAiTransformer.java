package com.securova.server.llm;

import com.securova.server.ETLConfig;
import dev.langchain4j.service.AiServices;

public class SynchronizedAiTransformer {
    private static AiTransformer aiTransformer = null;

    public static synchronized AiTransformer get() {
        if (aiTransformer == null) {
            aiTransformer = AiServices.create(AiTransformer.class, ETLConfig.getInstance().getModel());
        }
        return aiTransformer;
    }
}
