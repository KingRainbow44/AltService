package moe.seikimo.altservice.utils.enums;

import org.cloudburstmc.math.vector.Vector3i;

public enum PositionType {
    RELATIVE, FROM,
    EXACT, AT;

    /**
     * Parses a position from a string.
     *
     * @param type The position type.
     * @param basePosition The base position.
     * @param position The position string.
     * @return The position.
     */
    public static Vector3i parse(
            PositionType type,
            Vector3i basePosition,
            String position
    ) {
        return switch (type) {
            case RELATIVE, FROM -> {
                var newPosition = Vector3i.from(basePosition);

                var split = position.split(" ");
                if (split.length != 3) {
                    throw new IllegalArgumentException("Invalid position.");
                }

                newPosition = newPosition.add(
                        Integer.parseInt(split[0]),
                        Integer.parseInt(split[1]),
                        Integer.parseInt(split[2])
                );

                yield newPosition;
            }
            case EXACT, AT -> Vector3i.from(
                    Integer.parseInt(position.split(" ")[0]),
                    Integer.parseInt(position.split(" ")[1]),
                    Integer.parseInt(position.split(" ")[2])
            );
        };
    }
}
