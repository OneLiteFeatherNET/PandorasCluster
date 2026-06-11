package net.onelitefeather.pandorascluster.api.service.result;

import java.util.UUID;

public sealed interface CreateLandPlayerResult {

        record Created(UUID uuid, String name) implements CreateLandPlayerResult {}

        record AlreadyCreated(String message) implements CreateLandPlayerResult {}

        record Failed(String message, Throwable cause) implements CreateLandPlayerResult {}
}
