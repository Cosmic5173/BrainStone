package moe.seikimo.base;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BaseManager {

    private static final Map<UUID, Base> bases = new HashMap<>();
    private static final File baseDirectory = new File("bases");

    public static void init() {
        try {
            if (!baseDirectory.exists() && !baseDirectory.mkdir())
                throw new RuntimeException("Failed to create base directory");

            var baseFiles = baseDirectory.listFiles();
            if (baseFiles == null)
                throw new RuntimeException("Failed to list base directory");

            for (File baseFile : baseFiles)
                registerBase(Base.fromSave(Files.readString(baseFile.toPath())));
        } catch (Exception e) {
            throw new RuntimeException("Failed to setup bases.", e);
        }
    }

    public static void fini() {
        try {
            for (Base base : bases.values())
                Files.writeString(new File(baseDirectory, base.id().toString()).toPath(), base.toSave());
        } catch (Exception e) {
            throw new RuntimeException("Failed to save bases.", e);
        }
    }

    public static boolean baseExists(UUID id) {
        return bases.containsKey(id);
    }

    public static void registerBase(Base base) {
        bases.put(base.id(), base);
    }

    public static void unregisterBase(Base base) {
        bases.remove(base.id());

        if (Files.exists(Path.of(baseDirectory.getPath(), base.id().toString()))) {
            var baseFile = new File(baseDirectory, base.id().toString());
            if (!baseFile.delete()) {
                System.out.println("ERROR: Unable to delete base file with id: " + base.id().toString());
            }
        }
    }

    public static Base getBase(UUID id) {
        return bases.get(id);
    }

    public static Map<UUID, Base> getBases() {
        return bases;
    }
}
