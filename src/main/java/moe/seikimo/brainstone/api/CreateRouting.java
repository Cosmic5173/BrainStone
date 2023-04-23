package moe.seikimo.brainstone.api;

import io.javalin.http.Context;
import moe.seikimo.brainstone.api.body.CreateDoorRequest;
import moe.seikimo.brainstone.base.BaseManager;
import moe.seikimo.brainstone.base.Door;
import moe.seikimo.brainstone.user.StasisChamber;
import moe.seikimo.brainstone.user.UserManager;

import java.util.UUID;

/** Routing for creation requests. */
public interface CreateRouting {
    /**
     * Attempts to create a new stasis chamber.
     *
     * @route POST /user/{userId}/stasis/{stasisId}/create
     * @param ctx The Javalin request/response context.
     */
    static void createUserStasisChamber(Context ctx) {
        var userId = UUID.fromString(ctx.pathParam("userId"));
        var stasisId = UUID.fromString(ctx.pathParam("stasisId"));
        if (!UserManager.userExists(userId)) {
            ctx.status(404).result("User does not exist.");
            return;
        }

        var user = UserManager.getUser(userId);
        var stasisChamber = new StasisChamber(user, UUID.randomUUID(), stasisId);
        user.addStasisChamber(stasisChamber);

        ctx.status(200).result(stasisId.toString());
    }

    /**
     * Attempts to create a new door.
     *
     * @route POST /base/{baseId}/doors/create
     * @param ctx The Javalin request/response context.
     */
    static void createBaseDoor(Context ctx) {
        var baseId = UUID.fromString(ctx.pathParam("baseId"));
        if (!BaseManager.baseExists(baseId)) {
            ctx.status(404).result("Base does not exist.");
            return;
        }

        try {
            var base = BaseManager.getBase(baseId);
            var door = Door.of(ctx.bodyAsClass(CreateDoorRequest.class));
            base.addDoor(door);

            ctx.status(200).result(door.id().toString());
        } catch (Exception ignored) {
            ctx.status(400).result("Invalid door request.");
        }
    }
}
