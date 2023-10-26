package moe.seikimo.altservice.pathing;

import lombok.Data;
import org.cloudburstmc.math.vector.Vector3i;
import org.jetbrains.annotations.Nullable;

@Data
public final class Node {
    private final Vector3i position;
    private final boolean walkable;

    @Nullable private Node parent;
    private float hCost, gCost;

    public Node(@Nullable Node parent, Vector3i position, boolean walkable) {
        this.parent = parent;
        this.position = position;
        this.walkable = walkable;
    }

    /**
     * @return The fCost. f(n) = (gCost + hCost)
     */
    public float getFCost() {
        return this.getGCost() + this.getHCost();
    }

    /**
     * Computes the distance to another node.
     *
     * @param other The other node.
     * @return The distance.
     */
    public int getDistanceTo(Node other) {
        var thisPos = this.getPosition();
        var otherPos = other.getPosition();

        var distanceX = Math.abs(thisPos.getX() - otherPos.getX());
        var distanceY = Math.abs(thisPos.getY() - otherPos.getY());
        var distanceZ = Math.abs(thisPos.getZ() - otherPos.getZ());

        if (distanceX > distanceZ) {
            return 14 * distanceZ + 10 * (distanceX - distanceZ) + 10 * distanceY;
        } else {
            return 14 * distanceX + 10 * (distanceZ - distanceX) + 10 * distanceY;
        }
    }

    /**
     * @param other The other object.
     * @return Whether the two objects are equal.
     */
    @Override
    public boolean equals(Object other) {
        return other instanceof Node node &&
                node.getPosition().getX() == this.getPosition().getX() &&
                node.getPosition().getY() == this.getPosition().getY() &&
                node.getPosition().getZ() == this.getPosition().getZ();
    }
}
