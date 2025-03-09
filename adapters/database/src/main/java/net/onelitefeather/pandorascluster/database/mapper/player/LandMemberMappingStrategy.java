package net.onelitefeather.pandorascluster.database.mapper.player;

import net.onelitefeather.pandorascluster.api.land.LandArea;
import net.onelitefeather.pandorascluster.api.mapper.MapperStrategy;
import net.onelitefeather.pandorascluster.api.mapper.MappingContext;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import net.onelitefeather.pandorascluster.api.player.LandMember;
import net.onelitefeather.pandorascluster.api.player.LandPlayer;
import net.onelitefeather.pandorascluster.database.mapper.land.LandAreaMappingStrategy;
import net.onelitefeather.pandorascluster.database.models.land.LandAreaEntity;
import net.onelitefeather.pandorascluster.database.models.player.LandMemberEntity;
import net.onelitefeather.pandorascluster.database.models.player.LandPlayerEntity;
import net.onelitefeather.pandorascluster.dbo.player.LandMemberDBO;

import java.util.function.Function;

public final class LandMemberMappingStrategy implements MapperStrategy {

    @Override
    public Function<PandorasModel, PandorasModel> entityToModel() {
        return databaseEntity -> {
            if (databaseEntity == null) return null;
            if (!(databaseEntity instanceof LandMemberDBO landMemberDBO)) return null;
            MappingContext mappingContext = MappingContext.create();
            mappingContext.setMappingStrategy(LandAreaMappingStrategy.create());
            mappingContext.setMappingType(MapperStrategy.MapperType.ENTITY_TO_MODEL);
            return new LandMember(
                    landMemberDBO.id(),
                    (LandPlayer) mappingContext.doMapping(landMemberDBO.member()),
                    landMemberDBO.role(),
                    (LandArea) mappingContext.doMapping(landMemberDBO.landArea()));
        };
    }

    @Override
    public Function<PandorasModel, PandorasModel> modelToEntity() {
        return model -> {
            if (model == null) return null;
            if (!(model instanceof LandMember landMember)) return null;
            MappingContext mappingContext = MappingContext.create();
            mappingContext.setMappingStrategy(LandAreaMappingStrategy.create());
            mappingContext.setMappingType(MapperStrategy.MapperType.MODEL_TO_ENTITY);
            return new LandMemberEntity(
                    landMember.getId(),
                    (LandPlayerEntity) mappingContext.doMapping(landMember.getMember()),
                    landMember.getRole(),
                    (LandAreaEntity) mappingContext.doMapping(landMember.getLandArea()));
        };
    }

    public static LandMemberMappingStrategy create() {
        return new LandMemberMappingStrategy();
    }
}
