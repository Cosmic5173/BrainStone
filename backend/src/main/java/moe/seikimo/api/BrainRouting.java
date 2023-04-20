package moe.seikimo.api;

import moe.seikimo.user.StasisChamber;
import moe.seikimo.user.UserManager;
import io.javalin.http.Context;

import java.util.UUID;

public class BrainRouting {

    // Configure handlers for endpoints here
    public static void activateUserStasisChamber(Context ctx) {
        System.out.println("Activating stasis chamber...");
        var userId = UUID.fromString(ctx.pathParam("userId"));
        var stasisChamberId = UUID.fromString(ctx.pathParam("stasisId"));
        if (!validateUserStasisChamber(userId, stasisChamberId, ctx)) return;

        var user = UserManager.getUser(userId);
        var stasisChamber = user.getStasisChamber(stasisChamberId);
        stasisChamber.activate();
        ctx.status(200);
        ctx.result("Stasis chamber activated.");
    }

    public static void createUserStasisChamber(Context ctx) {
        var userId = UUID.fromString(ctx.pathParam("userId"));
        var stasisKey = UUID.fromString(ctx.pathParam("key"));
        if (!UserManager.userExists(userId)) {
            ctx.status(404);
            ctx.result("User does not exist.");
            return;
        }

        var user = UserManager.getUser(userId);
        var stasisChamber = new StasisChamber(user, UUID.randomUUID(), stasisKey);
        user.addStasisChamber(stasisChamber);
        ctx.status(200);
        ctx.result(stasisKey.toString());
    }

    public static void deleteUserStasisChamber(Context ctx) {
        var userId = UUID.fromString(ctx.pathParam("userId"));
        var stasisChamberId = UUID.fromString(ctx.pathParam("stasisId"));
        if (!validateUserStasisChamber(userId, stasisChamberId, ctx)) return;

        var user = UserManager.getUser(userId);
        user.removeStasisChamber(stasisChamberId);
        ctx.status(200);
        ctx.result("Stasis chamber deleted.");
    }

    private static boolean validateUserStasisChamber(UUID userId, UUID stasisChamberId, Context ctx) {
        if (!UserManager.userExists(userId)) {
            ctx.status(404);
            ctx.result("User does not exist.");
            return false;
        }

        var user = UserManager.getUser(userId);
        if (!user.hasStasisChamber(stasisChamberId)) {
            ctx.status(404);
            ctx.result("Stasis chamber does not exist.");
            return false;
        }

        return true;
    }
}
