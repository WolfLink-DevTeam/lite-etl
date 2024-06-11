package com.securova.server.transformer.group;

import com.securova.server.data.FormatDictionary;
import com.securova.server.transformer.common.DataFormatter;
import com.securova.server.transformer.llm.FinalVerifier;
import com.securova.server.transformer.llm.FuzzyExtractor;
import com.securova.server.transformer.llm.ValueFilter;

public class LLMTransformerGroup extends TransformerGroup {
    public LLMTransformerGroup(FormatDictionary dictionary) {
        super(new DataFormatter(dictionary), ValueFilter.getInstance(), FuzzyExtractor.getInstance(), FinalVerifier.getInstance());
    }
}
