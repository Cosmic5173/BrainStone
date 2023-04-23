package moe.seikimo.brainstone.api.body;

import lombok.Data;
import moe.seikimo.brainstone.base.Door;

@Data public final class CreateDoorRequest {
    private String name;
    private String description;
    private String key;
    private Door.DoorPosition position;
}
