package net.onelitefeather.pandorascluster.menus;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import net.kyori.adventure.text.Component;
import net.onelitefeather.pandorascluster.PandorasClusterPlugin;
import net.onelitefeather.pandorascluster.builder.ItemBuilder;
import net.onelitefeather.pandorascluster.enums.LandRole;
import net.onelitefeather.pandorascluster.chunk.WorldChunk;
import net.onelitefeather.pandorascluster.hook.ProtocolLibHook;
import net.onelitefeather.pandorascluster.menu.Closeable;
import net.onelitefeather.pandorascluster.menu.PaginatedMenu;
import net.onelitefeather.pandorascluster.menu.event.MenuClickEvent;
import net.onelitefeather.pandorascluster.menu.event.MenuCloseEvent;
import net.onelitefeather.pandorascluster.player.ChunkPlayer;
import net.onelitefeather.pandorascluster.service.EntityDataStoreService;
import net.onelitefeather.pandorascluster.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.IntStream;

public class ChunkPlayerManagerMenu extends PaginatedMenu implements Closeable {

    private Filter filter;
    private final WorldChunk worldChunk;
    private int currentFilterIndex;

    private int totalPages = 1;
    private int currentPage = 1;
    private UUID targetPlayer;

    private PacketListener signUpdateListener;

    public ChunkPlayerManagerMenu(PandorasClusterPlugin pandorasClusterPlugin, Player owner, int maxItemsPerPage, WorldChunk worldChunk) {
        super(pandorasClusterPlugin, owner, maxItemsPerPage);
        this.currentFilterIndex = 0;
        this.worldChunk = worldChunk;
        this.filter = Filter.ALL;
    }

