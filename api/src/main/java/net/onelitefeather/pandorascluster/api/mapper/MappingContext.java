package net.onelitefeather.pandorascluster.api.mapper;

import org.jetbrains.annotations.Nullable;

public sealed interface MappingContext permits MappingContextImpl {

    void setMappingStrategy(MapperStrategy strategy);

    void setMappingType(MapperStrategy.MapperType type);

    @Nullable
    PandorasModel doMapping(PandorasModel object);

    static MappingContext create() {
        return new MappingContextImpl();
    }
}
