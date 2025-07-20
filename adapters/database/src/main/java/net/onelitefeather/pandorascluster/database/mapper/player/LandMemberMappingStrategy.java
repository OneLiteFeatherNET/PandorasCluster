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
import net.onelitefeather.pandorascluster.dto.land.LandAreaDto;
import net.onelitefeather.pandorascluster.dto.player.LandMemberDto;
import net.onelitefeather.pandorascluster.dto.player.LandPlayerDto;

import java.util.function.Function;

public final class LandMemberMappingStrategy implements MapperStrategy {

    @Override
    public Function<PandorasModel, PandorasModel> entityToModel() {
        return databaseEntity -> {
            if (databaseEntity == null) return null;
            if (!(databaseEntity instanceof LandMemberDto landMemberDto)) return null;
            return new LandMember(
                    landMemberDto.id(),
                    getLandPlayer(landMemberDto.member()),
                    landMemberDto.role(),
                    getLandArea(landMemberDto.landArea()));
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

    private LandPlayer getLandPlayer(LandPlayerDto landPlayer) {
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

    private LandArea getLandArea(LandAreaDto area) {
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
