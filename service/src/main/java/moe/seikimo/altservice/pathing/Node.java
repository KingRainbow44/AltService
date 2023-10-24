package moe.seikimo.altservice.pathing;

import lombok.Data;
import org.cloudburstmc.math.vector.Vector3i;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

@Data
public final class Node {
    private final Vector3i position;
    private final boolean walkable;

    private final Set<Node> neighbors = new HashSet<>();

    @Nullable private Node parent;
    private float hCost, gCost;

    public Node(@Nullable Node parent, Vector3i position, boolean walkable) {
        this.parent = parent;
        this.position = position;
        this.walkable = walkable;

        this.gCost = this.costToReach();
    }

    /**
     * @return The fCost. f(n) = (gCost + hCost)
     */
    public float fCost() {
        return this.getGCost() + this.getHCost();
    }

    /**
     * Determines the cost to reach this node.
     *
     * @return The cost. (as a distance)
     */
    public float costToReach() {
        var cost = 0f;
        var node = this;
        while (node.getParent() != null) {
            cost += node.computeCost(node.getParent());
            node = node.getParent();
        }

        return cost;
    }

    /**
     * Calculates the cost to the given node.
     *
     * @param node The node.
     * @return The cost. (as a distance)
     */
    public float computeCost(Node node) {
        return this.getPosition().distance(node.getPosition());
    }

    /**
     * Calculates the cost to the given position.
     *
     * @param position The position.
     * @return The cost. (as a distance)
     */
    public float computeCost(Vector3i position) {
        return this.getPosition().distance(position);
    }
}
