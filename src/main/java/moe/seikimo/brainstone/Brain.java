package moe.seikimo.brainstone;

import io.javalin.json.JavalinGson;
import lombok.SneakyThrows;
import moe.seikimo.brainstone.api.BrainRouting;
import moe.seikimo.brainstone.api.CreateRouting;
import moe.seikimo.brainstone.base.BaseManager;
import moe.seikimo.brainstone.command.CommandMap;
import moe.seikimo.brainstone.command.defaults.*;
import moe.seikimo.brainstone.user.UserManager;
import moe.seikimo.brainstone.util.Configuration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;
import lombok.Getter;
import okhttp3.OkHttpClient;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.UUID;

public final class Brain {
    @Getter private static final Logger logger
            = LoggerFactory.getLogger("Brain");
    @Getter private static final LineReader console
            = Brain.createConsole();
    @Getter private static final Gson gson
            = new Gson();
    @Getter private static Brain instance;

    // Tasks to do immediately on startup.
    static {
        // Set logback configuration file.
        System.setProperty("logback.configurationFile", "logback.xml");

        // Load the configuration on startup.
        // Create default if not found.
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
                String line; while ((line = reader.readLine()) != null) {
                    out.append(line);
                } reader.close();

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

    public static void main(String[] args) {
        Brain.instance = new Brain();
    }

    /**
     * Returns the URL for toggling the endpoint.
     *
     * @param uuid The UUID of the endpoint.
     * @return The URL.
     */
    public static String endpoint(UUID uuid) {
        return Brain.getInstance().getConfiguration()
                .getWirelessRedstoneEndpoint() + uuid.toString();
    }

    /**
     * Creates a {@link LineReader}, or "console" for the application.
     *
     * @return A {@link LineReader} instance.
     * @throws RuntimeException if something impossible happened. (no dumb terminal created)
     */
    @SneakyThrows(IOException.class)
    private static LineReader createConsole() {
        Terminal terminal;
        try {
            terminal = TerminalBuilder.builder().jna(true).build();
        } catch (IOException ignored) {
            // Try to get a dumb terminal.
            terminal = TerminalBuilder.builder().dumb(true).build();
        }

        return LineReaderBuilder.builder().terminal(terminal).build();
    }

    /** Sets up the console for input. */
    private static void setupConsole() {
        while (true) {
            try {
                var line = Brain.console.readLine("> ");
                if (line == null) continue;

                // Check if the line is empty.
                if (line.isEmpty()) continue;
                var content = line.trim(); /* .split(" "); */

                // Handle the line as a command.
                /* var label = content[0]; */
                /* var args = new ArrayList<>(Arrays.asList(content).subList(1, content.length)) */
                Brain.getInstance().getCommandMap().executeRawCommand(content);
            } catch (UserInterruptException | EndOfFileException ignored) {
                // Ignore this exception.
            } catch (IOError | Exception exception) {
                Brain.logger.error("Unable to process command.", exception);
            }
        }
    }

    @Getter private Configuration configuration;

    @Getter private final Javalin webApp = Javalin.create(config -> config.jsonMapper(new JavalinGson(new Gson())));
    @Getter private final OkHttpClient httpClient = new OkHttpClient();

    @Getter private boolean running = true;
    @Getter private final CommandMap commandMap = new CommandMap();

    public Brain() {
        this.loadConfiguration();
        this.start();
    }

    public void start() {
        this.configureCommands();

        this.configureApp();
        this.webApp.start(this.configuration.getPort());

        this.loadData();

        Brain.logger.info("Done!");
        // Configure the console reader.
        new Thread(Brain::setupConsole).start();
    }

    public void stop() {
        this.running = false;
        this.webApp.stop();

        BaseManager.fini();
        UserManager.fini();

        System.exit(0);
    }

    private void loadConfiguration() {
        var file = new File("config.json");
        if (!file.exists()) {
            Brain.logger.error("Config file not found. Exiting.");
            System.exit(1);
        }

        try {
            var reader = new FileReader(file);
            this.configuration = new Gson().fromJson(reader, Configuration.class);

            if (this.configuration == null) {
                Brain.logger.error("Create a valid configuration file.");
                System.exit(1);
            }
        } catch (IOException ignored) {
            Brain.logger.error("Unable to read config file. Exiting.");
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
        webApp.get("/user/{userId}/get", BrainRouting::getUser);
        webApp.get("/user/{userId}/stasis/{stasisId}/activate", BrainRouting::activateUserStasisChamber);
        webApp.post("/user/{userId}/stasis/{stasisId}/create", CreateRouting::createUserStasisChamber);
        webApp.delete("/user/{userId}/stasis/{stasisId}/delete", BrainRouting::deleteUserStasisChamber);

        webApp.get("/base/{baseId}/get", BrainRouting::getBase);
        webApp.get("/base/{baseId}/doors/all", BrainRouting::getAllBaseDoors);
        webApp.get("/base/{baseId}/doors/{doorId}/open", BrainRouting::openBaseDoor);
        webApp.get("/base/{baseId}/doors/{doorId}/close", BrainRouting::closeBaseDoor);
        webApp.post("/base/{baseId}/doors/create", CreateRouting::createBaseDoor);
    }

    private void loadData() {
        BaseManager.init();
        UserManager.init();
    }
}