    @Override
    public String getName() {
        return "Mitgliederverwaltung";
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleClick(@NotNull MenuClickEvent event) {

        if(event.getMenu() != this) return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack currentItem = event.getClickedItem();
        if (currentItem == null) return;

        ItemMeta itemMeta = currentItem.getItemMeta();
        if (itemMeta == null) return;

        switch (currentItem.getType()) {

            case LEVER -> {
                Filter[] filters = Filter.values();
                if ((this.currentFilterIndex + 1) <= filters.length - 1) {
                    this.currentFilterIndex++;
                } else {
                    this.currentFilterIndex = 0;
                }
                this.filter = filters[this.currentFilterIndex];
                itemMeta.displayName(Component.text(color("&7Filter: &e" + this.filter.name())));
                currentItem.setItemMeta(itemMeta);
                refreshFilter();
            }

            case PLAYER_HEAD -> {
                if (currentItem.getItemMeta() instanceof SkullMeta skullMeta) {
                    OfflinePlayer owner = skullMeta.getOwningPlayer();
                    if (owner != null) {

                    }
                }
            }

            case RED_DYE -> Util.openChunkMainMenu(player, this.worldChunk).open();

            case LIME_DYE -> {

                ProtocolLibHook.openSignEditor(player);
                this.pandorasClusterPlugin.getEntityDataStoreService().applyPersistentData(player, EntityDataStoreService.MENU_TRUST_PLAYER, PersistentDataType.BYTE, (byte) 1);

                this.signUpdateListener = new PacketAdapter(this.pandorasClusterPlugin, PacketType.Play.Client.UPDATE_SIGN) {
                    @Override
                    public void onPacketReceiving(PacketEvent event) {

                        if (pandorasClusterPlugin.getEntityDataStoreService().has(player, EntityDataStoreService.MENU_TRUST_PLAYER)) {
                            String[] lines = event.getPacket().getStringArrays().read(0);
                            List<String> names = new ArrayList<>();
                            for (String line : lines) {
                                ChunkPlayer chunkPlayer = pandorasClusterPlugin.getChunkPlayerService().getChunkPlayer(line);
                                if (chunkPlayer == null) continue;
                                if (worldChunk.hasAccess(chunkPlayer.getUniqueId())) continue;
                                worldChunk.setPlayerRole(chunkPlayer.getUniqueId(), LandRole.MEMBER);
                                names.add(line);
                            }

                            player.sendMessage(String.format("The following players have now access. (%s)", String.join(", ", names)));
                        }

                        super.onPacketReceiving(event);
                    }
                };

                ProtocolLibrary.getProtocolManager().addPacketListener(this.signUpdateListener);
            }

            case STONE_BUTTON -> {
                if (getPage() == 1) return;
                this.currentPage -= 1;
                super.open();
            }

            case OAK_BUTTON -> {
                if ((getPage() + 1) <= getTotalPages()) {
                    this.currentPage += 1;
                    super.open();
                }
            }
        }

    }

    @Override
    public void setItems() {
        Inventory inventory = getInventory();

        setFilteredItems();

        inventory.setItem(45, new ItemBuilder(Material.LEVER, 1).displayName("&7Filter: &e" + this.filter.name()).build());
        inventory.setItem(46, new ItemBuilder(Material.LIME_DYE, 1).displayName("&aHinzufügen").build());

        inventory.setItem(48, new ItemBuilder(Material.STONE_BUTTON, 1).displayName("&cSeite zurück").build());
        inventory.setItem(50, new ItemBuilder(Material.OAK_BUTTON, 1).displayName("&aSeite weiter").build());

        inventory.setItem(53, new ItemBuilder(Material.RED_DYE, 1).displayName("&cZurück").build());
    }

    @Override
    public int getTotalPages() {
        return totalPages;
    }

    @Override
    public int getPage() {
        return this.currentPage;
    }

    private void setFilteredItems() {
        Inventory inventory = getInventory();

        List<UUID> list = filter(this.filter);
        int currentPage = getPage();
        int totalPageCount = 0;

        if ((list.size() % getMaxItemsPerPage()) == 0) {
            if (list.size() > 0) totalPageCount = list.size() / getMaxItemsPerPage();
        } else {
            totalPageCount = (list.size() / getMaxItemsPerPage()) + 1;
        }

        if (currentPage <= totalPageCount) {
            int i = 0, k = 0;
            currentPage--;

            for (UUID uuid : list) {
                k++;
                if ((((currentPage * getMaxItemsPerPage()) + i + 1) == k) && (k != ((currentPage * getMaxItemsPerPage()) + getMaxItemsPerPage() + 1))) {
                    i++;
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                    inventory.setItem(inventory.firstEmpty(), new ItemBuilder(Material.PLAYER_HEAD, 1).skullOwner(offlinePlayer).displayName(this.worldChunk.getPlayerRole(uuid).getDisplay() + offlinePlayer.getName()).build());
                }
            }

            totalPages = totalPageCount;
        }
    }

    private List<UUID> filter(Filter filter) {

        List<UUID> admins = this.worldChunk.getPlayers(LandRole.ADMIN);
        List<UUID> trusted = this.worldChunk.getPlayers(LandRole.TRUSTED);
        List<UUID> members = this.worldChunk.getPlayers(LandRole.MEMBER);
        List<UUID> banned = this.worldChunk.getPlayers(LandRole.BANNED);

        switch (filter) {

            case BANNED -> {
                return banned;
            }

            case MEMBERS -> {
                return members;
            }

            case ADMINS -> {
                return admins;
            }

            default -> {
                List<UUID> list = new ArrayList<>();
                list.addAll(banned);
                list.addAll(admins);
                list.addAll(trusted);
                list.addAll(members);
                return list;
            }
        }
    }

    private void refreshFilter() {
        Inventory inventory = getInventory();
        IntStream.range(0, inventory.getSize()).mapToObj(inventory::getItem).filter(Objects::nonNull).filter(itemStack -> itemStack.getItemMeta() != null && itemStack.getItemMeta() instanceof SkullMeta).forEach(inventory::remove);
        setFilteredItems();
    }

    @Override
    public void handleClose(@NotNull MenuCloseEvent event) {
        if (event.getMenu() != this) return;
        ProtocolLibrary.getProtocolManager().removePacketListener(this.signUpdateListener);
    }

    public enum Filter {
        ALL,
        ADMINS,
        TRUSTED,
        MEMBERS,
        BANNED;
    }
}
