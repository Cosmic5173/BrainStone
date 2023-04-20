package moe.seikimo.console;

import moe.seikimo.Brain;
import net.minecrell.terminalconsole.SimpleTerminalConsole;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

public class TerminalConsole extends SimpleTerminalConsole {

    private final Brain brain;
    private final ConsoleThread thread;

    public TerminalConsole(Brain brain) {
        this.brain = brain;
        this.thread = new ConsoleThread(this);
    }

    @Override
    protected boolean isRunning() {
        return brain.isRunning();
    }

    @Override
    protected void runCommand(String s) {
        brain.getCommandMap().executeRawCommand(s);
    }

    @Override
    protected LineReader buildReader(LineReaderBuilder builder) {
        builder.appName("SmpBrain");
        builder.option(LineReader.Option.HISTORY_BEEP, false);
        builder.option(LineReader.Option.HISTORY_IGNORE_DUPS, true);
        builder.option(LineReader.Option.HISTORY_IGNORE_SPACE, true);
        return super.buildReader(builder);
    }

    @Override
    protected void shutdown() {
        brain.stop();
    }

    public ConsoleThread getThread() {
        return thread;
    }
}
