package moe.seikimo.altservice;

import com.beust.jcommander.JCommander;
import com.google.protobuf.GeneratedMessageV3;
import lombok.Getter;
import moe.seikimo.altservice.handlers.PacketHandler;
import moe.seikimo.altservice.proto.Service;
import moe.seikimo.altservice.proto.Structures;
import moe.seikimo.altservice.services.ServiceManager;
import moe.seikimo.altservice.utils.BinaryUtils;
import moe.seikimo.altservice.utils.SocketUtils;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Objects;

public final class AltService extends WebSocketServer {
    @Getter
    private static final Logger logger
            = LoggerFactory.getLogger("Alt Service");

    @Getter private static AltService instance;

    static {
        // Set logback configuration file.
        System.setProperty("logback.configurationFile", "logback.xml");
    }

    /**
     * Application entrypoint.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        // Parse command line arguments.
        JCommander.newBuilder()
                .addObject(Arguments.getInstance())
                .build()
                .parse(args);

        // Create an instance of the service.
        AltService.instance = new AltService();
        AltService.getInstance().start();
    }

    @Getter private final PacketHandler packetHandler = new PacketHandler();

    private AltService() {
        super(new InetSocketAddress(
                Arguments.getInstance().getPort()));

        // Register packet handlers.
        this.getPacketHandler().register(
                Service.ServiceIds._ServiceJoinCsReq,
                (WebSocket socket, Service.ServiceJoinCsReq packet) ->
                        ServiceManager.onServiceJoin(socket, packet),
                Service.ServiceJoinCsReq::parseFrom
        );

        // Disable the default connection timeout.
        this.setConnectionLostTimeout(0);
    }

    /**
     * Sends a packet to the backend.
     *
     * @param socket The socket to send the packet to.
     * @param packetId The packet ID.
     * @param packet The packet to send.
     */
    public static void send(
            WebSocket socket,
            Service.ServiceIds packetId,
            GeneratedMessageV3.Builder<?> packet
    ) {
        // Build the packet.
        var builder = Structures.Packet.newBuilder()
                .setId(packetId.getNumber());
        if (packet != null)
            builder.setData(packet.build().toByteString());

        AltService.send(socket, builder.build());
    }

    /**
     * Sends a packet to the backend.
     *
     * @param socket The socket to send the packet to.
     * @param packet The packet to send.
     */
    public static void send(WebSocket socket, Structures.Packet packet) {
        socket.send(BinaryUtils.base64Encode(packet.toByteArray()));
    }

    @Override
    public void onStart() {
        AltService.getLogger().info("Started Alt Service on port {}.",
                this.getAddress().getPort());
    }

    @Override
    public void onOpen(WebSocket socket, ClientHandshake handshake) {
        // Set the socket attachment to the address.
        var address = SocketUtils.getAddress(socket);
        socket.setAttachment(address);

        AltService.getLogger().info("Opened connection to {}.", address);
    }

    @Override
    public void onClose(WebSocket socket, int code, String message, boolean clean) {
        AltService.getLogger().info("Closed connection to {}.",
                SocketUtils.getAddress(socket));
    }

    @Override
    public void onMessage(WebSocket socket, String data) {
        var identifier = socket.getAttachment();
        try {
            var bytes = BinaryUtils.base64Decode(data);
            var packet = BinaryUtils.decodeFromProto(bytes, Structures.Packet.class);
            Objects.requireNonNull(packet, "Packet is null.");

            // Handle the packet.
            var packetId = packet.getId();
            var packetData = packet.getData().toByteArray();

            this.getPacketHandler().invokeHandler(socket, packetId, packetData);
        } catch (Exception ignored) {
            AltService.getLogger().warn("Received invalid packet from {}.",
                    identifier);
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception exception) {
        AltService.getLogger().error("An error occurred in the Alt Service.",
                exception);
    }
}
