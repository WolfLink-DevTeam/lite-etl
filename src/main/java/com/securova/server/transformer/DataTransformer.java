package com.securova.server.transformer;

import com.securova.server.Pipeline;
import com.securova.server.data.ExtraData;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public abstract class DataTransformer<FROM,TO> {

    Pipeline pipeline;
    @Getter
    Class<FROM> fromClass;
    @Getter
    Class<TO> toClass;

    public DataTransformer(Class<FROM> fromClass, Class<TO> toClass) {
        this.fromClass = fromClass;
        this.toClass = toClass;
    }

    public void bind(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public final TO transform(@NotNull Object from, @NotNull ExtraData extra) {
        return transformTo(fromClass.cast(from),extra);
    }
    protected TO transformTo(@NotNull FROM from,@NotNull ExtraData extra) { return null; }
}
