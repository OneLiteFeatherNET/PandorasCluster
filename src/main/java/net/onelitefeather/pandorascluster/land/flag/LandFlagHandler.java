package net.onelitefeather.pandorascluster.land.flag;

import net.onelitefeather.pandorascluster.land.Land;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public record LandFlagHandler(Land land) {

    public void handleEntityChangeBlock(EntityChangeBlockEvent event) {
        LandFlagEntity landFlag = this.land.getFlag(LandFlag.BLOCK_FORM);
        if (landFlag != null && landFlag.<Boolean>getValue()) return;
        event.setCancelled(true);
    }
}
