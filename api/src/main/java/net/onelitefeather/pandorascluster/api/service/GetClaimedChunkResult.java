package net.onelitefeather.pandorascluster.api.service;

import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk;

public sealed interface GetClaimedChunkResult {

    record Found(ClaimedChunk chunk) implements GetClaimedChunkResult {
    }

    record NotFound() implements GetClaimedChunkResult {
    }

    record Failed(String message, Throwable cause) implements GetClaimedChunkResult {
    }
}
