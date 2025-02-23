package net.onelitefeather.pandorascluster.database.mapper;

import net.onelitefeather.pandorascluster.api.mapper.MapperStrategy;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;

import java.util.function.Function;

public final class LandAreaMappingStrategy implements MapperStrategy {

    @Override
    public Function<PandorasModel, PandorasModel> entityToModel() {
        return databaseEntity -> {

        };
    }

    @Override
    public Function<PandorasModel, PandorasModel> modelToEntity() {
        return null;
    }
}
