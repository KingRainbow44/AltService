package moe.seikimo.altservice;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.Getter;
import moe.seikimo.altservice.backend.PacketHandlers;
import moe.seikimo.altservice.command.SimpleCommandMap;
import moe.seikimo.altservice.command.player.DisconnectCommand;
import moe.seikimo.altservice.command.player.RequestCommand;
import moe.seikimo.altservice.command.util.PaletteCommand;
import moe.seikimo.altservice.command.util.ReloadCommand;
import moe.seikimo.altservice.command.util.RunScriptCommand;
import moe.seikimo.altservice.command.util.StopCommand;
import moe.seikimo.altservice.handlers.PacketHandler;
import moe.seikimo.altservice.player.Player;
import moe.seikimo.altservice.player.PlayerManager;
import moe.seikimo.altservice.player.PlayerTickThread;
import moe.seikimo.altservice.player.command.PlayerCommandMap;
import moe.seikimo.altservice.player.command.action.*;
import moe.seikimo.altservice.player.command.util.*;
import moe.seikimo.altservice.proto.Frontend.FrontendIds;
import moe.seikimo.altservice.proto.Service.ServiceIds;
import moe.seikimo.altservice.proto.Service.ServiceJoinCsReq;
import moe.seikimo.altservice.proto.Service.UpdateSessionsCsNotify;
import moe.seikimo.altservice.proto.Structures.Packet;
import moe.seikimo.altservice.proto.Structures.UnionCmdNotify;
import moe.seikimo.altservice.script.ScriptLoader;
import moe.seikimo.altservice.utils.BinaryUtils;
import moe.seikimo.altservice.utils.LoggerUtils;
import moe.seikimo.altservice.utils.objects.ThreadFactoryBuilder;
import moe.seikimo.altservice.utils.objects.absolute.GameConstants;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOError;
import java.io.InputStream;
import java.net.URI;

public final class AltBackend extends WebSocketClient {
    @Getter private static final Logger logger
            = LoggerFactory.getLogger("Alt Backend");
    @Getter private static final EventLoopGroup eventGroup
            = new NioEventLoopGroup(0, ThreadFactoryBuilder.base());

    @Getter private static String configFile = "config.json";
    @Getter private static AltBackend instance;
    private static LineReader lineReader = null;

    static {
        // Set logback configuration file.
        System.setProperty("logback.configurationFile", "logback.xml");
        // Disable other loggers.
        LoggerUtils.disableLoggers();
    }

    @Getter private static final PlayerTickThread playerTickThread
            = new PlayerTickThread();

    /*
     * Command maps.
     */
    @Getter private static final SimpleCommandMap consoleCommands
            = new SimpleCommandMap();
    @Getter private static final PlayerCommandMap playerCommands
            = new PlayerCommandMap();

