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
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public final class FlagRegistry {

    private static final List<Flag<?>> knownFlags = new ArrayList<>();
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    public <T> void registerFlag(Flag<T> flag) {
        if (!knownFlags.contains(flag)) {
            knownFlags.add(flag);
        }
    }

    public static List<EntityCapFlag> getEntityCapFlags() {
        return knownFlags.stream().filter(EntityCapFlag.class::isInstance).map(EntityCapFlag.class::cast).toList();
    }

    public static List<RoleFlag> getRoleFlags() {
        return knownFlags.stream().filter(RoleFlag.class::isInstance).map(RoleFlag.class::cast).toList();
    }

    public static List<NaturalFlag> getNaturalFlags() {
        return knownFlags.stream().filter(NaturalFlag.class::isInstance).map(NaturalFlag.class::cast).toList();
    }

    @Nullable
    public static EntityCapFlag entityCapFlagOf(String name) {
        return getEntityCapFlags().stream()
                .filter(Objects::nonNull)
                .filter(flag -> flag.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    @Nullable
    public static RoleFlag roleFlagOf(String name) {
        return getRoleFlags().stream()
                .filter(Objects::nonNull).filter(flag -> flag.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    @Nullable
    public static NaturalFlag naturalFlagOf(String name) {
        return getNaturalFlags().stream()
                .filter(Objects::nonNull).filter(flag -> flag.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    public void loadDefaultFlags() {
        registerRoleFlags();
        registerNaturalFlags();
        registerEntityCapFlags();
    }

    private void registerEntityCapFlags() {

        String fileName = "entityCapFlags.json";

        try (InputStream stream = loadRessource(fileName);
             InputStreamReader inputStreamReader = new InputStreamReader(stream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            knownFlags.addAll(List.of(GSON.fromJson(bufferedReader, EntityCapImpl[].class)));
        } catch (IOException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot register default role flags.", e);
        }
    }

    private void registerRoleFlags() {
        String fileName = "roleFlags.json";
        try (InputStream stream = loadRessource(fileName);
             InputStreamReader inputStreamReader = new InputStreamReader(stream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            knownFlags.addAll(List.of(GSON.fromJson(bufferedReader, RoleFlagImpl[].class)));
        } catch (IOException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot register default role flags.", e);
        }
    }

    private void registerNaturalFlags() {

        String fileName = "naturalFlags.json";
        try (InputStream stream = loadRessource(fileName);
             InputStreamReader inputStreamReader = new InputStreamReader(stream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            knownFlags.addAll(List.of(GSON.fromJson(bufferedReader, NaturalFlagImpl[].class)));
        } catch (IOException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot register default natural flags.", e);
        }
    }

    private InputStream loadRessource(String path) throws IOException {
        var result = FlagRegistry.class.getClassLoader().getResourceAsStream(path);
        if (result == null) {
            throw new IOException("Could not find resource: " + path);
        }
        return result;
    }
}
