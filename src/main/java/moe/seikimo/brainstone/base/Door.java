package moe.seikimo.brainstone.base;

import moe.seikimo.brainstone.Brain;
import moe.seikimo.brainstone.util.Callback;
import okhttp3.Request;
import org.eclipse.jetty.util.FuturePromise;


import java.util.UUID;
import java.util.concurrent.Future;

public final class Door {
    private final UUID id;
    private final DoorInfo info;
    private final DoorPosition position;

    private transient boolean open = false;

    public Door(UUID id, DoorInfo info, DoorPosition position) {
        this.id = id;
        this.info = info;
        this.position = position;
    }

    public static Door fromSave(String save) {
        var split = save.split("::");
        var id = UUID.fromString(split[0]);
        var info = new DoorInfo(split[1], split[2], UUID.fromString(split[3]));
        var position = Brain.getGson().fromJson(split[4], DoorPosition.class);
        return new Door(id, info, position);
    }

    public boolean isOpen() {
        return open;
    }

    public Future<Boolean> open() {
        if (open) {
            return new FuturePromise<>(true);
        } else {
            open = true;
            return activate(true);
        }
    }

    public Future<Boolean> close() {
        if (!open) {
            return new FuturePromise<>(true);
        } else {
            open = false;
            return activate(false);
        }
    }

    private Future<Boolean> activate(boolean open) {
        return activate(0, open);
    }

    private Future<Boolean> activate(int calls, boolean open) {
        // Check if the method has been called too many times.
        if (calls > 4) {
            Brain.getLogger().info("Failed to activate door: {}. Too many failed attempts.", info.name());
            return new FuturePromise<>(false);
        }

        // Activate the stasis chamber.
        var request = new Request.Builder()
                .url(Brain.endpoint(this.info.key()))
                .build();
        var future = new FuturePromise<Boolean>();
        Callback.makeRequest(request, response -> {
            try {
                // Check if the response is null.
                if (response == null) {
                    future.succeeded(false);
                    return;
                }
                // Check the response code of the request.
                if (response.code() != 200) {
                    future.succeeded(false);
                    return;
                }

                // Check the response body of the request.
                var body = response.body();
                if (body == null) {
                    future.succeeded(false);
                    return;
                }

                // Check if the response body is equal to "true".
                if (!body.string().equals(open ? "true" : "false")) {
                    future.succeeded(activate(calls + 1, open).get());
                } else {
                    future.succeeded(true);
                    Brain.getLogger().info("Successfully activated door: " + info.name());
                }
            } catch (Exception ignored) {
                future.succeeded(false);
                Brain.getLogger().info("Failed to activate door: " + info.name());
            }
        });

        return future;
    }

    public String toSave() {
        return String.format("%s::%s::%s::%s::%s", id, info.name(), info.description(), info.key(), Brain.getGson().toJson(position));
    }

    @Override
    public String toString() {
        return String.format("Door{id=%s, info=%s, open=%s, position=%s}", id, info, open, position);
    }

    public UUID id() {
        return id;
    }

    public DoorInfo info() {
        return info;
    }

    public DoorPosition position() {
        return position;
    }

    public record DoorInfo(String name, String description, UUID key) {

        @Override
        public String toString() {
            return String.format("DoorInfo{name=%s, description=%s, key=%s}", name, description, key);
        }
    }

    public record DoorPosition(String dimension, float x, float y, float z) {

        @Override
        public String toString() {
            return String.format("DoorPosition{dimension=%s, x=%s, y=%s, z=%s}", dimension, x, y, z);
        }
    }
}
