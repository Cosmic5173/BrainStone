package moe.seikimo.console;

import net.minecrell.terminalconsole.SimpleTerminalConsole;

public class ConsoleThread extends Thread {

    private final SimpleTerminalConsole console;

    public ConsoleThread(SimpleTerminalConsole console) {
        this.console = console;
    }

    @Override
    public void run() {
        console.start();
    }
}
