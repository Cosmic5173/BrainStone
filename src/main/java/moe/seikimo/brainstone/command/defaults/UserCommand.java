package moe.seikimo.brainstone.command.defaults;

import moe.seikimo.brainstone.command.Command;
import moe.seikimo.brainstone.user.User;
import moe.seikimo.brainstone.user.UserManager;

import java.util.UUID;

public final class UserCommand extends Command {
    private static final String USAGE = "Usage: /user <create|delete|list|info>";

    public UserCommand() {
        super("user");
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            System.out.println(USAGE);
            return;
        }

        switch (args[0]) {
            case "create" -> {
                try {
                    var name = args[1];
                    var user = new User(UUID.randomUUID(), name);
                    UserManager.registerUser(user);

                    System.out.println("Created user: " + user);
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Usage: /user create <name>");
                } catch (Exception e) {
                    System.out.println("There was an error creating the user. " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "delete" -> {
                try {
                    var userId = UUID.fromString(args[1]);
                    if (!UserManager.userExists(userId)) {
                        System.out.println("There is no user found with the id: " + userId);
                    } else {
                        var user = UserManager.getUser(userId);
                        UserManager.unregisterUser(user);
                        System.out.println("Deleted user: " + user);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Usage /user delete <id>");
                } catch (Exception e) {
                    System.out.println("There was an error deleting the user. " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "list" -> {
                var users = UserManager.getUsers();
                if (users.isEmpty()) {
                    System.out.println("There are no users to show.");
                    return;
                }
                System.out.println("Showing " + users.size() + " users:");
                users.forEach((uuid, user) -> System.out.println(user));
            }
            case "info" -> System.out.println("Showing user info...");
            default -> System.out.println(USAGE);
        }
    }
}
