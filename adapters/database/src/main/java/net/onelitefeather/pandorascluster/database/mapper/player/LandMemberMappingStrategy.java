package net.onelitefeather.pandorascluster.database.mapper.player;

import net.onelitefeather.pandorascluster.api.mapper.MapperStrategy;
import net.onelitefeather.pandorascluster.api.mapper.MappingContext;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import net.onelitefeather.pandorascluster.api.player.LandMember;
import net.onelitefeather.pandorascluster.api.player.LandPlayer;
import net.onelitefeather.pandorascluster.database.models.player.LandMemberEntity;
import net.onelitefeather.pandorascluster.database.models.player.LandPlayerEntity;

import java.util.function.Function;

/**
 * The member↔land-area back-reference is intentionally left null to avoid the
 * mapper cycle (LandArea → members → member.landArea → …). Callers that need
 * the parent area must look it up separately via {@code LandAreaService}.
 */
public final class LandMemberMappingStrategy implements MapperStrategy {

    @Override
    public Function<PandorasModel, PandorasModel> entityToModel() {
        return databaseEntity -> {
            if (databaseEntity == null) return null;
            if (!(databaseEntity instanceof LandMemberEntity landMemberEntity)) return null;
            return new LandMember(
                    landMemberEntity.id(),
                    getLandPlayer(landMemberEntity.member()),
                    landMemberEntity.role());
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
                    null);
        };
    }

    private LandPlayer getLandPlayer(LandPlayerEntity landPlayer) {
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

    public static LandMemberMappingStrategy create() {
        return new LandMemberMappingStrategy();
    }
}
