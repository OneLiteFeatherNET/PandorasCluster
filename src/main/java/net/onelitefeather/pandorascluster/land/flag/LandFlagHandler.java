package net.onelitefeather.pandorascluster.land.flag;

import net.onelitefeather.pandorascluster.land.Land;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public final class LandFlagHandler {

    private final Land land;

    public LandFlagHandler(Land land) {
        this.land = land;
    }


    public void handleEntityChangeBlock(EntityChangeBlockEvent event) {
        LandFlagEntity landFlag = this.land.getFlag(LandFlag.BLOCK_FORM);
        if (landFlag != null && landFlag.<Boolean>getValue()) return;
        event.setCancelled(true);
    }
}
