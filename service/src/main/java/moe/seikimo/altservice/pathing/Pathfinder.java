package moe.seikimo.altservice.pathing;

import lombok.Data;
import lombok.EqualsAndHashCode;
import moe.seikimo.altservice.player.Player;
import moe.seikimo.altservice.player.server.ServerBlock;
import org.cloudburstmc.math.vector.Vector3i;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Data
@EqualsAndHashCode(callSuper = true)
/* These are single instance. */
public final class Pathfinder extends Thread {
    private final Player handle;
    private final Vector3i targetPosition;

    private final Map<Vector3i, Node> nodes = new HashMap<>();

    /**
     * @return The player's block position.
     */
    public Vector3i playerPos() {
        return this.getHandle().getPosition().toInt();
    }

    /**
     * Main running method.
     */
    @Override
    public void run() {
        // Create the initial node.
        var initialNode = this.createNode(null, this.playerPos(), true);

        var open = new HashSet<>(List.of(initialNode));
        var closed = new HashSet<Node>();

        Node targetNode;
        while (!open.isEmpty()) {
            // Pick the best node.
            targetNode = open.stream()
                    .min(Comparator.comparingDouble(Node::fCost))
                    .get();
            open.remove(targetNode);

            if (targetNode.getPosition() == this.getTargetPosition()) {
                break;
            } else {
                closed.add(targetNode);

                // Compute the neighbors of the node.
                var neighbors = targetNode.getNeighbors();
                for (var neighborNode : neighbors) {
                    if (neighborNode.getGCost() < targetNode.getGCost() && closed.contains(neighborNode)) {
                        neighborNode.setGCost(neighborNode.computeCost(this.getTargetPosition()));
                        targetNode.setParent(neighborNode);
                    } else if (targetNode.getGCost() < neighborNode.getGCost() && open.contains(neighborNode)) {
                        neighborNode.setGCost(neighborNode.computeCost(this.getTargetPosition()));
                        neighborNode.setParent(targetNode);
                    } else if (!open.contains(neighborNode) && !closed.contains(neighborNode)) {
                        open.add(neighborNode);
                        neighborNode.setGCost(neighborNode.computeCost(this.getTargetPosition()));
                    }
                }
            }
        }

        this.getHandle().sendMessage("Path found!");
    }

    /**
     * Creates a node at the given position.
     *
     * @param parent The parent node.
     * @param position The position.
     * @return The node.
     */
    private Node createNode(@Nullable Node parent, Vector3i position, boolean walkable) {
        var node = new Node(parent, position, walkable);

        // Attempt to calculate the hCost.
        node.setHCost(node.computeCost(this.getTargetPosition()));
        // Compute the neighbors of the node.
        for (var x = -1; x <= 1; x++) {
            for (var y = -1; y <= 1; y++) {
                for (var z = -1; z <= 1; z++) {
                    // Check if the node exists in the world.
                    var neighborPos = position.add(x, y, z);
                    if (!this.getNodes().containsKey(neighborPos)) {
                        // Create a node for the position.
                        var blockAt = this.getHandle().getWorld().getBlockAt(0, neighborPos);
                        var neighbor = this.createNode(node, neighborPos, this.isWalkable(blockAt));
                        this.getNodes().put(neighborPos, neighbor);
                    }

                    node.getNeighbors().add(this.getNodes().get(neighborPos));
                }
            }
        }

        return node;
    }

    /**
     * Considers the context of a block to determine if it is walkable.
     *
     * @param block The block.
     * @return Whether the block is walkable.
     */
    @SuppressWarnings("RedundantIfStatement")
    private boolean isWalkable(ServerBlock block) {
        if (!block.isWalkable()) return false;

        var world = block.getWorld();
        var position = block.getLocation();

        // Check if the block has another block beneath it.
        var blockBelow = world.getBlockAt(0, position.add(0, -1, 0));
        if (!blockBelow.isWalkable()) return false;

        // Check if the block has another block above it.
        var blockAbove = world.getBlockAt(0, position.add(0, 1, 0));
        if (!blockAbove.isWalkable()) return false;

        return true;
    }
}
