package moe.seikimo.user;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class User {

    @Getter
    private final UUID id;
    @Getter
    private final String name;
    @Getter
    private final Map<UUID, StasisChamber> stasisChambers = new HashMap<>();

    public static User fromSave(String save) {
        var split = save.split(";");
        var id = UUID.fromString(split[0]);
        var name = split[1];
        var user = new User(id, name);

        if (split.length >= 3 && !split[2].isEmpty()) {
            var stasisChambers = split[2].split(",");
            for (String stasisChamber : stasisChambers) {
                var stasisChamberSplit = stasisChamber.split(":");
                var stasisId = UUID.fromString(stasisChamberSplit[0]);
                var stasisKey = UUID.fromString(stasisChamberSplit[1]);
                user.addStasisChamber(new StasisChamber(user, stasisId, stasisKey));
            }
        }

        return user;
    }

    public String toSave() {
        var stasisChambersSave = new StringBuilder();
        for (StasisChamber stasisChamber : stasisChambers.values()) {
            stasisChambersSave.append(stasisChamber.id()).append(":").append(stasisChamber.key()).append(",");
        }

        return String.format("%s;%s;%s", id, name, stasisChambersSave);
    }

    public User(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public boolean hasStasisChamber(UUID id) {
        return stasisChambers.containsKey(id);
    }

    public void addStasisChamber(StasisChamber stasisChamber) {
        stasisChambers.put(stasisChamber.id(), stasisChamber);
    }

    public StasisChamber getStasisChamber(UUID id) {
        return stasisChambers.get(id);
    }

    public void removeStasisChamber(UUID id) {
        stasisChambers.remove(id);
    }

    public void removeStasisChamber(StasisChamber stasisChamber) {
        removeStasisChamber(stasisChamber.id());
    }

    @Override
    public String toString() {
        return String.format("User{id=%s, name='%s', stasisChambers=%s}", id, name, stasisChambers);
    }
}