package moe.seikimo.altservice.utils.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.cloudburstmc.math.vector.Vector3f;

@Getter
@Setter
@AllArgsConstructor
public final class Location {
    public static final Location ZERO
            = new Location(0, Vector3f.ZERO, Vector3f.ZERO);

    private int dimension;
    private Vector3f position;
    private Vector3f rotation;

    @Override
    public String toString() {
        return this.getPosition().toString() + " in dimension " + this.getDimension();
    }
}
