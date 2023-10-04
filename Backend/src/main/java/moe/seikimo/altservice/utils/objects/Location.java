package moe.seikimo.altservice.utils.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.packet.MoveEntityAbsolutePacket;
import org.cloudburstmc.protocol.bedrock.packet.MoveEntityDeltaPacket;
import org.cloudburstmc.protocol.bedrock.packet.MoveEntityDeltaPacket.Flag;

@Getter
@Setter
@AllArgsConstructor
public final class Location {
    /**
     * @return A location with all values set to zero.
     */
    public static Location ZERO() {
        return new Location(0, Vector3f.ZERO, Vector3f.ZERO, false);
    }

    private int dimension;
    private Vector3f position;
    private Vector3f rotation;
    private boolean grounded;

    @Override
    public String toString() {
        return this.getPosition().toString() + " in dimension " + this.getDimension();
    }

    /**
     * Updates the location from the specified packet.
     *
     * @param packet The packet.
     */
    public void update(MoveEntityDeltaPacket packet) {
        var flags = packet.getFlags();
        if (flags.contains(Flag.HAS_X)) {
            this.getPosition().add(packet.getX(), 0, 0);
        }
        if (flags.contains(Flag.HAS_Y)) {
            this.getPosition().add(0, packet.getY(), 0);
        }
        if (flags.contains(Flag.HAS_Z)) {
            this.getPosition().add(0, 0, packet.getZ());
        }
        if (flags.contains(Flag.HAS_PITCH)) {
            this.getRotation().add(packet.getPitch(), 0, 0);
        }
        if (flags.contains(Flag.HAS_YAW)) {
            this.getRotation().add(0, packet.getYaw(), 0);
        }
        this.setGrounded(flags.contains(Flag.ON_GROUND));
    }

    /**
     * Updates the location from the specified packet.
     *
     * @param packet The packet.
     */
    public void update(MoveEntityAbsolutePacket packet) {
        this.setPosition(packet.getPosition());
        this.setRotation(packet.getRotation());
        this.setGrounded(packet.isOnGround());
    }
}
