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
            = new Location(0, Vector3f.from(0, 0, 0));

    private int dimension;
    private Vector3f position;

    @Override
    public String toString() {
        return this.getPosition().toString() + " in dimension " + this.getDimension();
    }
}
