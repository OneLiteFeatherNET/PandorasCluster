package net.onelitefeather.pandorascluster.database.mapper.position;

import net.onelitefeather.pandorascluster.api.mapper.MapperStrategy;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import net.onelitefeather.pandorascluster.api.position.HomePosition;
import net.onelitefeather.pandorascluster.database.models.position.HomePositionEntity;

import java.util.function.Function;

public final class HomePositionMappingStrategy implements MapperStrategy {

    @Override
    public Function<PandorasModel, PandorasModel> entityToModel() {
        return entity -> {
            if(entity == null) return null;
            if(!(entity instanceof HomePositionEntity home)) return null;
            return new HomePositionEntity(home.id(), home.world(), home.posX(), home.posY(), home.posZ(), home.yaw(), home.pitch());
        };
    }

    @Override
    public Function<PandorasModel, PandorasModel> modelToEntity() {
        return model -> {
            if(model == null) return null;
            if(!(model instanceof HomePosition home)) return null;
            return new HomePositionEntity(home.getId(), home.getWorld(), home.getPosX(), home.getPosY(), home.getPosZ(), home.getYaw(), home.getPitch());
        };
    }

    public static HomePositionMappingStrategy create() {
        return new HomePositionMappingStrategy();
    }
}
