package net.onelitefeather.pandorascluster.api.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.onelitefeather.pandorascluster.api.flag.Flag;
import net.onelitefeather.pandorascluster.api.flag.impl.EntityCapImpl;
import net.onelitefeather.pandorascluster.api.flag.impl.NaturalFlagImpl;
import net.onelitefeather.pandorascluster.api.flag.impl.RoleFlagImpl;
import net.onelitefeather.pandorascluster.api.flag.types.EntityCapFlag;
import net.onelitefeather.pandorascluster.api.flag.types.NaturalFlag;
import net.onelitefeather.pandorascluster.api.flag.types.RoleFlag;
import net.onelitefeather.pandorascluster.api.util.Constants;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public final class FlagRegistryServiceImpl implements FlagRegistryService {

    private static final String ENTITY_CAP_FLAGS_FILE_NAME = "entityCapFlags.json";
    private static final String ROLE_FLAGS_FILE_NAME = "roleFlags.json";
    private static final String NATURAL_FLAGS_FILE_NAME = "naturalFlags.json";
    private static final Path PLUGIN_FOLDER = Path.of("plugins", "PandorasCluster");
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    private final List<Flag<?>> knownFlags;

    public FlagRegistryServiceImpl() {
        this.knownFlags = new ArrayList<>();
    }

    public <T> void registerFlag(Flag<T> flag) {
        if (!knownFlags.contains(flag)) {
            knownFlags.add(flag);
        }
    }

    @Override
    public List<EntityCapFlag> getEntityCapFlags() {
        return knownFlags.stream().filter(EntityCapFlag.class::isInstance).map(EntityCapFlag.class::cast).toList();
    }

    @Override
    public List<RoleFlag> getRoleFlags() {
        return knownFlags.stream().filter(RoleFlag.class::isInstance).map(RoleFlag.class::cast).toList();
    }

    @Override
    public List<NaturalFlag> getNaturalFlags() {
        return knownFlags.stream().filter(NaturalFlag.class::isInstance).map(NaturalFlag.class::cast).toList();
    }

    public void loadDefaultFlags() {
        registerRoleFlags();
        registerNaturalFlags();
        registerEntityCapFlags();
    }

    private void registerEntityCapFlags() {

        if (Files.notExists(PLUGIN_FOLDER.resolve(ENTITY_CAP_FLAGS_FILE_NAME)))
            saveResource(ENTITY_CAP_FLAGS_FILE_NAME, false);

        try (InputStream stream = getResource(ENTITY_CAP_FLAGS_FILE_NAME)) {
            if (stream == null)
                throw new FileNotFoundException("File %s was not found!".formatted(ENTITY_CAP_FLAGS_FILE_NAME));
            try (InputStreamReader inputStreamReader = new InputStreamReader(stream)) {
                knownFlags.addAll(List.of(GSON.fromJson(inputStreamReader, EntityCapImpl[].class)));
            }
        } catch (IOException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot register default entity cap flags.", e);
        }
    }

    private void registerRoleFlags() {

        if (Files.notExists(PLUGIN_FOLDER.resolve(ROLE_FLAGS_FILE_NAME)))
            saveResource(ROLE_FLAGS_FILE_NAME, false);

        try (InputStream stream = getResource(ROLE_FLAGS_FILE_NAME)) {
            if (stream == null)
                throw new FileNotFoundException("File %s was not found!".formatted(ROLE_FLAGS_FILE_NAME));
            try (InputStreamReader inputStreamReader = new InputStreamReader(stream)) {
                knownFlags.addAll(List.of(GSON.fromJson(inputStreamReader, RoleFlagImpl[].class)));
            }
        } catch (IOException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot register default role flags.", e);
        }
    }

    private void registerNaturalFlags() {

        if (Files.notExists(PLUGIN_FOLDER.resolve(NATURAL_FLAGS_FILE_NAME)))
            saveResource(NATURAL_FLAGS_FILE_NAME, false);

        try (InputStream stream = getResource(NATURAL_FLAGS_FILE_NAME)) {
            if (stream == null)
                throw new FileNotFoundException("File %s was not found!".formatted(NATURAL_FLAGS_FILE_NAME));
            try (InputStreamReader inputStreamReader = new InputStreamReader(stream)) {
                knownFlags.addAll(List.of(GSON.fromJson(inputStreamReader, NaturalFlagImpl[].class)));
            }
        } catch (IOException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot register default natural flags.", e);
        }
    }

    private void saveResource(String resourcePath, boolean replace) {
        if (resourcePath == null || resourcePath.isEmpty())
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");

        resourcePath = resourcePath.replace('\\', '/');

        File outFile = PLUGIN_FOLDER.resolve(resourcePath).toFile();
        File outDir = PLUGIN_FOLDER.resolve(resourcePath.substring(0, Math.max(resourcePath.lastIndexOf('/'), 0))).toFile();

        try (InputStream in = getResource(resourcePath)) {
            if (in == null)
                throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in resources folder!");

            if (!Files.notExists(outDir.toPath())) Files.createDirectories(outDir.toPath());
            if (Files.exists(outFile.toPath()) || !replace) {
                Constants.LOGGER.log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
                return;
            }

            try (OutputStream out = new FileOutputStream(outFile)) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        } catch (IOException e) {
            Constants.LOGGER.log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, e);
        }
    }

    private @Nullable InputStream getResource(String filename) {
        if (filename == null) throw new IllegalArgumentException("Filename cannot be null");
        try {
            URL url = this.getClass().getClassLoader().getResource(filename);
            if (url == null) return null;
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }
}
