package moe.seikimo.altservice.services;

import moe.seikimo.altservice.AltService;
import moe.seikimo.altservice.proto.Service.ServiceIds;
import moe.seikimo.altservice.proto.Service.ServiceJoinCsReq;
import org.java_websocket.WebSocket;

import java.util.HashMap;
import java.util.Map;

public final class ServiceManager {
    private static final Map<String, ServiceInstance> instances = new HashMap<>();

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
        var instance = new ServiceInstance(address, port);
        instances.put(combined, instance);

        // Send back the response packet.
        AltService.send(socket, ServiceIds._ServiceJoinScRsp, null);
        // Additionally request all sessions.
        AltService.send(socket, ServiceIds._GetAllSessionsScReq, null);

        AltService.getLogger().info("Registered service: {}.", instance.getServer());
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
}
