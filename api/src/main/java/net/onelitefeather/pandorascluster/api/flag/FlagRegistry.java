package net.onelitefeather.pandorascluster.api.flag;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.onelitefeather.pandorascluster.api.flag.impl.EntityCapImpl;
import net.onelitefeather.pandorascluster.api.flag.impl.NaturalFlagImpl;
import net.onelitefeather.pandorascluster.api.flag.impl.RoleFlagImpl;
import net.onelitefeather.pandorascluster.api.flag.types.EntityCapFlag;
import net.onelitefeather.pandorascluster.api.flag.types.NaturalFlag;
import net.onelitefeather.pandorascluster.api.flag.types.RoleFlag;
import net.onelitefeather.pandorascluster.api.util.Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public final class FlagRegistry {

    private static final List<Flag<?>> knownFlags = new ArrayList<>();
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    public <T> void registerFlag(Flag<T> flag) {
        if (!knownFlags.contains(flag)) {
            knownFlags.add(flag);
        }
    }

    @Nullable
    public static EntityCapFlag entityCapFlagOf(String name) {
        return (EntityCapFlag) knownFlags.stream()
                .filter(EntityCapFlag.class::isInstance)
                .filter(flag -> flag.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    @Nullable
    public static RoleFlag roleFlagOf(String name) {
        return (RoleFlag) knownFlags.stream()
                .filter(RoleFlag.class::isInstance).filter(flag -> flag.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    @Nullable
    public static NaturalFlag naturalFlagOf(String name) {
        return (NaturalFlag) knownFlags.stream()
                .filter(NaturalFlag.class::isInstance).filter(flag -> flag.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    public void loadDefaultFlags(@NotNull Path dataFolder) {
        registerRoleFlags(dataFolder);
        registerNaturalFlags(dataFolder);
        registerEntityCapFlags(dataFolder);
    }

    private void registerEntityCapFlags(@NotNull Path dataFolder) {
        Path roleFlagsFlagsFilePath = dataFolder.resolve("entityCapFlags.json");
        try (BufferedReader bufferedReader = Files.newBufferedReader(roleFlagsFlagsFilePath)) {
            knownFlags.addAll(List.of(GSON.fromJson(bufferedReader, EntityCapImpl[].class)));
        } catch (IOException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot register default role flags.", e);
        }
    }

    private void registerRoleFlags(@NotNull Path dataFolder) {
        Path roleFlagsFlagsFilePath = dataFolder.resolve("roleFlags.json");
        try (BufferedReader bufferedReader = Files.newBufferedReader(roleFlagsFlagsFilePath)) {
            knownFlags.addAll(List.of(GSON.fromJson(bufferedReader, RoleFlagImpl[].class)));
        } catch (IOException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot register default role flags.", e);
        }
    }

    private void registerNaturalFlags(@NotNull Path dataFolder) {
        Path roleFlagsFlagsFilePath = dataFolder.resolve("naturalFlags.json");
        try (BufferedReader bufferedReader = Files.newBufferedReader(roleFlagsFlagsFilePath)) {
            knownFlags.addAll(List.of(GSON.fromJson(bufferedReader, NaturalFlagImpl[].class)));
        } catch (IOException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot register default natural flags.", e);
        }
    }
}
