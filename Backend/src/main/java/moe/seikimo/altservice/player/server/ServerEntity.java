package moe.seikimo.altservice.player.server;

import lombok.Data;
import moe.seikimo.altservice.utils.objects.Location;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityProperties;
import org.cloudburstmc.protocol.bedrock.packet.AddEntityPacket;

@Data public class ServerEntity {
    /**
     * Creates a new server entity from the specified packet.
     *
     * @param packet The packet.
     * @return The server entity.
     */
    public static ServerEntity from(AddEntityPacket packet) {
        var entity = new ServerEntity(packet.getRuntimeEntityId(),
                packet.getEntityType());

        // Set the entity's location.
        entity.setPosition(packet.getPosition());
        entity.setRotation(Vector3f.from(
                packet.getRotation().getX(),
                packet.getRotation().getY(),
                0
        ));

        return entity;
    }

    private final long runtimeId;
    private final int entityType;

    private EntityProperties properties = new EntityProperties();
    private Location location = Location.ZERO();

    public ServerEntity(long runtimeId, int entityType) {
        this.runtimeId = runtimeId;
        this.entityType = entityType;
    }

    /**
     * @return The position of the entity.
     */
    public Vector3f getPosition() {
        return this.getLocation().getPosition();
    }

    /**
     * @return The rotation of the entity.
     */
    public Vector3f getRotation() {
        return this.getLocation().getRotation();
    }

    /**
     * Sets the position of the entity.
     *
     * @param position The position.
     * @return The entity.
     */
    public ServerEntity setPosition(Vector3f position) {
        this.getLocation().setPosition(position);
        return this;
    }

    /**
     * Sets the rotation of the entity.
     *
     * @param rotation The rotation.
     * @return The entity.
     */
    public ServerEntity setRotation(Vector3f rotation) {
        this.getLocation().setRotation(rotation);
        return this;
    }
}
