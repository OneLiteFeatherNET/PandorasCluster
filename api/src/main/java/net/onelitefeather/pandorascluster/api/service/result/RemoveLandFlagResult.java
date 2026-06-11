package net.onelitefeather.pandorascluster.api.service.result;

import net.onelitefeather.pandorascluster.api.land.flag.LandFlag;

public sealed interface RemoveLandFlagResult {
    record Success(LandFlag flag) implements RemoveLandFlagResult {}
    record NotFound(LandFlag flag) implements RemoveLandFlagResult {}
    record Failed(LandFlag flag) implements RemoveLandFlagResult {}
}
