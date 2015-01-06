package ch.k42.metropolis.generator.cuboid;

import ch.n1b.vector.Vec2D;
import ch.n1b.vector.Vec3D;

/**
 * Created on 05.01.2015.
 *
 * @author Thomas
 */
public class Portal {
    private final Vec3D offset;
    private final Vec2D size;
    private final Vec3D normal;

    public Portal(Vec3D offset, Vec3D normal, Vec2D size) {
        this.offset = offset;
        this.size = size;
        this.normal = normal;
    }

    public Vec3D getOffset() {
        return offset;
    }

    public Vec2D getSize() {
        return size;
    }

    public Vec3D getNormal() {
        return normal;
    }
}
