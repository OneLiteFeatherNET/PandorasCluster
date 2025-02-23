package net.onelitefeather.pandorascluster.dbo.land;

import net.onelitefeather.pandorascluster.dbo.chunk.ClaimedChunkDBO;
import net.onelitefeather.pandorascluster.dbo.flag.NaturalFlagDBO;
import net.onelitefeather.pandorascluster.dbo.flag.RoleFlagDBO;
import net.onelitefeather.pandorascluster.dbo.player.LandMemberDBO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface LandAreaDBO {

    @Nullable
    Long id();

    @NotNull
    String name();

    @NotNull
    List<LandMemberDBO> members();

    @NotNull
    List<ClaimedChunkDBO> chunks();

    @NotNull
    List<RoleFlagDBO> roleFlags();

    @NotNull
    List<NaturalFlagDBO> naturalFlags();

    LandDBO land();
}
