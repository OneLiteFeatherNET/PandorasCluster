package net.onelitefeather.pandorascluster.api.service.result;

import net.onelitefeather.pandorascluster.api.land.Land;
import net.onelitefeather.pandorascluster.api.service.LandService;

/**
 * Outcome of {@link LandService#createLand}. Leaves room for future
 * domain-specific failures (e.g. {@code ChunkAlreadyClaimed},
 * {@code PlayerLimitReached}) without polluting a generic result type.
 */
public sealed interface CreateLandResult {

    record Created(Land land) implements CreateLandResult {
    }

    record Failed(String message, Throwable cause) implements CreateLandResult {
    }
}
