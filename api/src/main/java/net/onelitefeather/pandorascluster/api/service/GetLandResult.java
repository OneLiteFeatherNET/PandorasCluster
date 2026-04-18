package net.onelitefeather.pandorascluster.api.service;

import net.onelitefeather.pandorascluster.api.land.Land;

/**
 * Outcome of {@link LandService#getLand(Long)}. Exhaustive switch forces
 * callers to distinguish a missing row ({@link NotFound}) from a database
 * failure ({@link Failed}) — silent null-handling is no longer possible.
 */
public sealed interface GetLandResult {

    record Found(Land land) implements GetLandResult {
    }

    record NotFound() implements GetLandResult {
    }

    record Failed(String message, Throwable cause) implements GetLandResult {
    }
}
