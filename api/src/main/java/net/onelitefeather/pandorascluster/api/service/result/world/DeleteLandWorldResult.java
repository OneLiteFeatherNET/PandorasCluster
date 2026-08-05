package net.onelitefeather.pandorascluster.api.service.result.world;

import net.onelitefeather.pandorascluster.api.land.LandWorld;

public sealed interface DeleteLandWorldResult {

    record Success(LandWorld world) implements DeleteLandWorldResult {
    }

    record NotFound() implements DeleteLandWorldResult {}

    record Failed(String message, Throwable cause) implements DeleteLandWorldResult {
    }
}
