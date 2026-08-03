package net.onelitefeather.pandorascluster.api.service.result.land;

import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk;

public sealed interface UnclaimChunkResult {

    record Success(ClaimedChunk chunk) implements UnclaimChunkResult {}
    record NotFound() implements UnclaimChunkResult {}
    record Failed(String message, Throwable cause) implements UnclaimChunkResult {}
}
