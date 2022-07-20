package net.onelitefeather.pandorascluster.menus;

import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import net.onelitefeather.pandorascluster.land.Land;
import net.onelitefeather.pandorascluster.util.Constants;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class LandMainMenu extends Gui {

    private final Land land;

    public LandMainMenu(Player player, int lines, String title, Land land) {
        super(player, lines, title);
        this.land = land;
    }

    @Override
    public void redraw() {
        if (isFirstDraw()) {

            addItem(ItemStackBuilder.of(Material.COMMAND_BLOCK_MINECART).name("&cSettings").buildItem().build());
            addItem(ItemStackBuilder.of(Material.PLAYER_HEAD).transformMeta(itemMeta -> {
                if (itemMeta instanceof SkullMeta skullMeta) {

                    UUID skullOwnerId = !land.getOwner().getUniqueId().equals(Constants.SERVER_UUID) ?
                            this.land.getOwner().getUniqueId() :
                            UUID.fromString("c06f8906-4c8a-4911-9c29-ea1dbd1aab82");
                    skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(skullOwnerId));
                }
            }).name("&eManage members").buildItem().build());
        }
    }
}
