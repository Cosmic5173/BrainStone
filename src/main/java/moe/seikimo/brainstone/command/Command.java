package moe.seikimo.brainstone.command;

import java.util.ArrayList;
import java.util.List;

public abstract class Command {

    private final String name;
    private final List<String> aliases;

    public Command(String name) {
        this(name, new ArrayList<>());
    }

    public Command(String name, List<String> aliases) {
        this.name = name;
        this.aliases = aliases;
    }

    public String getName() {
        return name;
    }

    public List<String> getAliases() {
        return aliases;
    }

    abstract public void execute(String[] args);
}