    /**
     * Fetches a resource.
     *
     * @param path The path to the resource.
     *             Prefixed '/' is unneeded.
     * @return The resource as an input stream.
     */
    public static InputStream getResource(String path) {
        try {
            return AltBackend.class.getResourceAsStream("/" + path);
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * Application entrypoint.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            AltBackend.configFile = args[0];
        }

        // Load the configuration.
        Configuration.load();

        // Load the block palette.
        GameConstants.initializeBlocks();

        // Set the logger in debug mode.
        LoggerUtils.setDebug(AltBackend.getLogger());

        // Start the console.
        AltBackend.getConsole();
        new Thread(AltBackend::startConsole).start();

        // Listen for a shutdown.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            AltBackend.getPlayerTickThread().shutdown();
            PlayerManager.destroyAll();
            AltBackend.getEventGroup().shutdownGracefully();
            AltBackend.getLogger().info("Stopping backend...");
        }));

        // Start the player tick thread.
        AltBackend.getPlayerTickThread().start();
        // Register commands.
        AltBackend.registerCommands();

        // Initialize the script loader.
        ScriptLoader.initialize();

        // Create & start the backend.
        AltBackend.instance = new AltBackend();
        AltBackend.instance.connect();

        AltBackend.getLogger().info("Done! Alt Backend started.");
    }

    /**
     * @return The terminal line reader.
     *         Creates a new line reader if not already created.
     */
    public static LineReader getConsole() {
        // Check if the line reader exists.
        if (AltBackend.lineReader == null) {
            Terminal terminal = null; try {
                // Create a native terminal.
                terminal = TerminalBuilder.builder()
                        .jna(true).build();
            } catch (Exception ignored) {
                try {
                    // Fallback to a dumb JLine terminal.
                    terminal = TerminalBuilder.builder()
                            .dumb(true).build();
                } catch (Exception ignored1) { }
            }

            // Set the line reader.
            AltBackend.lineReader = LineReaderBuilder.builder()
                    .terminal(terminal).build();
        }

        return AltBackend.lineReader;
    }

    /**
     * Starts the line reader.
     */
    public static void startConsole() {
        String input = null;
        var isLastInterrupted = false;
        var logger = AltBackend.getLogger();

        while (true) {
            try {
                input = AltBackend.lineReader.readLine("> ");
            } catch (UserInterruptException ignored) {
                if (!isLastInterrupted) {
                    isLastInterrupted = true;
                    logger.info("Press Ctrl-C again to shutdown.");
                    continue;
                } else {
                    Runtime.getRuntime().exit(0);
                }
            } catch (EndOfFileException ignored) {
                continue;
            } catch (IOError e) {
                logger.error("An IO error occurred while trying to read from console.", e);
                return;
            }

            isLastInterrupted = false;

            try {
                // Invoke the command.
                consoleCommands.invoke(input);
            } catch (Exception e) {
                logger.warn("An error occurred while trying to invoke command.", e);
            }
        }
    }

    /**
     * @return Whether the backend is in debug mode.
     */
    public static boolean debug() {
        return Configuration.get().debug;
    }

    /**
     * Registers all commands, for players and the console.
     */
    private static void registerCommands() {
        // Service Commands
        consoleCommands.addCommand(new RequestCommand());
        consoleCommands.addCommand(new DisconnectCommand());
        consoleCommands.addCommand(new StopCommand());
        consoleCommands.addCommand(new ReloadCommand());
        consoleCommands.addCommand(new RunScriptCommand());
        consoleCommands.addCommand(new SayCommand());
        consoleCommands.addCommand(new PaletteCommand());

        // Player Commands
        playerCommands.addCommand(new DisconnectCommand());
        playerCommands.addCommand(new MoveCommand());
        playerCommands.addCommand(new LocationCommand());
        playerCommands.addCommand(new RotateCommand());
        playerCommands.addCommand(new FollowCommand());
        playerCommands.addCommand(new AttackCommand());
        playerCommands.addCommand(new InventoryCommand());
        playerCommands.addCommand(new BreakCommand());
        playerCommands.addCommand(new PlaceCommand());
        playerCommands.addCommand(new GuardCommand());
        playerCommands.addCommand(new BehaviorCommand());
        playerCommands.addCommand(new DropCommand());
        playerCommands.addCommand(new BlockCommand());
    }

    @Getter private final PacketHandler<?> packetHandler = new PacketHandler<>();

    private AltBackend() {
        super(URI.create("ws://" + Configuration.get().backend.pair()));

        // Register all packet handlers.
        this.getPacketHandler().register(
                ServiceIds._ServiceJoinScRsp,
                (packet) -> {
                    AltBackend.getLogger().info("Backend accepted connection.");
                    AltBackend.getInstance().send(
                            ServiceIds._UpdateSessionsCsNotify,
                            UpdateSessionsCsNotify.newBuilder()
                                    .addAllSessions(PlayerManager.getPlayers().stream()
                                            .map(Player::toProto).toList())
                    );
                },
                null
        );
        PacketHandlers.register(this.getPacketHandler());
    }

    /**
     * Sends a packet to the backend.
     *
     * @param packetId The packet ID.
     * @param packet The packet to send.
     */
    public void send(ServiceIds packetId, GeneratedMessageV3.Builder<?> packet) {
        // Build the packet.
        var builder = Packet.newBuilder()
                .setId(packetId.getNumber());
        if (packet != null)
            builder.setData(packet.build().toByteString());

        this.send(builder.build());
    }

    public void forward(FrontendIds packetId, GeneratedMessageV3.Builder<?> packet) {
        // Build the packet.
        var builder = Packet.newBuilder()
                .setId(packetId.getNumber());
        if (packet != null)
            builder.setData(packet.build().toByteString());

        // Prepare a union command.
        var union = UnionCmdNotify.newBuilder()
                .addPackets(builder.build());
        this.send(ServiceIds._ServiceCmdNotify, union);
    }

    /**
     * Sends a packet to the backend.
     *
     * @param packet The packet to send.
     */
    public void send(Packet packet) {
        this.send(BinaryUtils.base64Encode(packet.toByteArray()));
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        AltBackend.getLogger().info("Established a connection to the backend.");

        var server = Configuration.get().getServer();
        this.send(
                ServiceIds._ServiceJoinCsReq,
                ServiceJoinCsReq.newBuilder()
                        .setServerAddress(server.getAddress())
                        .setServerPort(server.getPort())
        );
    }

    @Override
    public void onMessage(String data) {
        var bytes = BinaryUtils.base64Decode(data);
        var packet = BinaryUtils.decodeFromProto(bytes, Packet.class);
        if (packet == null) {
            AltBackend.getLogger().warn("Received invalid packet from backend.");
            return;
        }

        // Handle the packet.
        var packetId = packet.getId();
        var packetData = packet.getData().toByteArray();

        try {
            this.getPacketHandler().invokeHandler(packetId, packetData);
        } catch (Exception ignored) {
            AltBackend.getLogger().warn("Received invalid packet from backend.");
        }
    }

    @Override
    public void onClose(int code, String msg, boolean clean) {
        AltBackend.getLogger().info("Disconnected from backend.");

        new Thread(() -> {
            // Wait 5s before reconnecting.
            try {
                Thread.sleep(5000);
                this.reconnect();
            } catch (InterruptedException ignored) {
                AltBackend.getLogger().warn("Unable to reconnect.");
            }
        }).start();
    }

    @Override
    public void onError(Exception exception) {
        AltBackend.getLogger().error("An error occurred in connection to the backend.", exception);
    }
}
