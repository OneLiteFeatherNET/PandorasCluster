package net.onelitefeather.pandorascluster.dto.land;

import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import net.onelitefeather.pandorascluster.dto.chunk.ClaimedChunkDto;
import net.onelitefeather.pandorascluster.dto.player.LandMemberDto;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface LandAreaDto extends PandorasModel {

    @Nullable
    Long id();

    @NotNull
    String name();

    @NotNull
    List<LandMemberDto> members();

    @NotNull
    List<ClaimedChunkDto> chunks();

    LandDto land();
}
