package com.securova.server.llm;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SynchronizedAiService {
    private final AiService aiService;
    public synchronized AiService get() {
        return aiService;
    }
}
