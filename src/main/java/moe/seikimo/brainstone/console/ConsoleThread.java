package moe.seikimo.brainstone.console;

import net.minecrell.terminalconsole.SimpleTerminalConsole;

public final class ConsoleThread extends Thread {
    private final SimpleTerminalConsole console;

    public ConsoleThread(SimpleTerminalConsole console) {
        this.console = console;
    }

    @Override
    public void run() {
        this.console.start();
    }
}
