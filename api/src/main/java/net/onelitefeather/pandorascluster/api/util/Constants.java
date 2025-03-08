package net.onelitefeather.pandorascluster.api.util;

import java.util.UUID;
import java.util.logging.Logger;

public final class Constants {

    public static final UUID SERVER_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    public static final UUID EVERYONE = UUID.fromString("1-1-3-3-7");
    public static final Logger LOGGER = Logger.getLogger("PandorasCluster");
    public static final Integer IGNORE_CLAIM_LIMIT = -2;

    private Constants() {
        throw new IllegalStateException("Utility class");
    }

}
