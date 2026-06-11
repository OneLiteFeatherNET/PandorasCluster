package net.onelitefeather.pandorascluster.api.service.result;

import net.onelitefeather.pandorascluster.api.land.flag.LandFlag;

public sealed interface AddLandFlagResult {

    record Added(LandFlag flag) implements AddLandFlagResult {}
    record AlreadyAdded(String message) implements AddLandFlagResult {}
    record Failed(String message) implements AddLandFlagResult {}

}
