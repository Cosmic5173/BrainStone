package moe.seikimo;

import moe.seikimo.api.BrainRouting;
import moe.seikimo.base.BaseManager;
import moe.seikimo.command.CommandMap;
import moe.seikimo.command.defaults.*;
import moe.seikimo.console.TerminalConsole;
import moe.seikimo.user.UserManager;
import moe.seikimo.util.Configuration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;
import lombok.Getter;
import okhttp3.OkHttpClient;

import java.io.*;

public class Brain {

    // Load the configuration on startup
    // Create default if not found
    static {
        var configFile = new File("config.json");
        if (!configFile.exists()) {
            try {
                if (!configFile.createNewFile()) {
                    System.out.println("Unable to create config file. Check permissions.");
                    System.exit(1);
                }

                var classLoader = Thread.currentThread().getContextClassLoader();
                var stream = classLoader.getResourceAsStream("config.json");
                if (stream == null) {
                    System.out.println("Unable to read template config file.");
                    System.exit(1);
                }

                var reader = new BufferedReader(new InputStreamReader(stream));
                var out = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    out.append(line);
                }
                reader.close();

                var gson = new GsonBuilder().setPrettyPrinting().create();
                var writer = new FileWriter(configFile);
                gson.toJson(gson.fromJson(out.toString(), Configuration.class), writer);
                writer.close();
            } catch (IOException ignored) {
                System.out.println("Unable to create config file. Check permissions.");
                System.exit(1);
            }
        }
    }

    @Getter
    private static Brain instance;

    public static void main(String[] args) {
        instance = new Brain();
    }

    @Getter
    private Configuration configuration;
    @Getter
    private boolean running = true;
    @Getter
    private TerminalConsole console;
    @Getter
    private final CommandMap commandMap = new CommandMap();
    @Getter
    private final Javalin webApp = Javalin.create();
    @Getter
    private final OkHttpClient httpClient = new OkHttpClient();
    @Getter
    private final Gson gsonInstance = new Gson();


    public Brain() {
        this.loadConfiguration();
        this.start();
    }

    public void start() {
        this.console = new TerminalConsole(this);
        this.console.getThread().start();

        this.configureCommands();

        this.configureApp();
        this.webApp.start(configuration.getPort());

        this.loadData();

        System.out.println("Done!");
    }

    public void stop() {
        this.running = false;
        this.webApp.stop();

        BaseManager.fini();
        UserManager.fini();
    }

    private void loadConfiguration() {
        var file = new File("config.json");
        if (!file.exists()) {
            System.out.println("Config file not found. Exiting.");
            System.exit(1);
        }

        try {
            FileReader reader = new FileReader(file);
            configuration = new Gson().fromJson(reader, Configuration.class);
        } catch (IOException ignored) {
            System.out.println("Unable to read config file. Exiting.");
            System.exit(1);
        }
    }

    private void configureCommands() {
        this.commandMap.registerCommand(new StopCommand());
        this.commandMap.registerCommand(new BaseCommand());
        this.commandMap.registerCommand(new UserCommand());
        this.commandMap.registerCommand(new StasisCommand());

    }

    private void configureApp() {
        webApp.get("/user/{userId}/stasis/{stasisId}/activate", BrainRouting::activateUserStasisChamber);
        webApp.get("/user/{userId}/stasis/{key}/create", BrainRouting::createUserStasisChamber);
        webApp.get("/user/{userId}/stasis/{stasisId}/delete", BrainRouting::deleteUserStasisChamber);
    }

    private void loadData() {
        BaseManager.init();
        UserManager.init();
    }
}