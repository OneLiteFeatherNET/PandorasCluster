package net.onelitefeather.pandorascluster.builder;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class ItemBuilder {

    private final ItemStack itemStack;

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemBuilder(Material material, int amount) {
        this(new ItemStack(material, amount));
    }

    public ItemBuilder displayName(String displayName) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', displayName)));
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public <T, Z> ItemBuilder persistData(NamespacedKey key, PersistentDataType<T, Z> persistentDataType, Z value) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(key, persistentDataType, value);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.lore(lore.stream().map(s -> Component.text(ChatColor.translateAlternateColorCodes('&', s))).collect(Collectors.toList()));
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder enchantment(Enchantment enchantment, int level, boolean unsafe) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addEnchant(Objects.requireNonNull(EnchantmentWrapper.getByKey(enchantment.getKey())), level, unsafe);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder addItemFlag(ItemFlag itemFlag) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addItemFlags(itemFlag);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setTitle(String title) {
        BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
        bookMeta.setTitle(title);
        itemStack.setItemMeta(bookMeta);
        return this;
    }

    public ItemBuilder setAuthor(String author) {
        BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
        bookMeta.setAuthor(author);
        itemStack.setItemMeta(bookMeta);
        return this;
    }

    public ItemBuilder addPage(String text) {
        BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
        bookMeta.addPages(Component.text(text));
        itemStack.setItemMeta(bookMeta);
        return this;
    }

    public ItemBuilder skullOwner(OfflinePlayer offlinePlayer) {
        if(this.itemStack.getItemMeta() instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(offlinePlayer);
            this.itemStack.setItemMeta(skullMeta);
        }
        return this;
    }

    public ItemBuilder skullOwner(String name) {

        UUID uuid = Bukkit.getPlayerUniqueId(name);
        if(uuid == null) return this;

        if(this.itemStack.getItemMeta() instanceof SkullMeta skullMeta) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            if(offlinePlayer.hasPlayedBefore()) {
                skullMeta.setOwningPlayer(offlinePlayer);
            } else {
                PlayerProfile profile = Bukkit.createProfile(uuid, name);
                skullMeta.setPlayerProfile(profile);
            }

            this.itemStack.setItemMeta(skullMeta);
        }


        return this;
    }

    public ItemBuilder withCustomTexture(String texture, UUID uuid) {
        if(this.itemStack.getItemMeta() instanceof SkullMeta skullMeta) {
            PlayerProfile playerProfile = Bukkit.createProfile(uuid, ChatColor.stripColor(LegacyComponentSerializer.legacySection().serialize(Objects.requireNonNull(skullMeta.displayName()))));
            playerProfile.getProperties().add(new ProfileProperty("textures", texture));
            skullMeta.setPlayerProfile(playerProfile);
            itemStack.setItemMeta(skullMeta);
        }

        return this;
    }

    public ItemBuilder armorColor(Color color) {
        if (itemStack.getItemMeta() instanceof LeatherArmorMeta armorMeta) {
            armorMeta.setColor(color);
            itemStack.setItemMeta(armorMeta);
        }
        return this;
    }

    public ItemBuilder amount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder unbreakable() {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setUnbreakable(true);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemStack build() {
        return itemStack;
    }
}
