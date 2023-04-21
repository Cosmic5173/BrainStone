package moe.seikimo.brainstone.api;

import moe.seikimo.brainstone.base.BaseManager;
import moe.seikimo.brainstone.user.StasisChamber;
import moe.seikimo.brainstone.user.UserManager;
import io.javalin.http.Context;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/** Configure handlers for endpoints here. */
public interface BrainRouting {

    /**
     * Attempts to get a new user.
     *
     * @route GET /user/{userId}/get
     * @param ctx The Javalin request/response context.
     */
    static void getUser(Context ctx) {
        var userId = UUID.fromString(ctx.pathParam("userId"));
        if (UserManager.userExists(userId)) {
            ctx.status(200).json(UserManager.getUser(userId));
        } else {
            ctx.status(400).result("User not found.");
        }
    }

    /**
     * Attempts to activate a stasis chamber.
     *
     * @route GET /user/{userId}/stasis/{stasisId}/activate
     * @param ctx The Javalin request/response context.
     */
    static void activateUserStasisChamber(Context ctx) {
        var userId = UUID.fromString(ctx.pathParam("userId"));
        var stasisChamberId = UUID.fromString(ctx.pathParam("stasisId"));
        if (!validateUserStasisChamber(userId, stasisChamberId, ctx)) return;

        var user = UserManager.getUser(userId);
        var stasisChamber = user.getStasisChamber(stasisChamberId);

        try {
            var response = stasisChamber.activate().get(3, TimeUnit.SECONDS);
            ctx.status(response ? 200 : 400).result(response ?
                    "Stasis chamber activated." :
                    "Stasis chamber activation failed.");
        } catch (Exception ignored) {
            ctx.status(500).result("Stasis chamber activation failed.");
        }
    }

    /**
     * Attempts to create a new stasis chamber.
     *
     * @route POST /user/{userId}/stasis/{stasisId}/create
     * @param ctx The Javalin request/response context.
     */
    static void createUserStasisChamber(Context ctx) {
        var userId = UUID.fromString(ctx.pathParam("userId"));
        var stasisKey = UUID.fromString(ctx.pathParam("key"));
        if (!UserManager.userExists(userId)) {
            ctx.status(404).result("User does not exist.");
            return;
        }

        var user = UserManager.getUser(userId);
        var stasisChamber = new StasisChamber(user, UUID.randomUUID(), stasisKey);
        user.addStasisChamber(stasisChamber);

        ctx.status(200).result(stasisKey.toString());
    }

    /**
     * Attempts to delete a stasis chamber.
     *
     * @route DELETE /user/{userId}/stasis/{stasisId}/delete
     * @param ctx The Javalin request/response context.
     */
    static void deleteUserStasisChamber(Context ctx) {
        var userId = UUID.fromString(ctx.pathParam("userId"));
        var stasisChamberId = UUID.fromString(ctx.pathParam("stasisId"));
        if (!validateUserStasisChamber(userId, stasisChamberId, ctx)) return;

        var user = UserManager.getUser(userId);
        user.removeStasisChamber(stasisChamberId);

        ctx.status(200).result("Stasis chamber deleted.");
    }

    /**
     * Attempts to get a new base.
     *
     * @route GET /base/{baseId}/get
     * @param ctx The Javalin request/response context.
     */
    static void getBase(Context ctx) {
        var baseId = UUID.fromString(ctx.pathParam("baseId"));
        if (BaseManager.baseExists(baseId)) {
            ctx.status(200).json(BaseManager.getBase(baseId));
        } else {
            ctx.status(400).result("Base not found.");
        }
    }

    /**
     * Attempts to create a new base.
     *
     * @route GET /base/{baseId}/doors/{doorId}/get
     * @param ctx The Javalin request/response context.
     */
    static void getBaseDoor(Context ctx) {
        var baseId = UUID.fromString(ctx.pathParam("baseId"));
        var doorId = UUID.fromString(ctx.pathParam("doorId"));
        if (!BaseManager.baseExists(baseId)) {
            ctx.status(400).result("Base does not exist.");
            return;
        }

        var base = BaseManager.getBase(baseId);
        if (!base.hasDoor(doorId)) {
            ctx.status(400).result("Door does not exist.");
            return;
        }

        ctx.status(200).json(base.getDoor(doorId));
    }

    /**
     * Attempts to open a base door.
     *
     * @route GET /base/{baseId}/doors/{doorId}/open
     * @param ctx The Javalin request/response context.
     */
    static void openBaseDoor(Context ctx) {
        var baseId = UUID.fromString(ctx.pathParam("baseId"));
        var doorId = UUID.fromString(ctx.pathParam("doorId"));
        if (!BaseManager.baseExists(baseId)) {
            ctx.status(400).result("Base does not exist.");
            return;
        }

        var base = BaseManager.getBase(baseId);
        if (!base.hasDoor(doorId)) {
            ctx.status(400).result("Door does not exist.");
            return;
        }

        var door = base.getDoor(doorId);
        try {
            if (door.isOpen()) {
                ctx.status(400).result("Door is already open.");
                return;
            }

            var response = door.open().get();
            ctx.status(response ? 200 : 400).result(response ?
                    "Door activated. (200)" :
                    "Door activation failed. (400)");
        } catch (Exception e) {
            ctx.status(500).result("Door activation failed (500).");
            e.printStackTrace();
        }
    }

    /**
     * Attempts to retrieve all base doors.
     *
     * @route GET /base/{baseId}/doors/all
     * @param ctx The Javalin request/response context.
     */
    static void getAllBaseDoors(Context ctx) {
        var baseId = UUID.fromString(ctx.pathParam("baseId"));
        if (!BaseManager.baseExists(baseId)) {
            ctx.status(400).result("Base does not exist.");
            return;
        }

        var base = BaseManager.getBase(baseId);
        ctx.status(200).json(base.getDoors().values());
    }

    /**
     * Attempts to close a base door.
     *
     * @route GET /base/{baseId}/doors/{doorId}/close
     * @param ctx The Javalin request/response context.
     */
    static void closeBaseDoor(Context ctx) {
        var baseId = UUID.fromString(ctx.pathParam("baseId"));
        var doorId = UUID.fromString(ctx.pathParam("doorId"));
        if (!BaseManager.baseExists(baseId)) {
            ctx.status(400).result("Base does not exist.");
            return;
        }

        var base = BaseManager.getBase(baseId);
        if (!base.hasDoor(doorId)) {
            ctx.status(400).result("Door does not exist.");
            return;
        }

        var door = base.getDoor(doorId);
        try {
            if (!door.isOpen()) {
                ctx.status(400).result("Door is already closed.");
                return;
            }

            var response = door.close().get();
            ctx.status(response ? 200 : 400).result(response ?
                    "Door activated (200)." :
                    "Door activation failed (400).");
        } catch (Exception e) {
            ctx.status(500).result("Door activation failed. (500)");
            e.printStackTrace();
        }
    }

    /**
     * Checks if a stasis chamber is valid.
     *
     * @param userId The user's ID.
     * @param stasisChamberId The stasis chamber's ID.
     * @param ctx The Javalin request/response context.
     * @return True if the stasis chamber is valid, false otherwise.
     */
    private static boolean validateUserStasisChamber(UUID userId, UUID stasisChamberId, Context ctx) {
        if (!UserManager.userExists(userId)) {
            ctx.status(404).result("User does not exist.");
            return false;
        }

        var user = UserManager.getUser(userId);
        if (!user.hasStasisChamber(stasisChamberId)) {
            ctx.status(404).result("Stasis chamber does not exist.");
            return false;
        }

        return true;
    }
}
