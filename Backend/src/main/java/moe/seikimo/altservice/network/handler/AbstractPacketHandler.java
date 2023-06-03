package moe.seikimo.altservice.network.handler;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import moe.seikimo.altservice.Configuration;
import moe.seikimo.altservice.network.PlayerNetworkSession;
import moe.seikimo.altservice.player.Player;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketHandler;
import org.slf4j.Logger;

@Data
@RequiredArgsConstructor
public class AbstractPacketHandler implements BedrockPacketHandler {
    protected final PlayerNetworkSession session;

    /**
     * @return The session's logger.
     */
    protected Logger getLogger() {
        return this.getSession().getLogger();
    }

    /**
     * @return The session's player.
     */
    protected Player getPlayer() {
        return this.getSession().getPlayer();
    }

    /**
     * Logs a packet to console if logging is enabled.
     * @param packet Packet that was received.
     */
    private void logPacket(BedrockPacket packet) {
        if (Configuration.get().isDebug())
            this.getLogger().debug("Received packet: {}",
                    packet.getClass().getSimpleName());
    }
}
