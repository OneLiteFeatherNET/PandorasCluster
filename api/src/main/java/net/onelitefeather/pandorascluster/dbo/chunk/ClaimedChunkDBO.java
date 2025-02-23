package net.onelitefeather.pandorascluster.dbo.chunk;

import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import net.onelitefeather.pandorascluster.dbo.land.LandAreaDBO;
import org.jetbrains.annotations.Nullable;

public interface ClaimedChunkDBO extends PandorasModel {

    @Nullable
    Long id();

    Long chunkIndex();

    LandAreaDBO landArea();
}
