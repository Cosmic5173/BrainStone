package moe.seikimo.brainstone.api;

import moe.seikimo.brainstone.user.StasisChamber;
import moe.seikimo.brainstone.user.UserManager;
import io.javalin.http.Context;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/** Configure handlers for endpoints here. */
public interface BrainRouting {
    /**
     * Attempts to activate a stasis chamber.
     *
     * @route GET /user/{userId}/stasis/{stasisId}/activate
     * @param ctx The Javalin request/response context.
     */
    static void activateUserStasisChamber(Context ctx) {
        System.out.println("Activating stasis chamber...");

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
