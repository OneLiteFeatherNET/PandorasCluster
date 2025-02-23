package net.onelitefeather.pandorascluster.database.mapper;

import net.onelitefeather.pandorascluster.api.land.flag.LandNaturalFlag;
import net.onelitefeather.pandorascluster.api.mapper.MapperStrategy;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import net.onelitefeather.pandorascluster.database.models.flag.LandNaturalFlagEntity;
import net.onelitefeather.pandorascluster.dbo.flag.NaturalFlagDBO;

import java.util.function.Function;

public final class NaturalFlagMappingStrategy implements MapperStrategy {

    @Override
    public Function<PandorasModel, PandorasModel> entityToModel() {
        return databaseEntity -> {
            if (databaseEntity == null) return null;
            if (!(databaseEntity instanceof NaturalFlagDBO naturalFlagDBO)) return null;
            return new LandNaturalFlag(naturalFlagDBO.id(), naturalFlagDBO.name(), naturalFlagDBO.state());
        };
    }

    @Override
    public Function<PandorasModel, PandorasModel> modelToEntity() {
        return pandorasModel -> {
            if (pandorasModel == null) return null;
            if (!(pandorasModel instanceof LandNaturalFlag landNaturalFlag)) return null;
            return new LandNaturalFlagEntity(landNaturalFlag.getId(), landNaturalFlag.getName(), landNaturalFlag.getState(), null);
        };
    }

    public static NaturalFlagMappingStrategy create() {
        return new NaturalFlagMappingStrategy();
    }
}
