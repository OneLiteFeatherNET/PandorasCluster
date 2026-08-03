package net.onelitefeather.pandorascluster.api.service.result.land;

import net.onelitefeather.pandorascluster.api.land.LandArea;

public sealed interface DeleteLandAreaResult {
    record Success(LandArea area) implements DeleteLandAreaResult {}
    record Failed(String message, Throwable cause) implements DeleteLandAreaResult {}
}
