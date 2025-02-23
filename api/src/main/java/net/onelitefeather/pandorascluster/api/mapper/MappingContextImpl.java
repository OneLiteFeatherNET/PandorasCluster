package net.onelitefeather.pandorascluster.api.mapper;

import java.util.Optional;

final class MappingContextImpl implements MappingContext {

    private MapperStrategy strategy;

    private MapperStrategy.MapperType type;

    @Override
    public void setMappingStrategy(MapperStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public void setMappingType(MapperStrategy.MapperType type) {
        this.type = type;
    }

    @Override
    public PandorasModel doMapping(PandorasModel model) {
        if (this.type == MapperStrategy.MapperType.ENTITY_TO_MODEL) {
            return Optional.ofNullable(strategy.entityToModel()).map(f -> f.apply(model)).orElse(null);
        } else {
            return Optional.ofNullable(strategy.entityToModel()).map(f -> f.apply(model)).orElse(null);
        }
    }

}
