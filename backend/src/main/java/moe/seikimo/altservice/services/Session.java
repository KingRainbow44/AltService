package moe.seikimo.altservice.services;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.ProtocolMessageEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import moe.seikimo.altservice.AltService;
import moe.seikimo.altservice.proto.Service;
import moe.seikimo.altservice.proto.Service.ServiceCmdNotify;
import moe.seikimo.altservice.proto.Structures;
import moe.seikimo.altservice.proto.Structures.Player;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public final class Session {
    private final ServiceInstance instance;
    private final String id;

    @Setter @NotNull
    private Player handle;

    /**
     * Updates the session with the new player data.
     *
     * @param player The new player data.
     */
    public void updateSession(Player player) {
        this.setHandle(player);
    }

    /**
     * Sends a packet to be handled by the session.
     *
     * @param id The packet ID.
     * @param packet The packet.
     */
    public void send(Object id, GeneratedMessageV3 packet) {
        var encodedPacket = Structures.Packet.newBuilder()
                .setId(id instanceof ProtocolMessageEnum protoId ?
                        protoId.getNumber() : (int) id)
                .setData(packet.toByteString());
        var servicePacket = ServiceCmdNotify.newBuilder()
                .setTarget(this.getHandle())
                .setPacket(encodedPacket);

        AltService.send(this.getInstance().getSession(),
                Service.ServiceIds._ServiceCmdNotify, servicePacket);
    }
}
