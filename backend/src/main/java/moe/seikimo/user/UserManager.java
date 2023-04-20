package moe.seikimo.user;

import lombok.Getter;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserManager {

    @Getter
    private static final Map<UUID, User> users = new HashMap<>();
    private static final File userDirectory = new File("users");

    public static void init() {
        try {
            if (!userDirectory.exists() && !userDirectory.mkdir())
                throw new RuntimeException("Failed to create user directory");

            var userFiles = userDirectory.listFiles();
            if (userFiles == null)
                throw new RuntimeException("Failed to list user directory");

            for (File userFile : userFiles)
                registerUser(User.fromSave(Files.readString(userFile.toPath())));
        } catch (Exception e) {
            throw new RuntimeException("Failed to setup users.", e);
        }
    }

    public static void fini() {
        try {
            for (User user : users.values())
                Files.writeString(new File(userDirectory, user.getId().toString()).toPath(), user.toSave());
        } catch (Exception e) {
            throw new RuntimeException("Failed to save users.", e);
        }
    }

    public static boolean userExists(UUID id) {
        return users.containsKey(id);
    }

    public static void registerUser(User user) {
        users.put(user.getId(), user);
    }

    public static void unregisterUser(User user) {
        users.remove(user.getId());

        if (Files.exists(Path.of(userDirectory.getPath(), user.getId().toString()))) {
            var baseFile = new File(userDirectory, user.getId().toString());
            if (!baseFile.delete()) {
                System.out.println("ERROR: Unable to delete base file with id: " + user.getId().toString());
            }
        }
    }

    public static User getUser(UUID id) {
        return users.get(id);
    }

}
