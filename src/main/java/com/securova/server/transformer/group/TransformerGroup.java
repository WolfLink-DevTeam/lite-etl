package com.securova.server.transformer.group;

import com.securova.server.data.ProcessedData;
import com.securova.server.data.SourceData;
import com.securova.server.transformer.DataTransformer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public class TransformerGroup {
    private final List<DataTransformer<?, ?>> transformers;

    public TransformerGroup(DataTransformer<?, ?>... transformers) {
        this.transformers = Arrays.stream(transformers).toList();
        verifyTransformers();
    }

    /**
     * 验证多个转换器是否按顺序接通
     */
    private void verifyTransformers() {
        DataTransformer<?, ?> firstTransformer = null;
        DataTransformer<?, ?> lastTransformer = null;
        for (DataTransformer<?, ?> transformer : transformers) {
            if (firstTransformer == null) {
                firstTransformer = transformer;
                lastTransformer = transformer;
            } else {
                if (!transformer.getFromClass().equals(lastTransformer.getToClass())) {

                    throw new IllegalArgumentException("""
                            Transformer flows are not valid .
                                                        
                            lastTransformer:
                                className: %s
                                fromClassName: %s
                            nextTransformer:
                                className: %s
                                toClassName: %s
                            """.formatted(
                            lastTransformer.getClass().getName(),
                            lastTransformer.getToClass().getName(),
                            transformer.getClass().getName(),
                            transformer.getFromClass().getName()));
                } else lastTransformer = transformer;
            }
        }
        if (firstTransformer == null) throw new IllegalArgumentException("No transformer found");
        if (!firstTransformer.getFromClass().equals(SourceData.class))
            throw new IllegalArgumentException("Transformer flows must use SourceData as input");
        if (!lastTransformer.getToClass().equals(ProcessedData.class))
            throw new IllegalArgumentException("Transformer flows must use ProcessedData as output");
    }
}
