package com.securova.server.transformer.llm;

import com.securova.server.data.ExtraData;
import com.securova.server.data.SourceData;
import com.securova.server.llm.AiResponseConverter;
import com.securova.server.llm.SynchronizedAiTransformer;
import com.securova.server.transformer.DataTransformer;
import org.jetbrains.annotations.NotNull;

public class ValueFilter extends DataTransformer<SourceData,SourceData> {
    public ValueFilter() {
        super(SourceData.class, SourceData.class);
    }
    @Override
    protected SourceData transformTo(@NotNull SourceData sourceData, @NotNull ExtraData extra) {
        return AiResponseConverter.toBoolean(SynchronizedAiTransformer.get().isContentUseful(sourceData.toJson().toString())) ? sourceData : null;
    }
}
