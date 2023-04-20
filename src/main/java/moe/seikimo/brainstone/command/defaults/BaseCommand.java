package moe.seikimo.brainstone.command.defaults;

import moe.seikimo.brainstone.Brain;
import moe.seikimo.brainstone.base.Base;
import moe.seikimo.brainstone.base.BaseManager;
import moe.seikimo.brainstone.command.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public final class BaseCommand extends Command {
    private static final String USAGE = "Usage: base <create | delete | edit | list | info>";

    public BaseCommand() {
        super("base");
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
                    var name = args[1];
                    var description = args[2];
                    var members = new ArrayList<>(Arrays.asList(args).subList(3, args.length));
                    if (members.isEmpty()) {
                        Brain.getLogger().info("You must specify at least one member.");
                        return;
                    }

                    var base = new Base(UUID.randomUUID(), new Base.BaseInfo(name, description, members));
                    BaseManager.registerBase(base);
                    Brain.getLogger().info("Created base: " + base);
                } catch (ArrayIndexOutOfBoundsException e) {
                    Brain.getLogger().info("Usage: base create <name> <description> <members>");
                } catch (Exception e) {
                    Brain.getLogger().info("There was an error creating the base. " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "delete" -> {
                try {
                    var id = UUID.fromString(args[1]);
                    if (!BaseManager.baseExists(id)) {
                        Brain.getLogger().info("There is no base with the id: " + id);
                    } else {
                        var base = BaseManager.getBase(id);
                        BaseManager.unregisterBase(base);
                        Brain.getLogger().info("Deleted base: " + base);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    Brain.getLogger().info("Usage: base delete <id>");
                } catch (Exception e) {
                    Brain.getLogger().info("There was an error deleting the base. " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "edit" -> Brain.getLogger().info("Editing base...");
            case "list" -> {
                var bases = BaseManager.getBases();
                if (bases.isEmpty()) {
                    Brain.getLogger().info("There are no bases to show.");
                    return;
                }
                Brain.getLogger().info("Showing " + bases.size() + " bases:");
                bases.forEach((uuid, base) -> Brain.getLogger().info("" + base));
            }
            case "info" -> {
                try {
                    var baseId = UUID.fromString(args[1]);
                    var base = BaseManager.getBase(baseId);
                    if (base == null) {
                        Brain.getLogger().info("There is no base with the id " + baseId);
                        return;
                    }

                    Brain.getLogger().info("Base info: " + base);
                } catch (ArrayIndexOutOfBoundsException e) {
                    Brain.getLogger().info("Usage: base info <baseId>");
                } catch (Exception e) {
                    Brain.getLogger().info("There was an error getting the base info. " + e.getMessage());
                    e.printStackTrace();
                }
            }
            default -> Brain.getLogger().info(USAGE);
        }
    }
}
