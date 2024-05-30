package com.securova.server.transformer;

import com.securova.server.data.FormatDictionary;
import com.securova.server.transformer.common.DataFormater;
import com.securova.server.transformer.llm.FinalVerifier;
import com.securova.server.transformer.llm.FuzzyExtractor;
import com.securova.server.transformer.llm.ValueFilter;

public class LLMTransformerGroup extends TransformerGroup {
    public LLMTransformerGroup(FormatDictionary dictionary) {
        super(new DataFormater(dictionary), ValueFilter.getInstance(), FuzzyExtractor.getInstance(), FinalVerifier.getInstance());
    }
}
