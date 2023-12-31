package moe.seikimo.altservice.services;

import moe.seikimo.altservice.AltService;
import moe.seikimo.altservice.client.PanelClient;
import moe.seikimo.altservice.proto.Frontend.FrontendIds;
import moe.seikimo.altservice.proto.Frontend.UpdateSessionsScNotify;
import moe.seikimo.altservice.proto.Service.ServiceIds;
import moe.seikimo.altservice.proto.Service.ServiceJoinCsReq;
import moe.seikimo.altservice.proto.Structures.Player;
import org.java_websocket.WebSocket;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class ServiceManager {
    private static final Map<String, ServiceInstance> instances = new HashMap<>();

    /**
     * Aggregates all sessions from all services.
     *
     * @return The sessions.
     */
    public static Collection<Player> getAllSessions() {
        return instances.values().stream()
                .map(instance -> instance.getSessions().values())
                .flatMap(Collection::stream)
                .map(Session::getHandle)
                .toList();
    }

    /**
     * Invoked when a service requests to connect.
     *
     * @param socket The socket.
     * @param request The request.
     */
    public static void onServiceJoin(WebSocket socket, ServiceJoinCsReq request) {
        var address = request.getServerAddress();
        var port = (short) request.getServerPort();
        var combined = address + ":" + port;
        socket.setAttachment(combined);

        // Check if the service is already registered.
        if (instances.containsKey(combined)) {
            socket.close(); // Close the socket.
            return;
        }

        // Create a new service instance.
        var instance = new ServiceInstance(socket, address, port);
        instances.put(combined, instance);

        // Send back the response packet.
        AltService.send(socket, ServiceIds._ServiceJoinScRsp, null);
        // Additionally request all sessions.
        AltService.send(socket, ServiceIds._GetAllSessionsScReq, null);

        AltService.getLogger().info("Registered service: {}.", instance.getServer());
    }

    /**
     * Removes a service from the manager.
     *
     * @param socket The socket.
     */
    public static void removeService(WebSocket socket) {
        var address = socket.getAttachment();
        if (!(address instanceof String)) return;

        var instance = instances.remove(address);
        if (instance == null) return;

        // Notify all clients that the service has disconnected.
        PanelClient.broadcast(
                FrontendIds._UpdateSessionsScNotify,
                UpdateSessionsScNotify.newBuilder()
                        .addAllSessions(ServiceManager.getAllSessions())
        );

        AltService.getLogger().info("Unregistered service: {}.", instance.getServer());
    }

    /**
     * Fetches a service by the socket.
     *
     * @param socket The socket.
     * @return The service instance.
     */
    public static ServiceInstance getService(WebSocket socket) {
        var address = socket.getAttachment();
        if (address == null) return null;

        return address instanceof String s ? instances.get(s) : null;
    }

    /**
     * Fetches a service by the session ID.
     *
     * @param sessionId The session ID.
     * @return The service instance.
     */
    public static ServiceInstance getService(String sessionId) {
        return instances.values().stream()
                .filter(instance -> instance.hasSession(sessionId) != null)
                .findFirst()
                .orElse(null);
    }
}
