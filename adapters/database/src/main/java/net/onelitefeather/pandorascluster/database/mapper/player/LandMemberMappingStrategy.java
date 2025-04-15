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
import net.onelitefeather.pandorascluster.dbo.land.LandAreaDBO;
import net.onelitefeather.pandorascluster.dbo.player.LandMemberDBO;
import net.onelitefeather.pandorascluster.dbo.player.LandPlayerDBO;

import java.util.function.Function;

public final class LandMemberMappingStrategy implements MapperStrategy {

    @Override
    public Function<PandorasModel, PandorasModel> entityToModel() {
        return databaseEntity -> {
            if (databaseEntity == null) return null;
            if (!(databaseEntity instanceof LandMemberDBO landMemberDBO)) return null;
            return new LandMember(
                    landMemberDBO.id(),
                    getLandPlayer(landMemberDBO.member()),
                    landMemberDBO.role(),
                    getLandArea(landMemberDBO.landArea()));
        };
    }

    @Override
    public Function<PandorasModel, PandorasModel> modelToEntity() {
        return model -> {
            if (model == null) return null;
            if (!(model instanceof LandMember landMember)) return null;
            return new LandMemberEntity(
                    landMember.getId(),
                    getLandPlayerEntity(landMember.getMember()),
                    landMember.getRole(),
                    getLandAreaEntity(landMember.getLandArea()));
        };
    }

    private LandPlayer getLandPlayer(LandPlayerDBO landPlayer) {
        MappingContext mappingContext = MappingContext.create();
        mappingContext.setMappingStrategy(LandPlayerMappingStrategy.create());
        mappingContext.setMappingType(MapperType.ENTITY_TO_MODEL);
        return (LandPlayer) mappingContext.doMapping(landPlayer);
    }

    private LandPlayerEntity getLandPlayerEntity(LandPlayer landPlayer) {
        MappingContext mappingContext = MappingContext.create();
        mappingContext.setMappingStrategy(LandPlayerMappingStrategy.create());
        mappingContext.setMappingType(MapperType.MODEL_TO_ENTITY);
        return (LandPlayerEntity) mappingContext.doMapping(landPlayer);
    }

    private LandArea getLandArea(LandAreaDBO area) {
        MappingContext mappingContext = MappingContext.create();
        mappingContext.setMappingStrategy(LandAreaMappingStrategy.create());
        mappingContext.setMappingType(MapperType.ENTITY_TO_MODEL);
        return (LandArea) mappingContext.doMapping(area);
    }

    private LandAreaEntity getLandAreaEntity(LandArea area) {
        MappingContext mappingContext = MappingContext.create();
        mappingContext.setMappingStrategy(LandAreaMappingStrategy.create());
        mappingContext.setMappingType(MapperType.MODEL_TO_ENTITY);
        return (LandAreaEntity) mappingContext.doMapping(area);
    }


    public static LandMemberMappingStrategy create() {
        return new LandMemberMappingStrategy();
    }
}
