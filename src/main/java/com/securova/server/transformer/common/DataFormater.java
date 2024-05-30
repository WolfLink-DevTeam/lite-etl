package com.securova.server.transformer.common;

import com.securova.server.data.ExtraData;
import com.securova.server.data.FormatDictionary;
import com.securova.server.data.SourceData;
import com.securova.server.transformer.DataTransformer;
import org.jetbrains.annotations.NotNull;

public class DataFormater extends DataTransformer<SourceData, SourceData> {
    FormatDictionary dictionary;

    public DataFormater(FormatDictionary dictionary) {
        super(SourceData.class, SourceData.class);
        this.dictionary = dictionary;
    }

    @Override
    protected SourceData transformTo(@NotNull SourceData sourceData, @NotNull ExtraData extra) {
        dictionary.format(sourceData.content(), sourceData.type()); // 数据字段规格化
        return sourceData;
    }
}
