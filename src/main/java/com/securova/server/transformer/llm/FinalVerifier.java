package com.securova.server.transformer.llm;

import com.google.gson.Gson;
import com.securova.server.data.ExtraData;
import com.securova.server.data.ProcessedData;
import com.securova.server.data.SourceData;
import com.securova.server.llm.AiResponseConverter;
import com.securova.server.llm.SynchronizedAiTransformer;
import com.securova.server.transformer.DataTransformer;
import org.jetbrains.annotations.NotNull;

public class FinalVerifier extends DataTransformer<ProcessedData,ProcessedData> {
    public FinalVerifier() {
        super(ProcessedData.class, ProcessedData.class);
    }

    @Override
    protected ProcessedData transformTo(@NotNull ProcessedData data, @NotNull ExtraData extra) {
        if(!(extra.getContent() instanceof SourceData)) return null;
        return AiResponseConverter.toBoolean(SynchronizedAiTransformer.get().isDataValid(((SourceData) extra.getContent()).toJson().toString(),new Gson().toJson(data))) ? data : null;
    }
}
