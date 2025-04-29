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
import java.util.logging.Level;

public final class FlagRegistry {

    private static FlagRegistry instance;

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

    public void loadDefaultFlags() {
        registerRoleFlags();
        registerNaturalFlags();
        registerEntityCapFlags();
    }

    public static FlagRegistry getInstance() {
        if (instance == null) instance = new FlagRegistry();
        return instance;
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
