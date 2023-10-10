package moe.seikimo.altservice.player.server;

import lombok.Data;
import moe.seikimo.altservice.utils.objects.Location;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityProperties;
import org.cloudburstmc.protocol.bedrock.packet.AddEntityPacket;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

@Data public class ServerEntity {
    /**
     * Creates a new server entity from the specified packet.
     *
     * @param packet The packet.
     * @return The server entity.
     */
    public static ServerEntity from(AddEntityPacket packet) {
        var entity = new ServerEntity(packet.getRuntimeEntityId(),
                packet.getIdentifier());

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
    private final String identifier;

    private EntityProperties properties = new EntityProperties();
    private Location location = Location.ZERO();

    @Nullable private ServerEntity riding;
    private final Set<ServerEntity> passengers = new HashSet<>();

    public ServerEntity(long runtimeId, String identifier) {
        this.runtimeId = runtimeId;
        this.identifier = identifier;
    }

    /**
     * Checks if two entities are equal.
     *
     * @param object The object to check.
     * @return Whether the entities are equal.
     */
    public boolean equals(Object object) {
        return object instanceof ServerEntity entity &&
                entity.getRuntimeId() == this.getRuntimeId();
    }

    /**
     * @return The hash code of the entity.
     */
    @Override
    public int hashCode() {
        return Long.hashCode(this.getRuntimeId());
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

    /**
     * Checks if two entities are related.
     *
     * @param entity The entity to check.
     * @return Whether the entities are related.
     */
    public boolean isRelated(ServerEntity entity) {
        // Check if the entities are equal.
        if (this.equals(entity)) return true;
        // Check if the entity is invalid.
        if (entity == null) return false;
        // Check if the entity is riding this entity.
        if (this.equals(entity.getRiding())) return true;
        // Check if this entity is riding the entity.
        return entity.equals(this.getRiding());
    }
}
