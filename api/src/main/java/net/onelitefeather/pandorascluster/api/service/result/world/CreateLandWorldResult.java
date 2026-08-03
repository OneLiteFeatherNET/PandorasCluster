package net.onelitefeather.pandorascluster.api.service.result.world;

import net.onelitefeather.pandorascluster.api.land.LandWorld;

public sealed interface CreateLandWorldResult {
    record Created(LandWorld world) implements CreateLandWorldResult {}
    record Failed(String message, Throwable cause) implements CreateLandWorldResult {}
}
