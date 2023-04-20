package moe.seikimo.base;

import java.util.List;
import java.util.UUID;

public record Base(UUID id, BaseInfo info) {

    public static Base fromSave(String save) {
        var split = save.split(";");
        var id = UUID.fromString(split[0]);
        var info = new BaseInfo(split[1], split[2], List.of(split[3].split(",")));
        return new Base(id, info);
    }

    public String toSave() {
        return String.format("%s;%s;%s;%s", id, info.name, info.description, String.join(",", info.members));
    }

    @Override
    public String toString() {
        return String.format("Base{id=%s, info=%s}", id, info);
    }

    public record BaseInfo(String name, String description, List<String> members) {

        @Override
        public String toString() {
            return String.format("BaseInfo{name='%s', description='%s', members=%s}", name, description, members);
        }
    }
}
