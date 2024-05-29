package com.securova.server.transformer.llm;

import com.securova.server.ETLConfig;
import com.securova.server.Pipeline;
import com.securova.server.data.ProcessedData;
import com.securova.server.data.SourceData;
import com.securova.server.llm.AiService;
import com.securova.server.llm.SynchronizedAiService;
import com.securova.server.transformer.DataTransformer;
import com.securova.server.writer.DataWriter;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 基于大语言模型实现的转换器，包括 内容过滤、信息提取、数据校验
 */
public class LLMTransformer extends DataTransformer {

    protected SynchronizedAiService aiService = null;
    protected ChatLanguageModel model = null;

    @Override
    public void initialize(Pipeline pipeline, DataWriter writer) {
        super.initialize(pipeline, writer);
        if (ETLConfig.getInstance().isLLMEnabled()) {
            model = ETLConfig.getInstance().getModel();
            aiService = new SynchronizedAiService(AiServices.create(AiService.class, model));
        } else throw new RuntimeException("LLM service not enabled");
    }

    @Override
    protected ProcessedData process(SourceData sourceData) {
        return CompletableFuture
                // 无效内容过滤
                .supplyAsync(()->aiService.get().isContentUseful(sourceData))
                // 关键信息提取
                .thenApply(useful -> useful ? aiService.get().extract(sourceData) : null)
                // 最终数据校验
                .thenApply(processedData -> (aiService.get().isDataValid(sourceData,processedData) ? processedData : null))
                // 60秒内未完成流程则传回 null
                .completeOnTimeout(null,60, TimeUnit.SECONDS)
                .join();
    }
}
