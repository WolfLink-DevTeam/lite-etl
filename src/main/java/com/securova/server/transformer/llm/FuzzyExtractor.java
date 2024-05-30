package com.securova.server.transformer.llm;

import com.securova.server.data.ExtraData;
import com.securova.server.data.ProcessedData;
import com.securova.server.data.SourceData;
import com.securova.server.llm.AiResponseConverter;
import com.securova.server.llm.SynchronizedAiTransformer;
import com.securova.server.transformer.DataTransformer;
import org.jetbrains.annotations.NotNull;

public class FuzzyExtractor extends DataTransformer<SourceData, ProcessedData> {
    public FuzzyExtractor() {
        super(SourceData.class, ProcessedData.class);
    }

    @Override
    protected ProcessedData transformTo(@NotNull SourceData sourceData, @NotNull ExtraData extra) {
        extra.setContent(sourceData);
        ProcessedData data = AiResponseConverter.toProcessedData(SynchronizedAiTransformer.get().extract(sourceData.toJson().toString()));
        return data;
    }
}
