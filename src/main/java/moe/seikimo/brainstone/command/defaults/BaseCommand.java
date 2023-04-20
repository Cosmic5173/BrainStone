package moe.seikimo.brainstone.command.defaults;

import moe.seikimo.brainstone.Brain;
import moe.seikimo.brainstone.base.Base;
import moe.seikimo.brainstone.base.BaseManager;
import moe.seikimo.brainstone.base.Door;
import moe.seikimo.brainstone.command.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
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

                    var base = new Base(UUID.randomUUID(), new Base.BaseInfo(name, description, members), Map.of());
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
            case "edit" -> {
                try {
                    var baseId = UUID.fromString(args[1]);
                    if (!BaseManager.baseExists(baseId)) {
                        Brain.getLogger().info("There is no base with the id: " + baseId);
                    } else {
                        var base = BaseManager.getBase(baseId);
                        switch (args[2]) {
                            case "name" -> {
                                var newName = args[3];
                                base.setInfo(new Base.BaseInfo(newName, base.getInfo().description(), base.getInfo().members()));
                                Brain.getLogger().info("Edited base name: " + base);
                            }
                            case "description" -> {
                                var newDescription = args[3];
                                base.setInfo(new Base.BaseInfo(base.getInfo().name(), newDescription, base.getInfo().members()));
                                Brain.getLogger().info("Edited base description: " + base);
                            }
                            case "members" -> {
                                switch (args[3]) {
                                    case "add" -> {
                                        var members = base.getInfo().members();
                                        if (members.contains(args[4])) {
                                            Brain.getLogger().info("The member " + args[4] + " is already in the base.");
                                        } else {
                                            members.add(args[4]);
                                            base.setInfo(new Base.BaseInfo(base.getInfo().name(), base.getInfo().description(), members));
                                            Brain.getLogger().info("Added member to base: " + base);
                                        }
                                    }
                                    case "remove" -> {
                                        var members = base.getInfo().members();
                                        if (!members.contains(args[4])) {
                                            Brain.getLogger().info("The member " + args[4] + " is not in the base.");
                                        } else {
                                            members.remove(args[4]);
                                            base.setInfo(new Base.BaseInfo(base.getInfo().name(), base.getInfo().description(), members));
                                            Brain.getLogger().info("Removed member from base: " + base);
                                        }
                                    }
                                    default -> Brain.getLogger().info("Usage: base edit <id> members <add | remove> <member>");
                                }
                            }
                            case "doors" -> {
                                switch (args[3]) {
                                    case "add" -> {
                                        try {
                                            base.addDoor(new Door(
                                                    UUID.randomUUID(),
                                                    new Door.DoorInfo(args[4], args[5], UUID.fromString(args[6])),
                                                    Brain.getGson().fromJson(args[7], Door.DoorPosition.class)
                                            ));

                                            Brain.getLogger().info("Added door to base: " + base);
                                        } catch (ArrayIndexOutOfBoundsException e) {
                                            Brain.getLogger().info("Usage: base edit <id> doors add <name> <description> <key> <positionJSON>");
                                        }
                                    }
                                    case "remove" -> {
                                        var doorId = UUID.fromString(args[4]);
                                        if (!base.getDoors().containsKey(doorId)) {
                                            Brain.getLogger().info("There is no door with the id: " + doorId);
                                        } else {
                                            base.removeDoor(doorId);
                                            Brain.getLogger().info("Removed door from base: " + base);
                                        }
                                    }
                                    default -> Brain.getLogger().info("Usage: base edit <id> doors <add | remove> <door>");
                                }
                            }
                            default -> Brain.getLogger().info("Usage: base edit <id> <name | description | members | doors> <value>");
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    Brain.getLogger().info("Usage: base edit <id> <name | description | members | doors> <value>");
                } catch (Exception e) {
                    Brain.getLogger().info("There was an error editing the base. " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "list" -> {
                var bases = BaseManager.getBases();
                if (bases.isEmpty()) {
                    Brain.getLogger().info("There are no bases to show.");
                    return;
                }
                Brain.getLogger().info("Showing " + bases.size() + " bases:");
                bases.forEach((uuid, base) -> Brain.getLogger().info(String.valueOf(base)));
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
