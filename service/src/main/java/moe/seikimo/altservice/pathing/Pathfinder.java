package moe.seikimo.altservice.pathing;

import lombok.Data;
import lombok.EqualsAndHashCode;
import moe.seikimo.altservice.player.Player;
import moe.seikimo.altservice.player.server.ServerBlock;
import org.cloudburstmc.math.vector.Vector3i;

import java.util.*;
import java.util.function.Consumer;

@Data
@EqualsAndHashCode(callSuper = true)
/* These are single instance. */
public final class Pathfinder extends Thread {
    private final Player handle;
    private final Vector3i targetPosition;
    private final Consumer<List<Node>> callback;

    private final Map<Vector3i, Node> nodes = new HashMap<>();

    private boolean successful = false;

    /**
     * @return The player's block position. This starts at their feet.
     */
    public Vector3i playerPos() {
        return this.getHandle().getPosition()
                .toInt().sub(0, 1, 0);
    }

    /**
     * Main running method.
     */
    @Override
    public void run() {
        try {
            // Create the initial node.
            var initialNode = new Node(null, this.playerPos(), true);
            this.getNodes().put(initialNode.getPosition(), initialNode);
            // Create the end node.
            var targetNode = new Node(null, this.getTargetPosition(), true);
            this.getNodes().put(targetNode.getPosition(), targetNode);

            var openSet = new ArrayList<Node>();
            var closedSet = new HashSet<Node>();
            openSet.add(initialNode);

            while (!openSet.isEmpty()) {
                var currentNode = openSet.get(0);
                for (var i = 1; i < openSet.size(); i++) {
                    var node = openSet.get(i);
                    if (node.getFCost() < currentNode.getFCost()
                            || node.getFCost() == currentNode.getFCost()
                            && node.getHCost() < currentNode.getHCost()) {
                        currentNode = openSet.get(i);
                    }
                }

                openSet.remove(currentNode);
                closedSet.add(currentNode);

                if (currentNode.equals(targetNode)) {
                    this.retracePath(initialNode, targetNode);
                    break;
                }

                for (var neighbor : this.getNeighbors(currentNode)) {
                    if (!neighbor.isWalkable() || closedSet.contains(neighbor)) continue;

                    var costToNeighbor = currentNode.getGCost()
                            + currentNode.getDistanceTo(neighbor);
                    if (costToNeighbor < neighbor.getGCost() || !openSet.contains(neighbor)) {
                        neighbor.setGCost(costToNeighbor);
                        neighbor.setHCost(neighbor.getDistanceTo(targetNode));
                        neighbor.setParent(currentNode);

                        if (!openSet.contains(neighbor))
                            openSet.add(neighbor);
                    }
                }
            }
        } catch (Exception exception) {
            this.getHandle().getSession().getLogger().info("Pathfinder error: " + exception.getMessage());
        }

        this.getHandle().setPathfinder(null);
        if (!this.isSuccessful()) {
            this.getCallback().accept(List.of());
        }
    }

    /**
     * Computes the nearby nodes.
     *
     * @param node The node.
     * @return The nodes.
     */
    private List<Node> getNeighbors(Node node) {
        var neighbors = new ArrayList<Node>();

        for (var x = -1; x <= 1; x++) {
            for (var y = -1; y <= 1; y++) {
                for (var z = -1; z <= 1; z++) {
                    // Skip the current node.
                    if (x == 0 && y == 0 && z == 0) continue;

                    // Compute the position.
                    var checkX = node.getPosition().getX() + x;
                    var checkY = node.getPosition().getY() + y;
                    var checkZ = node.getPosition().getZ() + z;
                    var checkPos = Vector3i.from(checkX, checkY, checkZ);

                    // Get the node, or compute it.
                    var blockAt = this.getHandle().getWorld().getBlockAt(0, checkPos);
                    var neighbor = this.getNodes().computeIfAbsent(checkPos, k ->
                            new Node(node, checkPos, this.isWalkable(blockAt, checkPos)));

                    // Add the node to the neighbors.
                    neighbors.add(neighbor);
                }
            }
        }

        return neighbors;
    }

    /**
     * Retraces the path from the start node to the end node.
     *
     * @param startNode The start node.
     * @param endNode The end node.
     */
    private void retracePath(Node startNode, Node endNode) {
        var path = new ArrayList<Node>();

        var currentNode = endNode;
        while (currentNode != null && !currentNode.equals(startNode)) {
            path.add(currentNode);
            currentNode = currentNode.getParent();
        }

        Collections.reverse(path);
        this.setSuccessful(true);

        this.getCallback().accept(path);
    }

    /**
     * Considers the context of a block to determine if it is walkable.
     *
     * @param block The block.
     * @param position The block's position.
     * @return Whether the block is walkable.
     */
    @SuppressWarnings("RedundantIfStatement")
    private boolean isWalkable(ServerBlock block, Vector3i position) {
        if (!block.isWalkable()) return false;

        var world = block.getWorld();

        // Check if the block has another block beneath it.
        var blockBelow = world.getBlockAt(0, position.sub(0, 1, 0));
        if (blockBelow.isWalkable()) return false;

        // Check if the block has another block above it.
        var blockAbove = world.getBlockAt(0, position.add(0, 1, 0));
        if (!blockAbove.isWalkable()) return false;

        return true;
    }
}
