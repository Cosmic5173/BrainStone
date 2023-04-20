package moe.seikimo.command.defaults;

import moe.seikimo.command.Command;
import moe.seikimo.user.StasisChamber;
import moe.seikimo.user.UserManager;

import java.util.UUID;

public class StasisCommand extends Command {

    String USAGE = "Usage: /stasis <user> <create|delete|list>";
    public StasisCommand() {
        super("stasis");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            System.out.println(USAGE);
            return;
        }

        var userId = UUID.fromString(args[0]);
        if (!UserManager.userExists(userId)) {
            System.out.println("User does not exist.");
            return;
        }

        var user = UserManager.getUser(userId);
        switch (args[1]) {
            case "create" -> {
                try {
                    var key = UUID.fromString(args[2]);
                    var stasis = new StasisChamber(user, UUID.randomUUID(), key);
                    user.addStasisChamber(stasis);

                    System.out.println("Created stasis: " + stasis + " for user " + user.getName());
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Usage: /stasis create <key>");
                } catch (Exception e) {
                    System.out.println("There was an error creating the stasis. " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "delete" -> {
                try {
                    var chamberId = UUID.fromString(args[2]);
                    if (!user.hasStasisChamber(chamberId)) {
                        System.out.println("User does not have a stasis chamber with id: " + chamberId);
                    } else {
                        var chamber = user.getStasisChamber(chamberId);
                        user.removeStasisChamber(chamber);
                        System.out.println("Deleted stasis chamber: " + chamber);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Usage: /stasis <user> delete <id>");
                } catch (Exception e) {
                    System.out.println("There was an error deleting that stasis chamber: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "list" -> {
                var chambers = user.getStasisChambers();
                if (chambers.isEmpty()) {
                    System.out.println("User does not have any stasis chambers.");
                } else {
                    System.out.println("Showing " + chambers.size() + " stasis chambers:");
                    chambers.forEach((id, chamber) -> System.out.println(chamber));
                }
            }
            case "info" -> System.out.println("Showing stasis info for user " + user.getName() + "...");
            default -> System.out.println(USAGE);
        }
    }
}
