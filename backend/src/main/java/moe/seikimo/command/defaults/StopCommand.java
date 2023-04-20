package moe.seikimo.command.defaults;

import moe.seikimo.Brain;
import moe.seikimo.command.Command;

public class StopCommand extends Command {

    public StopCommand() {
        super("stop");
    }

    @Override
    public void execute(String[] args) {
        Brain.getInstance().stop();
    }
}
