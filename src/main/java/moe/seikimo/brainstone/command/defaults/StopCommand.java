package moe.seikimo.brainstone.command.defaults;

import moe.seikimo.brainstone.Brain;
import moe.seikimo.brainstone.command.Command;

public final class StopCommand extends Command {
    public StopCommand() {
        super("stop");
    }

    @Override
    public void execute(String[] args) {
        Brain.getInstance().stop();
    }
}
