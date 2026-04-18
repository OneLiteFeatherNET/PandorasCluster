package net.onelitefeather.pandorascluster.api.service;

import net.onelitefeather.pandorascluster.api.land.LandArea;

/**
 * Outcome of {@link LandAreaService#getLandArea}. Exhaustive switch keeps
 * missing (chunk not claimed) and DB failures as distinct paths.
 */
public sealed interface GetLandAreaResult {

    record Found(LandArea area) implements GetLandAreaResult {
    }

    record NotFound() implements GetLandAreaResult {
    }

    record Failed(String message, Throwable cause) implements GetLandAreaResult {
    }
}
