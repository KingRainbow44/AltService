package moe.seikimo.altservice.utils.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.cloudburstmc.math.vector.Vector3f;

@Data
@AllArgsConstructor
public final class Location {
    public static final Location ZERO
            = new Location(0, Vector3f.from(0, 0, 0));

    private int dimension;
    private Vector3f position;
}
