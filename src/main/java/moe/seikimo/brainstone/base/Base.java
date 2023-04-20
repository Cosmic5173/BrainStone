package moe.seikimo.brainstone.base;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
public final class Base {
    private final UUID id;
    @Setter
    private BaseInfo info;
    private final Map<UUID, Door> doors = new HashMap<>();

    public Base(UUID id, BaseInfo info, Map<UUID, Door> doors) {
        this.id = id;
        this.info = info;
        this.doors.putAll(doors);
    }

    public static Base fromSave(String save) {
        var split = save.split(";");
        var id = UUID.fromString(split[0]);
        var info = new BaseInfo(split[1], split[2], List.of(split[3].split(",")));

        var doors = new HashMap<UUID, Door>();
        if (split.length > 4 && !split[4].isEmpty()) {
            var doorsData = split[4].split(",,");
            for (var doorData : doorsData) {
                var door = Door.fromSave(doorData);
                doors.put(door.id(), door);
            }
        }
        return new Base(id, info, doors);
    }

    public boolean hasDoor(UUID id) {
        return doors.containsKey(id);
    }

    public void addDoor(Door door) {
        doors.put(door.id(), door);
    }

    public void removeDoor(UUID id) {
        doors.remove(id);
    }

    public Door getDoor(UUID id) {
        return doors.get(id);
    }

    public String toSave() {
        return String.format("%s;%s;%s;%s;%s", id, info.name, info.description, String.join(",", info.members), String.join(",,", doors.values().stream().map(Door::toSave).toList()));
    }

    @Override
    public String toString() {
        return String.format("Base{id=%s, info=%s, doors=%s}", id, info, doors.values().stream().toList());
    }


    public record BaseInfo(String name, String description, List<String> members) {

        @Override
        public String toString() {
            return String.format("BaseInfo{name='%s', description='%s', members=%s}", name, description, members);
        }
    }
}
