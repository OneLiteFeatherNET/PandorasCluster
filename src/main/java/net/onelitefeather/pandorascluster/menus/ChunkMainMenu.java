package net.onelitefeather.pandorascluster.menus;

import net.onelitefeather.pandorascluster.PandorasClusterPlugin;
import net.onelitefeather.pandorascluster.builder.ItemBuilder;
import net.onelitefeather.pandorascluster.chunk.WorldChunk;
import net.onelitefeather.pandorascluster.menu.Menu;
import net.onelitefeather.pandorascluster.menu.event.MenuClickEvent;
import net.onelitefeather.pandorascluster.util.Constants;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.stream.IntStream;

public class ChunkMainMenu extends Menu {

    private final WorldChunk worldChunk;
    private final String targetName;

    public ChunkMainMenu(PandorasClusterPlugin pandorasClusterPlugin, Player owner, WorldChunk worldChunk) {
        super(pandorasClusterPlugin, owner);
        this.worldChunk = worldChunk;
        this.targetName = this.worldChunk.getOwner().equals(Constants.SERVER_UUID) ? "Server" : Bukkit.getOfflinePlayer(this.worldChunk.getOwner()).getName();
    }

    public WorldChunk getWorldChunk() {
        return worldChunk;
    }

    @Override
    public String getName() {
        return "Chunk-Settings / " + this.targetName;
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleClick(@NotNull MenuClickEvent event) {

        Menu menu = event.getMenu();
        if(menu != this) return;

        event.setCancelled(true);

        Player player = event.getWhoClicked();
        ItemStack clickedItem = event.getClickedItem();
        if (clickedItem == null) return;

        ItemMeta itemMeta = clickedItem.getItemMeta();
        if (itemMeta == null) return;

        switch (clickedItem.getType()) {
            case PLAYER_HEAD -> new ChunkPlayerManagerMenu(this.pandorasClusterPlugin, this.getOwner(), 45, this.worldChunk).open();
            case COMMAND_BLOCK_MINECART -> new ChunkSettingsMenu(this.pandorasClusterPlugin, player, this.pandorasClusterPlugin.getWorldChunkManager().getWorldChunk(player.getChunk())).open();
        }
    }

    @Override
    public void setItems() {
        Inventory inventory = getInventory();
        inventory.setItem(10, new ItemBuilder(Material.PLAYER_HEAD, 1).skullOwner(!worldChunk.getOwner().equals(Constants.SERVER_UUID) ? Bukkit.getOfflinePlayer(this.worldChunk.getOwner()).getName() : "MHF_Steve").displayName("&eMitgliederverwaltung").build());
        inventory.setItem(12, new ItemBuilder(Material.COMMAND_BLOCK_MINECART, 1).displayName("&bEinstellungen").build());
        IntStream.range(0, inventory.getSize()).filter(i -> inventory.getItem(i) == null).forEach(i -> inventory.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE, 1).displayName(" ").build()));
    }

}
