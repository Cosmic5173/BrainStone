package moe.seikimo.brainstone.user;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter public final class User {
    private final UUID id;
    private final String name;
    @Setter
    private String baseId;
    private final Map<UUID, StasisChamber> stasisChambers
            = new HashMap<>();

    public static User fromSave(String save) {
        var split = save.split(";");
        var user = new User(UUID.fromString(split[0]), split[1], split[2]);

        if (split.length >= 4 && !split[3].isEmpty()) {
            var stasisChambers = split[3].split(",");
            for (var stasisChamber : stasisChambers) {
                var stasisChamberSplit = stasisChamber.split(":");
                user.addStasisChamber(new StasisChamber(user, UUID.fromString(stasisChamberSplit[0]), UUID.fromString(stasisChamberSplit[1])));
            }
        }

        return user;
    }

    public String toSave() {
        var stasisChambersSave = new StringBuilder();
        for (var stasisChamber : this.stasisChambers.values()) {
            stasisChambersSave
                    .append(stasisChamber.id())
                    .append(":")
                    .append(stasisChamber.key())
                    .append(",");
        }

        return String.format("%s;%s;%s;%s", id, name, baseId, stasisChambersSave);
    }

    public User(UUID id, String name, String base) {
        this.id = id;
        this.name = name;
        this.baseId = base;
    }

    public boolean hasStasisChamber(UUID id) {
        return this.stasisChambers.containsKey(id);
    }

    public void addStasisChamber(StasisChamber stasisChamber) {
        this.stasisChambers.put(stasisChamber.id(), stasisChamber);
    }

    public StasisChamber getStasisChamber(UUID id) {
        return stasisChambers.get(id);
    }

    public void removeStasisChamber(UUID id) {
        this.stasisChambers.remove(id);
    }

    public void removeStasisChamber(StasisChamber stasisChamber) {
        this.removeStasisChamber(stasisChamber.id());
    }

    @Override
    public String toString() {
        return String.format("User{id=%s, name='%s', base=%s, stasisChambers=%s}",
                this.id, this.name, this.baseId, this.stasisChambers);
    }
}
