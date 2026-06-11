package net.onelitefeather.pandorascluster.api.service.result;

import net.onelitefeather.pandorascluster.api.land.LandArea;

public sealed interface CreateLandAreaResult {

    record Created(LandArea landArea) implements CreateLandAreaResult {
    }

    record AlreadyAssigned(String name) implements CreateLandAreaResult {}

    record AlreadyCreated(String name) implements CreateLandAreaResult {
    }

    record Failed(String message, Throwable cause) implements CreateLandAreaResult {
    }
}
