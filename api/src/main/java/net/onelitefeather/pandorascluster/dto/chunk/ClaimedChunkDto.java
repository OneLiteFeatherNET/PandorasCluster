package net.onelitefeather.pandorascluster.dto.chunk;

import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import net.onelitefeather.pandorascluster.dto.land.LandAreaDto;
import org.jetbrains.annotations.Nullable;

public interface ClaimedChunkDto extends PandorasModel {

    @Nullable
    Long id();

    Long chunkIndex();

    LandAreaDto landArea();
}
