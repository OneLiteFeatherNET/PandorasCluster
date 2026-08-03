package net.onelitefeather.pandorascluster.api.service.result.world;

import net.onelitefeather.pandorascluster.api.land.LandWorld;

public sealed interface GetLandWorldResult {

    record Found(LandWorld world) implements GetLandWorldResult {
    }

    record NotFound() implements GetLandWorldResult {
    }

    record Failed(String message, Throwable cause) implements GetLandWorldResult {
    }
}
