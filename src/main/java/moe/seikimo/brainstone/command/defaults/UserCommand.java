package moe.seikimo.brainstone.command.defaults;

import moe.seikimo.brainstone.Brain;
import moe.seikimo.brainstone.base.BaseManager;
import moe.seikimo.brainstone.command.Command;
import moe.seikimo.brainstone.user.User;
import moe.seikimo.brainstone.user.UserManager;

import java.util.UUID;

public final class UserCommand extends Command {
    private static final String USAGE = "Usage: /user <create|delete|list|edit>";

    public UserCommand() {
        super("user");
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            Brain.getLogger().info(USAGE);
            return;
        }

        switch (args[0]) {
            case "create" -> {
                try {
                    var user = new User(UUID.randomUUID(), args[1], args[2]);
                    UserManager.registerUser(user);

                    Brain.getLogger().info("Created user: " + user);
                } catch (ArrayIndexOutOfBoundsException e) {
                    Brain.getLogger().info("Usage: /user create <name> <base>");
                } catch (Exception e) {
                    Brain.getLogger().info("There was an error creating the user. " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "delete" -> {
                try {
                    var userId = UUID.fromString(args[1]);
                    if (!UserManager.userExists(userId)) {
                        Brain.getLogger().info("There is no user found with the id: " + userId);
                    } else {
                        var user = UserManager.getUser(userId);
                        UserManager.unregisterUser(user);
                        Brain.getLogger().info("Deleted user: " + user);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    Brain.getLogger().info("Usage /user delete <id>");
                } catch (Exception e) {
                    Brain.getLogger().info("There was an error deleting the user. " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "list" -> {
                var users = UserManager.getUsers();
                if (users.isEmpty()) {
                    Brain.getLogger().info("There are no users to show.");
                    return;
                }
                Brain.getLogger().info("Showing " + users.size() + " users:");
                users.forEach((uuid, user) -> Brain.getLogger().info(String.valueOf(user)));
            }
            case "edit" -> {
                try {
                    var userId = UUID.fromString(args[1]);
                    if (!UserManager.userExists(userId)) {
                        Brain.getLogger().info("There is no user found with the id: " + userId);
                    } else {
                        var user = UserManager.getUser(userId);
                        var baseId = UUID.fromString(args[2]);
                        if (!BaseManager.baseExists(baseId)) {
                            Brain.getLogger().info("There is no base found with the id: " + baseId);
                        } else {
                            var base = UserManager.getUser(baseId);
                            user.setBaseId(args[2]);
                            Brain.getLogger().info("Updated user: " + user);
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    Brain.getLogger().info("Usage /user edit <id> <base>");
                } catch (Exception e) {
                    Brain.getLogger().info("There was an error editing the user. " + e.getMessage());
                    e.printStackTrace();
                }
            }
            default -> Brain.getLogger().info(USAGE);
        }
    }
}
