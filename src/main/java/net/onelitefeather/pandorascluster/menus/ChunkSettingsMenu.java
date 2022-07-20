package net.onelitefeather.pandorascluster.menus;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import net.kyori.adventure.text.Component;
import net.onelitefeather.pandorascluster.PandorasClusterPlugin;
import net.onelitefeather.pandorascluster.builder.ItemBuilder;
import net.onelitefeather.pandorascluster.chunk.WorldChunk;
import net.onelitefeather.pandorascluster.land.position.HomePosition;
import net.onelitefeather.pandorascluster.hook.ProtocolLibHook;
import net.onelitefeather.pandorascluster.menu.Closeable;
import net.onelitefeather.pandorascluster.menu.Menu;
import net.onelitefeather.pandorascluster.menu.event.MenuClickEvent;
import net.onelitefeather.pandorascluster.menu.event.MenuCloseEvent;
import net.onelitefeather.pandorascluster.service.EntityDataStoreService;
import net.onelitefeather.pandorascluster.util.Constants;
import net.onelitefeather.pandorascluster.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.stream.IntStream;

public final class ChunkSettingsMenu extends Menu implements Closeable {

    private final WorldChunk worldChunk;
    private PacketListener signUpdateListener;

    public ChunkSettingsMenu(@NotNull PandorasClusterPlugin pandorasClusterPlugin, @NotNull Player owner, @NotNull WorldChunk worldChunk) {
        super(pandorasClusterPlugin, owner);
        this.worldChunk = worldChunk;
    }

    @Override
    public String getName() {
        return "Chunk-Settings / " + getOwner().getServer().getOfflinePlayer(worldChunk.getOwner()).getName();
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleClick(@NotNull MenuClickEvent event) {

        if (event.getMenu() != this) return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getClickedItem();
        if (clickedItem == null) return;

        switch (clickedItem.getType()) {

            case PLAYER_HEAD -> {

                if (!player.hasPermission("featherchunks.set.owner")) {
                    player.sendMessage(Component.text("Dir fehlt die Berechtigung §6featherchunks.set.owner"));
                    player.closeInventory();
                    return;
                }

                ProtocolLibHook.openSignEditor(player);
                this.pandorasClusterPlugin.getEntityDataStoreService().applyPersistentData(player, EntityDataStoreService.MENU_CHANGE_CHUNK_OWNER, PersistentDataType.BYTE, (byte) 1);

                this.signUpdateListener = new PacketAdapter(this.pandorasClusterPlugin, PacketType.Play.Client.UPDATE_SIGN) {
                    @Override
                    public void onPacketReceiving(PacketEvent event) {

                        if (pandorasClusterPlugin.getEntityDataStoreService().has(player, EntityDataStoreService.MENU_CHANGE_CHUNK_OWNER)) {
                            String[] lines = event.getPacket().getStringArrays().read(0);

                            String playerName = lines[0];

                            UUID uuid = playerName.equalsIgnoreCase("server") ? Constants.SERVER_UUID : Bukkit.getPlayerUniqueId(playerName);
                            if (uuid == null) {
                                player.sendMessage(Component.text("Die UUID für " + playerName + " konnte nicht gefunden werden!"));
                                return;
                            }

                            //TODO: Change owner
                            player.sendMessage(Component.text("Du hast erfolgreich den Chunk Besitzer geändert."));
                        }

                        super.onPacketReceiving(event);
                    }
                };
            }

            case LIME_BED -> {

                if (!player.hasPermission("featherchunks.set.home")) {
                    player.sendMessage(Component.text("Dir fehlt die Berechtigung §6raconiachunks.set.home"));
                    player.closeInventory();
                    return;
                }

                this.worldChunk.setHome(HomePosition.of(player.getLocation()));
                this.pandorasClusterPlugin.getWorldChunkManager().updatePlayerChunk(this.worldChunk);
                player.sendMessage(Component.text("Du hast den Homepunkt auf deine aktuelle Position gesetzt."));
                player.closeInventory();
            }

            case RED_DYE -> Util.openChunkMainMenu(player, this.worldChunk).open();
        }
    }


    @Override
    public void setItems() {
        Inventory inventory = getInventory();
        inventory.setItem(11, new ItemBuilder(Material.LIME_BED, 1).displayName("&7Chunk &aHome").build());
        inventory.setItem(15, new ItemBuilder(Material.PLAYER_HEAD, 1).skullOwner(Constants.SERVER_UUID.equals(this.worldChunk.getOwner()) ? "MHF_STEVE" : Bukkit.getOfflinePlayer(this.worldChunk.getOwner()).getName()).displayName("&7Ändere den Chunk &eBesitzer&7.").build());
        inventory.setItem(13, new ItemBuilder(Material.RED_DYE, 1).displayName("&cZurück").build());
        IntStream.range(0, inventory.getSize()).filter(i -> inventory.getItem(i) == null).forEach(i -> inventory.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE, 1).displayName(" ").build()));

    }

    @Override
    public void handleClose(@NotNull MenuCloseEvent event) {
        if (event.getMenu() != this) return;
        ProtocolLibrary.getProtocolManager().removePacketListener(this.signUpdateListener);
    }
}
