package moe.seikimo.brainstone.command.defaults;

import moe.seikimo.brainstone.Brain;
import moe.seikimo.brainstone.command.Command;
import moe.seikimo.brainstone.user.StasisChamber;
import moe.seikimo.brainstone.user.UserManager;

import java.util.UUID;

public final class StasisCommand extends Command {
    private static final String USAGE = "Usage: /stasis <user> <create|delete|list>";

    public StasisCommand() {
        super("stasis");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            Brain.getLogger().info(USAGE);
            return;
        }

        var userId = UUID.fromString(args[0]);
        if (!UserManager.userExists(userId)) {
            Brain.getLogger().info("User does not exist.");
            return;
        }

        var user = UserManager.getUser(userId);
        switch (args[1]) {
            case "create" -> {
                try {
                    var key = UUID.fromString(args[2]);
                    var stasis = new StasisChamber(user, UUID.randomUUID(), key);
                    user.addStasisChamber(stasis);

                    Brain.getLogger().info("Created stasis: " + stasis + " for user " + user.getName());
                } catch (ArrayIndexOutOfBoundsException e) {
                    Brain.getLogger().info("Usage: /stasis create <key>");
                } catch (Exception e) {
                    Brain.getLogger().info("There was an error creating the stasis. " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "delete" -> {
                try {
                    var chamberId = UUID.fromString(args[2]);
                    if (!user.hasStasisChamber(chamberId)) {
                        Brain.getLogger().info("User does not have a stasis chamber with id: " + chamberId);
                    } else {
                        var chamber = user.getStasisChamber(chamberId);
                        user.removeStasisChamber(chamber);
                        Brain.getLogger().info("Deleted stasis chamber: " + chamber);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    Brain.getLogger().info("Usage: /stasis <user> delete <id>");
                } catch (Exception e) {
                    Brain.getLogger().info("There was an error deleting that stasis chamber: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "list" -> {
                var chambers = user.getStasisChambers();
                if (chambers.isEmpty()) {
                    Brain.getLogger().info("User does not have any stasis chambers.");
                } else {
                    Brain.getLogger().info("Showing " + chambers.size() + " stasis chambers:");
                    chambers.forEach((id, chamber) -> Brain.getLogger().info("" + chamber));
                }
            }
            case "info" -> Brain.getLogger().info("Showing stasis info for user " + user.getName() + "...");
            default -> Brain.getLogger().info(USAGE);
        }
    }
}
