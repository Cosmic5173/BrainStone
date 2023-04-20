package moe.seikimo.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandMap {

    private final Map<String, Command> commands = new HashMap<>();

    public void registerCommand(Command command) {
        commands.put(command.getName(), command);
        for (String alias : command.getAliases()) {
            commands.put(alias, command);
        }
    }

    public Command getCommand(String name) {
        return commands.get(name);
    }

    public Map<String, Command> getCommands() {
        return commands;
    }

    public void executeRawCommand(String command) {
        List<String> args = parseArguments(command);
        Command cmd = getCommand(args.get(0));
        if (cmd != null) {
            cmd.execute(args.subList(1, args.size()).toArray(new String[0]));
        }
    }

    public static List<String> parseArguments(String cmdLine) {
        StringBuilder sb = new StringBuilder(cmdLine);
        List<String> args = new ArrayList<>();
        boolean notQuoted = true;
        int start = 0;

        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) == '\\') {
                sb.deleteCharAt(i);
                continue;
            }

            if (sb.charAt(i) == ' ' && notQuoted) {
                String arg = sb.substring(start, i);
                if (!arg.isEmpty()) {
                    args.add(arg);
                }
                start = i + 1;
            } else if (sb.charAt(i) == '"') {
                sb.deleteCharAt(i);
                --i;
                notQuoted = !notQuoted;
            }
        }

        String arg = sb.substring(start);
        if (!arg.isEmpty()) {
            args.add(arg);
        }
        return args;
    }
}
