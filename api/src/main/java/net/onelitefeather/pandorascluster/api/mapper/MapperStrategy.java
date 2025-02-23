package net.onelitefeather.pandorascluster.api.mapper;

import java.util.function.Function;

public interface MapperStrategy {

    Function<PandorasModel, PandorasModel> entityToModel();

    Function<PandorasModel, PandorasModel> modelToEntity();

    enum MapperType {
        ENTITY_TO_MODEL,
        MODEL_TO_ENTITY
    }
}
