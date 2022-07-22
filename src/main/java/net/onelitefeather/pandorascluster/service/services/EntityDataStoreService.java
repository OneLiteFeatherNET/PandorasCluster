package net.onelitefeather.pandorascluster.service.services;

import net.onelitefeather.pandorascluster.api.PandorasClusterApi;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class EntityDataStoreService {

    public static final String MENU_TRUST_PLAYER = "trust_player";
    public static final String MENU_CHANGE_CHUNK_OWNER = "change_chunk_owner";

    private final PandorasClusterApi api;
    private final List<NamespacedKey> nameSpacedKeys;

    public EntityDataStoreService(@NotNull PandorasClusterApi api) {
        this.api = api;
        this.nameSpacedKeys = new ArrayList<>();
    }

    @NotNull
    public List<NamespacedKey> getNameSpacedKeys() {
        return nameSpacedKeys;
    }

    @Nullable
    public NamespacedKey getNameSpacedKey(@NotNull String key) {

        NamespacedKey result = null;

        for (int i = 0; i < this.nameSpacedKeys.size() && result == null; i++) {
            NamespacedKey namespacedKey = this.nameSpacedKeys.get(i);
            if (namespacedKey.getKey().equalsIgnoreCase(key)) {
                result = namespacedKey;
            }
        }

        return result;
    }

    public boolean has(@NotNull PersistentDataHolder dataHolder, @NotNull String key) {
        NamespacedKey namespacedKey = getNameSpacedKey(key);
        if (namespacedKey == null) return false;
        return dataHolder.getPersistentDataContainer().has(namespacedKey);
    }

    public void removePersistentData(@NotNull PersistentDataHolder dataHolder, @NotNull String key) {
        if (!has(dataHolder, key)) return;
        dataHolder.getPersistentDataContainer().remove(Objects.requireNonNull(getNameSpacedKey(key)));
    }

    public <T, Z> void applyPersistentData(PersistentDataHolder dataHolder,
                                           @NotNull String key,
                                           @NotNull PersistentDataType<T, Z> type,
                                           @NotNull Z value) {

        NamespacedKey namespacedKey = getNameSpacedKey(key);
        if (namespacedKey == null) {
            namespacedKey = new NamespacedKey(this.api.getPlugin(), key);
            this.nameSpacedKeys.add(namespacedKey);
        }

        dataHolder.getPersistentDataContainer().set(namespacedKey, type, value);
    }
}
